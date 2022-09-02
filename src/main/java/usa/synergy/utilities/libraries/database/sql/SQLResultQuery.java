package usa.synergy.utilities.libraries.database.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Consumer;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.service.threading.SynergyFuture;
import usa.synergy.utilities.utlities.SynergyLogger;

public abstract class SQLResultQuery extends SQLQuery {

  public SQLResultQuery(SQLController<? extends JavaPlugin> sqlController) {
    super(sqlController);
  }

  /**
   *
   * @param response
   */
  public void results(Consumer<ResultSet> response){
    execute().getSync(resultSet -> {
      try{
        while (resultSet.next()){
          response.accept(resultSet);
        }
      }catch (SQLException exception){
        SynergyLogger.error("Error gathering results for SQL Query. See error down below.");
        exception.printStackTrace();
      }
    });
  }

  /**
   *
   * @return
   */
  @Override
  public SynergyFuture<ResultSet> execute() {
    return super.execute();
  }
}
