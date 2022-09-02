package usa.synergy.utilities.libraries.database.sql.queries;

import usa.synergy.utilities.libraries.database.sql.SQLQuery;
import usa.synergy.utilities.libraries.database.sql.SQLController;
import usa.synergy.utilities.libraries.database.sql.SQLResultQuery;

public class SelectQuery extends SQLResultQuery {

  public SelectQuery(SQLController<?> sqlController) {
    super(sqlController);
  }

  @Override
  public StringBuilder getQuery() {
    return new StringBuilder("SELECT %s FROM %s");
  }

  public SelectQuery leftJoin(String table, String onColumn, String check){
    this.addToQuery("LEFT JOIN" + _SPACE + table + _SPACE + "ON" +
        _SPACE + table + "." + _SPACE + onColumn + "=" + _SPACE + check);
    return this;
  }

  public SelectQuery columns(String... columns){
    if (columns.length <= 0){
      this.addToQuery("*");
    }else {
      for (int i = 0; i < columns.length; i++) {
        this.addToQuery(i == 0 ? columns[i] : _COMMA + _SPACE + columns[i]);
      }
    }
    return this;
  }

  public SelectQuery leftJoin(String table, String as, String onColumn, String check){
    return this;
  }

  public SelectQuery where(){
    return this;
  }

  public SelectQuery group(){
    return this;
  }

  public SelectQuery orderBy(){

    return this;
  }

}
