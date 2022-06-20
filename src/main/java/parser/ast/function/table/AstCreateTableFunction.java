package parser.ast.function.table;

import database.Database;
import database.btree.exception.WriteToDiskError;
import database.exception.TableCreationException;
import database.structures.Table;
import database.structures.TableFieldInformation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.ast.function.AstFunction;
import parser.ast.function.AstColumnDefinition;
import parser.ast.name.AstTableName;
import database.exception.TypeMismatchException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AstCreateTableFunction implements AstFunction {
    private final boolean hasNotExistPrefix;
    private final AstTableName tableName;
    private final List<AstColumnDefinition> columnDefinitionList;

    public AstCreateTableFunction(boolean hasNotExistPrefix, AstTableName tableName, List<AstColumnDefinition> columnDefinitionList) {
        this.hasNotExistPrefix = hasNotExistPrefix;
        this.tableName = tableName;
        this.columnDefinitionList = columnDefinitionList;
    }

    public boolean isHasNotExistPrefix() {
        return hasNotExistPrefix;
    }

    public List<AstColumnDefinition> getColumnDefinitionList() {
        return columnDefinitionList;
    }

    public void addColumnDefinition(AstColumnDefinition columnDefinition) {
        columnDefinitionList.add(columnDefinition);
    }

    @Override
    public String getType() {
        return AstCreateTableFunction.class.getName();
    }

    @Override
    public void execute(Database database) {
        boolean isPresent = database.hasTableByName(tableName.getSchemaName(), tableName.getTableName());

        if (isPresent) {
            if (hasNotExistPrefix) {
                log.info("Table already exists");
            } else {
                log.error("Table already exists");
            }
        } else {
            List<TableFieldInformation> tableFieldInformationList = new ArrayList<>();

            for (AstColumnDefinition columnDefinition : columnDefinitionList) {
                try {
                    tableFieldInformationList.add(columnDefinition.getInformation());
                } catch (TypeMismatchException e) {
                    log.error("Error while creating field for new table", e);
                }
            }

            try {
                database.addTable(new Table(database.getDatabaseUuid(), tableName.getTableName(), tableName.getSchemaName(), tableFieldInformationList, database));
                Database.writeToDisk(database.getDatabaseUuid(), database, database.getPath());
                log.info("Table created successfully {}", tableName);
            } catch (WriteToDiskError e) {
                log.error("Internal filesystem error occurred during command execution", e);
            } catch (TableCreationException e) {
                log.error("Bad table creation operation", e);
            }

        }
    }
}
