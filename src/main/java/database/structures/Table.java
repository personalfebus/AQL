package database.structures;

import database.btree.BTree;
import database.exception.FieldNumberMismatchException;
import database.exception.NotNullFieldNotInsideInsertException;
import database.exception.TypeMismatchException;
import database.exception.UnknownFieldException;
import database.field.Field;
import database.field.Fields;
import database.structures.value.BigSerialDefaultValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.ast.function.data.AstInsertRow;
import parser.ast.name.AstFieldName;

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
        List<Integer> positionConverter = new ArrayList<>();
        int j = 0;

        for (AstFieldName fieldName : columnList) {
            boolean isPresent = false;
            int i = 0;

            for (TableFieldInformation tableFieldInformation : fieldInformation) {
                if (fieldName.getName().equals(tableFieldInformation.getFieldName())) {
                    tableFieldInformation.setPresentInInsert(true);
                    tableFieldInformation.setInsertPosition(j);
                    positionConverter.add(i);
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
                        if (fieldInformation.get(i).isPrimary() && !fieldInformation.get(i).hasFieldDefaultValue()) {
                            throw new NotNullFieldNotInsideInsertException(i);
                        }
                        forInsertValues[i] = fieldInformation.get(i).getDefaultValue();
                    }
                }
            }

            if (forInsertKey == null) {
                forInsertKey = surrogate.getNext();
            }
            table.insert(forInsertKey, forInsertValues);
        }
    }


//    public Entry searchEq() {
//
//    }

    //todo
}
