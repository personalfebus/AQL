package database.structures;

import database.Database;
import database.btree.BTree;
import database.btree.Entry;
import database.btree.exception.ReadFromDiskError;
import database.btree.exception.WriteToDiskError;
import database.exception.BadForeignKeyException;
import database.exception.FieldNumberMismatchException;
import database.exception.NotNullFieldNotInsideInsertException;
import database.exception.TableCreationException;
import database.exception.TypeMismatchException;
import database.exception.UnknownFieldException;
import database.field.Field;
import database.field.Fields;
import database.structures.value.BigSerialDefaultValue;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import parser.ast.arithmetic.AstArithExpr;
import parser.ast.arithmetic.AstArithExprIdentConstant;
import parser.ast.arithmetic.AstArithExprValue;
import parser.ast.condition.*;
import parser.ast.function.data.AstInsertRow;
import parser.ast.name.AstFieldName;
import parser.ast.value.AstValue;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class Table implements Serializable {
    private final static String pathPrefix = "binaries/";
    private static final int T = 10;

    private final UUID databaseUuid;
    private final UUID tableUuid;

    private BTree table;
    //unique indices
    private List<Index> indices;
    //foreign mappers
    private List<Index> mappers;
    private String tableName;
    private String schemaName;
    private int numberOfFields;
    private List<TableFieldInformation> fieldInformation;
    private BigSerialDefaultValue surrogate;

    public Table(UUID databaseUuid, String tableName, String schemaName, List<TableFieldInformation> fieldInformation, Database database) throws WriteToDiskError, TableCreationException {
        this.tableUuid = UUID.randomUUID();
        this.databaseUuid = databaseUuid;
        this.numberOfFields = fieldInformation.size();
        this.table = new BTree(T, numberOfFields);
        this.indices = new ArrayList<>();
        this.mappers = new ArrayList<>();
        this.tableName = tableName;
        this.schemaName = schemaName;
        this.fieldInformation = fieldInformation;
        this.surrogate = new BigSerialDefaultValue();

        for (TableFieldInformation tableFieldInformation : fieldInformation) {
            if (tableFieldInformation.isNeedsIndex()) {
                indices.add(new Index(tableFieldInformation));
                tableFieldInformation.setIndexPosition(indices.size() - 1);
            } else if (tableFieldInformation.isForeign()) {
                mappers.add(new Index(tableFieldInformation));
                tableFieldInformation.setMapperPosition(mappers.size() - 1);
                if (database.hasTableByName(tableFieldInformation.getReferenceSchemaName(), tableFieldInformation.getReferencedTableName())) {
                    boolean flag = database.getTableByName(tableFieldInformation.getReferenceSchemaName(), tableFieldInformation.getReferencedTableName()).assertForeignField(tableFieldInformation);
                    if (!flag) {
                        throw new BadForeignKeyException();
                    }
                } else {
                    throw new BadForeignKeyException();
                }
            }
        }
    }

    public static Table readFromDisk(UUID uuid) throws ReadFromDiskError {
        try {
            @Cleanup FileInputStream fileInputStream = new FileInputStream(pathPrefix + uuid.toString());
            @Cleanup ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (Table) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new ReadFromDiskError(e);
        }
    }

    public static void writeToDisk(UUID uuid, Table table) throws WriteToDiskError {
        try {
            @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(pathPrefix + uuid.toString());
            @Cleanup ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(table);
        } catch (IOException e) {
            throw new WriteToDiskError(e);
        }
    }

    public void delete() throws ReadFromDiskError {
        table.deleteAll();
    }

    public String getTableName() {
        return tableName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public int getNumberOfFields() {
        return numberOfFields;
    }

    public void insertValues(List<AstFieldName> columnList, List<AstInsertRow> rowList) throws UnknownFieldException, FieldNumberMismatchException, TypeMismatchException, NotNullFieldNotInsideInsertException, WriteToDiskError, ReadFromDiskError {
        assertColumnList(columnList);

        for (AstInsertRow insertRow : rowList) {
            if (insertRow.getValueList().size() != columnList.size()) {
                throw new FieldNumberMismatchException();
            }
            Field[] forInsertValues = new Field[fieldInformation.size()];
            Field forInsertKey = null;
            List<Integer> indexed = new ArrayList<>();
            List<Integer> mapped = new ArrayList<>();

            for (int i = 0; i < fieldInformation.size(); i++) {
                if (fieldInformation.get(i).isPresentInInsert()) {
                    if (fieldInformation.get(i).isPrimary()) {
                        forInsertKey = Fields.astValueToField(fieldInformation.get(i).getFieldType(), insertRow.getValueList().get(fieldInformation.get(i).getInsertPosition()));
                    } else {
                        forInsertValues[i] = Fields.astValueToField(fieldInformation.get(i).getFieldType(), insertRow.getValueList().get(fieldInformation.get(i).getInsertPosition()));
                        if (fieldInformation.get(i).hasIndex()) {
                            indexed.add(i);
                        } else if (fieldInformation.get(i).isForeign()) {
                            mapped.add(i);
                        }
                    }
                } else {
                    if (fieldInformation.get(i).isNotNull()) {
                        throw new NotNullFieldNotInsideInsertException(i);
                    } else {
                        if (fieldInformation.get(i).isPrimary()) {
                            if (!fieldInformation.get(i).hasFieldDefaultValue()) {
                                throw new NotNullFieldNotInsideInsertException(i);
                            } else {
                                forInsertKey = fieldInformation.get(i).getDefaultValue();
                            }
                        } else {
                            if (!fieldInformation.get(i).hasFieldDefaultValue()) {
                                throw new NotNullFieldNotInsideInsertException(i);
                            } else {
                                forInsertValues[i] = fieldInformation.get(i).getDefaultValue();
                                if (fieldInformation.get(i).hasIndex()) {
                                    indexed.add(i);
                                } else if (fieldInformation.get(i).isForeign()) {
                                    mapped.add(i);
                                }
                            }
                        }
                    }
                }
            }

            if (forInsertKey == null) {
                forInsertKey = surrogate.getNext();
            }

            for (int pos : indexed) {
                indices.get(fieldInformation.get(pos).getIndexPosition()).insert(forInsertValues[pos], forInsertKey);
            }

            for (int pos : mapped) {
                //todo get ref ind
//                mappers.get(fieldInformation.get(pos).getMapperPosition()).insert();
            }
            table.insert(forInsertKey, forInsertValues);
        }

        for (TableFieldInformation tableFieldInformation : fieldInformation) {
            tableFieldInformation.setPresentInInsert(false);
            tableFieldInformation.setInsertPosition(0);
        }
    }

    public List<SelectOutputRow> selectValue(List<AstFieldName> columnList, AstCondition condition) throws TypeMismatchException, UnknownFieldException, ReadFromDiskError {
        List<SelectOutputRow> result = new ArrayList<>();

        assertColumnList(columnList);

        for (int i = 0; i < condition.getParts().size(); i += 3) {
            Entry entry = selectEqPrKey(columnList, condition);
            Field[] fields = new Field[columnList.size()];

            int j = 0;
            for (TableFieldInformation inf : fieldInformation) {
                if (inf.isPresentInInsert()) {
                    fields[inf.getInsertPosition()] = (j == 0 ? entry.getKey() : entry.getValues()[j - 1]);
                }

                j++;
            }
            SelectOutputRow row = new SelectOutputRow(fields);
            result.add(row);
        }

        return result;
    }

    public Entry selectEqPrKey(List<AstFieldName> columnList, AstCondition condition) throws TypeMismatchException, ReadFromDiskError {
        String fieldName = "";
        Field key = null;
        String op = "";

        for (AstConditionPart part : condition.getParts()) {
            if (part.getType().equals(AstConditionParts.astConditionConstantRValueType)) {
                AstArithExpr expr = ((AstConditionConstantRValue) part).getArithExpr();
                if (expr.getParts().get(0).getType().equals(AstArithExprIdentConstant.class.getName())) {
                    AstArithExprIdentConstant constant = (AstArithExprIdentConstant) expr.getParts().get(0);
                    fieldName = constant.getFieldName().getFieldName().getName();
                } else if (expr.getParts().get(0).getType().equals(AstArithExprValue.class.getName())) {
                    AstArithExprValue value = (AstArithExprValue) expr.getParts().get(0);
                    key = Fields.astValueToField("int", value.getValue());
                }
            } else if (part.getType().equals(AstConditionParts.astConditionConstantType)) {
                AstValue value = ((AstConditionConstant) part).getValue();
                key = Fields.astValueToField("int", value);
            } else if (part.getType().equals(AstConditionParts.astConditionOperatorType)) {
                AstConditionOperator operator = (AstConditionOperator) part;
                op = operator.getOperator();
            }
        }

        if (key == null) {
            return null;
        }

        return table.getEntryByKey(key);
    }

    private void assertColumnList(List<AstFieldName> columnList) throws UnknownFieldException {
        int j = 0;

        for (AstFieldName fieldName : columnList) {
            boolean isPresent = false;
            int i = 0;

            for (TableFieldInformation tableFieldInformation : fieldInformation) {
                if (fieldName.getName().equals(tableFieldInformation.getFieldName())) {
                    tableFieldInformation.setPresentInInsert(true);
                    tableFieldInformation.setInsertPosition(j);
                    isPresent = true;
                    break;
                }

                i++;
            }

            if (!isPresent) {
                throw new UnknownFieldException(i);
            }

            j++;
        }
    }

    public boolean assertForeignField(TableFieldInformation ref) {
        boolean isCorrect = false;

        for (TableFieldInformation cur : fieldInformation) {
            if (ref.getFieldName().equals(cur.getFieldName())) {
                if (ref.getFieldType().equals(cur.getFieldType())) {
                    isCorrect = true;
                }
                break;
            }
        }

        return isCorrect;
    }

//    public Entry searchEq() {
//
//    }

    //todo
}
