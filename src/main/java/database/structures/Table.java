package database.structures;

import database.btree.BTree;
import database.btree.Entry;
import database.exception.FieldNumberMismatchException;
import database.exception.NotNullFieldNotInsideInsertException;
import database.exception.TypeMismatchException;
import database.exception.UnknownFieldException;
import database.field.Field;
import database.field.Fields;
import database.structures.value.BigSerialDefaultValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.ast.arithmetic.AstArithExpr;
import parser.ast.arithmetic.AstArithExprIdentConstant;
import parser.ast.arithmetic.AstArithExprValue;
import parser.ast.condition.*;
import parser.ast.function.data.AstInsertRow;
import parser.ast.name.AstFieldName;
import parser.ast.name.AstFieldReference;
import parser.ast.value.AstValue;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private static final Logger log = LoggerFactory.getLogger(Table.class.getName());
    private static final int T = 10;

    private BTree table;
    private List<Index> indices;
    private String tableName;
    private String schemaName;
    private int numberOfFields;
    private List<TableFieldInformation> fieldInformation;
    private BigSerialDefaultValue surrogate;

    public Table(String tableName, String schemaName, List<TableFieldInformation> fieldInformation) {
        this.numberOfFields = fieldInformation.size();
        table = new BTree(T, numberOfFields);
        indices = new ArrayList<>();
        this.tableName = tableName;
        this.schemaName = schemaName;
        this.fieldInformation = fieldInformation;
        this.surrogate = new BigSerialDefaultValue();

        for (TableFieldInformation tableFieldInformation : fieldInformation) {
            if (tableFieldInformation.isNeedsIndex()) {
                indices.add(new Index()); //todo
                tableFieldInformation.setIndexPosition(indices.size() - 1);
            }
        }
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

    public void insertValues(List<AstFieldName> columnList, List<AstInsertRow> rowList) throws UnknownFieldException, FieldNumberMismatchException, TypeMismatchException, NotNullFieldNotInsideInsertException {
        assertColumnList(columnList);

        for (AstInsertRow insertRow : rowList) {
            if (insertRow.getValueList().size() != columnList.size()) {
                throw new FieldNumberMismatchException();
            }
            Field[] forInsertValues = new Field[fieldInformation.size()];
            Field forInsertKey = null;

            for (int i = 0; i < fieldInformation.size(); i++) {
                if (fieldInformation.get(i).isPresentInInsert()) {
                    if (fieldInformation.get(i).isPrimary()) {
                        forInsertKey = Fields.astValueToField(fieldInformation.get(i).getFieldType(), insertRow.getValueList().get(fieldInformation.get(i).getInsertPosition()));
                    } else {
                        forInsertValues[i] = Fields.astValueToField(fieldInformation.get(i).getFieldType(), insertRow.getValueList().get(fieldInformation.get(i).getInsertPosition()));
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
                            forInsertValues[i] = fieldInformation.get(i).getDefaultValue();
                        }
                    }
                }
            }

            if (forInsertKey == null) {
                forInsertKey = surrogate.getNext();
            }
            table.insert(forInsertKey, forInsertValues);
        }

        for (TableFieldInformation tableFieldInformation : fieldInformation) {
            tableFieldInformation.setPresentInInsert(false);
            tableFieldInformation.setInsertPosition(0);
        }
    }

    public List<SelectOutputRow> selectValue(List<AstFieldName> columnList, AstCondition condition) throws TypeMismatchException, UnknownFieldException {
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

    public Entry selectEqPrKey(List<AstFieldName> columnList, AstCondition condition) throws TypeMismatchException {
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

//    public Entry searchEq() {
//
//    }

    //todo
}
