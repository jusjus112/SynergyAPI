package usa.synergy.utilities.libraries.database.sql;

import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public record SQLConfig(HikariConfig hikariConfig) {

  public SQLConfig {
    hikariConfig.setMaximumPoolSize(25);
    hikariConfig.setMaxLifetime(28700);
//        hikariConfig.setMinimumIdle(0);
//        hikariConfig.setIdleTimeout(30000);
//        hikariConfig.setConnectionTimeout(60000);
    hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
  }

  public static SQLConfig fromProperties() {
    return new SQLConfig(new HikariConfig("sql"));
  }

  public static SQLConfig fromBukkitConfiguration(FileConfiguration fileConfiguration) {
    HikariConfig hikariConfig = new HikariConfig();

    try {
      String HOST = fileConfiguration.getString("sql.host"),
          DATABASE_NAME = fileConfiguration.getString("sql.database"),
          USERNAME = fileConfiguration.getString("sql.username"),
          PASSWORD = fileConfiguration.getString("sql.password");
      int PORT = fileConfiguration.getInt("sql.port");
      String jdbcUrl = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE_NAME;

      hikariConfig.setJdbcUrl(jdbcUrl);
      hikariConfig.setUsername(USERNAME);
      hikariConfig.setPassword(PASSWORD);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    return new SQLConfig(hikariConfig);
  }

}
