package usa.synergy.utilities.service.sql;

import java.sql.SQLException;
import java.util.HashMap;
import usa.synergy.utilities.service.SQLService;

public class TableBuilder {

  private final String tableName;
  private String query;
  private String query_update = "";
  private String specs;
  private int columns;
  private final DatabaseManager databaseManager;

  public TableBuilder(String tableName, DatabaseManager databaseManager) {
    this.tableName = tableName;
    this.databaseManager = databaseManager;

    this.query = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
  }

  public TableBuilder addColumn(String name, SQLDataType type, int amount, boolean allowNull,
      SQLDefaultType defaultType, boolean primary) {
    return addColumn(name, type, amount > 0 ? Integer.toString(amount) : null, allowNull,
        defaultType, primary);
  }

  public TableBuilder addColumn(String name, SQLDataType type, String amount, boolean allowNull,
      SQLDefaultType defaultType, boolean primary) {
    {
      specs = "`" + name + "` " + type;
      if (amount != null) {
        specs += "(" + amount + ")";
      }
      if (!allowNull) {
        specs += " NOT NULL";
      }
      if (defaultType == SQLDefaultType.CUSTOM) {
        if (defaultType.getDefaultObject()[0] instanceof Enum
            || defaultType.getDefaultObject()[0] instanceof String) {
          specs += " DEFAULT '" + defaultType.getDefaultObject()[0] + "'";
        } else {
          specs += " DEFAULT " + defaultType.getDefaultObject()[0];
        }
      } else if (defaultType == SQLDefaultType.AUTO_INCREMENT) {
        specs += " AUTO_INCREMENT";
      } else if (defaultType == SQLDefaultType.NULL) {
        specs += " DEFAULT NULL";
      }
      if (primary) {
        specs += " PRIMARY KEY";
      }
    }

    {
      if (columns > 0) {
        query += ", ";
      }

      query += specs;

      columns++;
    }

    {

      try {
        if (!
            this.databaseManager.getResults(null,
                "information_schema.COLUMNS",
                "COLUMN_NAME=? AND TABLE_NAME=? AND TABLE_SCHEMA=?",
                new HashMap<Integer, Object>() {{
                  put(1, name);
                  put(2, tableName);
                  put(3, SQLService.DATABASE_NAME);
                }}
            ).next()) {
          query_update += "ALTER TABLE " + this.tableName + " ADD " + specs + ";";
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return this;
  }

  public TableBuilder setConstraints(String... columnName) {
    StringBuilder constraint = new StringBuilder(", UNIQUE (");
    for (int i = 0; i < columnName.length; i++) {
      if (i > 0) {
        constraint.append(",");
      }
      constraint.append(columnName[i]);
    }
    this.query += constraint + ")";

//        Synergy.debug(this.query + "");
    return this;
  }

  public void execute() {
    query += ")";
    this.databaseManager.execute(query);
    if (!(query_update.equals(""))) {
      String[] queries = query_update.split(";");
      for (String query_u : queries) {
        if (!query_u.equals("")) {
          this.databaseManager.executeUpdate(query_u);
        }
      }
    }
  }

}
