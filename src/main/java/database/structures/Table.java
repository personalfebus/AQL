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
import database.exception.UnsupportedOperationException;
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
import parser.ast.function.data.AstUpdateValue;
import parser.ast.name.AstFieldName;
import parser.ast.value.AstValue;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        boolean hasPrimary = false;
        int j = 0;
        for (TableFieldInformation tableFieldInformation : fieldInformation) {
            if (tableFieldInformation.isPrimary()) {
                hasPrimary = true;
                tableFieldInformation.setTreePosition(-1);
            } else if (tableFieldInformation.isNeedsIndex()) {
                indices.add(new Index(tableFieldInformation, tableFieldInformation.getFieldName()));
                tableFieldInformation.setIndexPosition(indices.size() - 1);
                tableFieldInformation.setTreePosition(hasPrimary ? j - 1 : j);
            } else if (tableFieldInformation.isForeign()) {
                mappers.add(new Index(tableFieldInformation, tableFieldInformation.getFieldName()));
                tableFieldInformation.setMapperPosition(mappers.size() - 1);
                if (database.hasTableByName(tableFieldInformation.getReferenceSchemaName(), tableFieldInformation.getReferencedTableName())) {
                    boolean flag = database.getTableByName(tableFieldInformation.getReferenceSchemaName(), tableFieldInformation.getReferencedTableName()).assertForeignField(tableFieldInformation);
                    if (!flag) {
                        throw new BadForeignKeyException();
                    }
                } else {
                    throw new BadForeignKeyException();
                }
                tableFieldInformation.setTreePosition(hasPrimary ? j - 1 : j);
            } else {
                tableFieldInformation.setTreePosition(hasPrimary ? j - 1 : j);
            }

            j++;
        }

        if (hasPrimary) {
            this.surrogate = null;
        } else {
            this.surrogate = new BigSerialDefaultValue();
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
            Field[] forInsertValues = new Field[surrogate == null ? fieldInformation.size() - 1 : fieldInformation.size()];
            Field forInsertKey = null;
            List<Integer> indexed = new ArrayList<>();
            List<Integer> mapped = new ArrayList<>();

            for (int i = 0; i < fieldInformation.size(); i++) {
                if (fieldInformation.get(i).isPresentInInsert()) {
                    if (fieldInformation.get(i).isPrimary()) {
                        forInsertKey = Fields.astValueToField(fieldInformation.get(i).getFieldType(), insertRow.getValueList().get(fieldInformation.get(i).getInsertPosition()));
                    } else {
                        forInsertValues[fieldInformation.get(i).getTreePosition()] = Fields.astValueToField(fieldInformation.get(i).getFieldType(), insertRow.getValueList().get(fieldInformation.get(i).getInsertPosition()));
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
                                forInsertValues[fieldInformation.get(i).getTreePosition()] = fieldInformation.get(i).getDefaultValue();
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
                indices.get(fieldInformation.get(pos).getIndexPosition()).insert(forInsertValues[fieldInformation.get(pos).getTreePosition()], forInsertKey);
            }

            for (int pos : mapped) {
                //todo get ref ind from another table
                mappers.get(fieldInformation.get(pos).getMapperPosition()).insert(forInsertKey, forInsertValues[fieldInformation.get(pos).getTreePosition()]);
            }
            table.insert(forInsertKey, forInsertValues);
        }

        for (TableFieldInformation tableFieldInformation : fieldInformation) {
            tableFieldInformation.setPresentInInsert(false);
            tableFieldInformation.setInsertPosition(0);
        }
    }

    public List<SelectOutputRow> select(List<AstFieldName> columnList, AstCondition condition) throws TypeMismatchException, UnknownFieldException, ReadFromDiskError, UnsupportedOperationException {
        List<SelectOutputRow> result = new ArrayList<>();

        assertColumnList(columnList);

        for (int i = 0; i < condition.getParts().size(); i += 3) {
            int fieldsUsed = condition.getFieldCount();
            Set<Entry> entrySet = new HashSet<>();

            if (fieldsUsed == 1) {
                TableFieldInformation field = findFieldInformationByName(condition.getFieldNames().get(0));

            } else {
                throw new UnsupportedOperationException();
            }

            selectByPrimaryKey(columnList, condition, entrySet);

            for (Entry entry : entrySet) {
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
        }

        return result;
    }

    public void selectByPrimaryKey(List<AstFieldName> columnList, AstCondition condition, Set<Entry> result) throws TypeMismatchException, ReadFromDiskError, UnsupportedOperationException {
        String fieldName = "";
        Field key = null;
        String op = "";

        for (AstConditionPart part : condition.getParts()) {
            if (part.getType().equals(AstConditionParts.astConditionConstantRValueType)) {
                AstArithExpr expr = ((AstConditionConstantVariable) part).getArithExpr();
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

        if (key == null || op == null) {
            return;
        }

        switch (op) {
            case "=": {
                result.add(table.getEntryByKey(key));
                return;
            }
            case ">": {
                table.getEntriesByKeyGR(key, result);
                return;
            }
            case "<": {
                table.getEntriesByKeyLR(key, result);
                return;
            }
            case ">=": {
                table.getEntriesByKeyGE(key, result);
                return;
            }
            case "<=": {
                table.getEntriesByKeyLE(key, result);
                return;
            }
            case "!=": {
                table.getEntriesByKeyNE(key, result);
                return;
            }
            default: throw new UnsupportedOperationException();
        }
    }

    public void delete(AstCondition condition) throws UnsupportedOperationException, WriteToDiskError, ReadFromDiskError, TypeMismatchException {
        for (int i = 0; i < condition.getParts().size(); i += 3) {
            int fieldsUsed = condition.getFieldCount();

            if (fieldsUsed == 1) {
                TableFieldInformation field = findFieldInformationByName(condition.getFieldNames().get(0));

            } else {
                throw new UnsupportedOperationException();
            }

            deleteByPrimaryKey(condition);
        }
    }

    public void deleteByPrimaryKey(AstCondition condition) throws UnsupportedOperationException, WriteToDiskError, ReadFromDiskError, TypeMismatchException {
        Set<Entry> result = new HashSet<>();
        String fieldName = "";
        Field key = null;
        String op = "";

        for (AstConditionPart part : condition.getParts()) {
            if (part.getType().equals(AstConditionParts.astConditionConstantRValueType)) {
                AstArithExpr expr = ((AstConditionConstantVariable) part).getArithExpr();
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

        if (key == null || op == null) {
            return;
        }

        switch (op) {
            case "=": {
                result.add(table.getEntryByKey(key));
                break;
            }
            case ">": {
                table.getEntriesByKeyGR(key, result);
                break;
            }
            case "<": {
                table.getEntriesByKeyLR(key, result);
                break;
            }
            case ">=": {
                table.getEntriesByKeyGE(key, result);
                break;
            }
            case "<=": {
                table.getEntriesByKeyLE(key, result);
                break;
            }
            case "!=": {
                table.getEntriesByKeyNE(key, result);
                break;
            }
            default: throw new UnsupportedOperationException();
        }

        for (Entry entry : result) {
            table.remove(entry.getKey());
        }
    }

    public void update(List<AstUpdateValue> updateValues, AstCondition condition) throws UnsupportedOperationException, UnknownFieldException, WriteToDiskError, TypeMismatchException, ReadFromDiskError {
        for (int i = 0; i < condition.getParts().size(); i += 3) {
            int fieldsUsed = condition.getFieldCount();

            if (fieldsUsed == 1) {
                TableFieldInformation field = findFieldInformationByName(condition.getFieldNames().get(0));

            } else {
                throw new UnsupportedOperationException();
            }

            updateByPrimaryKey(updateValues, condition);
        }
    }

    public void updateByPrimaryKey(List<AstUpdateValue> updateValues, AstCondition condition) throws TypeMismatchException, ReadFromDiskError, UnsupportedOperationException, WriteToDiskError, UnknownFieldException {
        Set<Entry> forUpdate = new HashSet<>();

        selectByPrimaryKey(null, condition, forUpdate);

        for (Entry entry : forUpdate) {
            int j = 0;
            for (AstUpdateValue updateValue : updateValues) {
                TableFieldInformation tableFieldInformation = findFieldInformationByName(updateValue.getFieldName().getName());

                if (tableFieldInformation == null) {
                    throw new UnknownFieldException(j);
                }

                int pos = tableFieldInformation.getTreePosition();
                if (pos < 0) {
                    entry.setKey(Fields.astValueToField(tableFieldInformation.getFieldType(), updateValue.getValue()));
                }
                entry.getValues()[pos] = Fields.astValueToField(tableFieldInformation.getFieldType(), updateValue.getValue());
                j++;
            }
        }

        deleteByPrimaryKey(condition);

        for (Entry entry : forUpdate) {
            table.insert(entry.getKey(), entry.getValues());
        }
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

    public boolean hasIndexByName(String indexName) {
        boolean isPresent = false;

        for (Index index : indices) {
            if (index.getName().equals(indexName)) {
                isPresent = true;
                break;
            }
        }

        return isPresent;
    }

    public Index getIndexByName(String indexName) {
        for (Index index : indices) {
            if (index.getName().equals(indexName)) {
                return index;
            }
        }

        return null;
    }

    public void removeIndexByName(String indexName) {
        for (int i = 0; i < indices.size(); i++) {
            if (indices.get(i).getName().equals(indexName)) {
                indices.remove(i);
                break;
            }
        }
    }

    public void addIndex(String fieldName, String name) {
        TableFieldInformation tableFieldInformation = findFieldInformationByName(fieldName);

        if (!tableFieldInformation.hasIndex()) {
            indices.add(new Index(tableFieldInformation, tableFieldInformation.getFieldName()));
            tableFieldInformation.setIndexPosition(indices.size() - 1);
        }
    }

    public TableFieldInformation findFieldInformationByName(String name) {
        for (TableFieldInformation tableFieldInformation : fieldInformation) {
            if (tableFieldInformation.getFieldName().equals(name)) {
                return tableFieldInformation;
            }
        }

        return null;
    }


    private Map<String, Integer> FieldNameToPosition() {
        Map<String, Integer> result = new HashMap<>();

        int i = 0;
        for (TableFieldInformation tableFieldInformation : fieldInformation) {
            if (tableFieldInformation.isPrimary()) {
                result.put(tableFieldInformation.getFieldName(), -1);
            } else {
                result.put(tableFieldInformation.getFieldName(), i);
            }
            i++;
        }

        return result;
    }

//    public Entry searchEq() {
//
//    }

    //todo
}
