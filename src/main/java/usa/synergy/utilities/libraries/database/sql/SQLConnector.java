package usa.synergy.utilities.libraries.database.sql;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.PriorityQueue;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public class SQLConnector<A extends JavaPlugin> {

  private final SQLConfig config;
  private PriorityQueue<HikariDataSource> sources;
  // Peek =     retrieve not remove
  // Element =  retrieve not remove throws exception
  // Poll =     retrieves and removes
  // Remove =   retrieves and removes and throws exception

  public SQLController<A> initialize(A plugin){
    this.sources = new PriorityQueue<>();

    return new SQLController<>(plugin, this);
  }

  /**
   *
   * @return
   * @throws SQLException
   */
  public Connection getConnection() throws SQLException {
    Validate.notNull(this.sources, "Cannot get SQL connection without use initialize() for connector.");

    if (!sources.isEmpty()){
      HikariDataSource dataSource = sources.element();

      if (dataSource.isClosed() || dataSource.isRunning()){
        sources.remove();
        sources.offer(generateNewDataSource());

        return getConnection();
      }

      return dataSource.getConnection();
    }

    sources.offer(generateNewDataSource());
    return getConnection();
  }

  /**
   *
   * @return
   */
  private HikariDataSource generateNewDataSource() {
    return new HikariDataSource(this.config.hikariConfig());
  }

}
