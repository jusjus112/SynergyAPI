package usa.synergy.utilities.libraries.database.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.service.threading.SynergyFuture;
import usa.synergy.utilities.utlities.SynergyLogger;

public abstract class SQLQuery {

  protected final SQLController<? extends JavaPlugin> sqlController;
  private final StringBuilder query;

  protected final String _SPACE = " ";
  protected final String _COMMA = ",";
  protected final String _QUOTE = "'";

  public StringBuilder getQuery(){
    return new StringBuilder();
  }

  protected void addToQuery(String s){
    this.query.append(s);
  }

  public SQLQuery(SQLController<? extends JavaPlugin> sqlController){
    this.query = getQuery();
    this.sqlController = sqlController;
  }

  /**
   *
   * @return SynergyFuture<ResultSet>
   */
  public SynergyFuture<ResultSet> execute(){
    SynergyFuture<ResultSet> future = SynergyFuture.createInstance();
    try{
      try (Connection connection = sqlController.getSqlConnector().getConnection()) {

        // Execute the query
        PreparedStatement statement = connection.prepareStatement(
          this.query.toString()
        );

        // Populating results, cache and factory storage for results
        ResultSet resultSet = statement.executeQuery();
        RowSetFactory factory = RowSetProvider.newFactory();
        CachedRowSet crs = factory.createCachedRowSet();
        crs.populate(resultSet);

        // close when used
        statement.close();
        resultSet.close();
        connection.close();

        // Return the resultSet once completed
        future.complete(crs);
      }
    }catch (SQLException sqlException){
      // TODO Error handling
    }

    return future;
  }

}
