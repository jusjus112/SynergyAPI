package usa.synergy.utilities.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import usa.synergy.utilities.utlities.SynergyLogger;

@Deprecated
public class SQLService {

  public static String DATABASE_NAME;
  private static String HOST;
  private static String USERNAME;
  private static String PASSWORD;
  private static Integer PORT;
  private static String PROPERTIES_FILE_PATH = null;

  private static HikariDataSource dataSource1, dataSource2;

  private SQLService(String host, String databaseName, int port, String user, String password) {
    HOST = host;
    DATABASE_NAME = databaseName;
    PORT = port;
    USERNAME = user;
    PASSWORD = password;
  }

  private SQLService(){
    try (OutputStream output = new FileOutputStream("sql.properties")) {

      Properties prop = new Properties();

      // set the properties value
      prop.setProperty("sql.serverName", "127.0.0.1");
      prop.setProperty("sql.databaseName", "database");
      prop.setProperty("sql.portNumber", "3306");
      prop.setProperty("sql.user", "elon");
      prop.setProperty("sql.password", "musk");

      // save properties to project root folder
      prop.store(output, null);
      SynergyLogger.info("SQL properties loaded.");
    } catch (IOException io) {
      io.printStackTrace();
    }
  }

  public static SQLService fromProperties(){
    return new SQLService();
  }

  public static SQLService fromFile(String file){
    return null;
  }

  public SQLService(FileConfiguration fileConfiguration) {
    Validate.isTrue(fileConfiguration.contains("sql"),
        "No SQL entry found in the given configuration file.");

    try {
      HOST = fileConfiguration.getString("sql.host");
      DATABASE_NAME = fileConfiguration.getString("sql.database");
      PORT = fileConfiguration.getInt("sql.port");
      USERNAME = fileConfiguration.getString("sql.username");
      PASSWORD = fileConfiguration.getString("sql.password");
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static Connection connection() throws SQLException {
    return getDataSource().getConnection();
  }

  public static HikariDataSource getDataSource() {
    if (dataSource1 == null) {
      return generateNewDataSource(true);
    }

    return dataSource1;

//    if (dataSource1.getHikariPoolMXBean().getIdleConnections() <= 0) {
//      return generateNewDataSource(false);
//    } else {
//      return dataSource1;
//    }
  }

  private static HikariDataSource generateNewDataSource(boolean sourceOne) {
    HikariConfig hikariConfig = getConfig();
    if (sourceOne) {
      dataSource1 = new HikariDataSource(hikariConfig);
      return dataSource1;
    } else {
      if (dataSource2 == null) {
        dataSource2 = new HikariDataSource(hikariConfig);
      }
      return dataSource2;
    }
  }

  private static HikariConfig getConfig() {
    String jdbcUrl = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE_NAME;

    if (PROPERTIES_FILE_PATH != null){
      return new HikariConfig("sql.properties");
    }
    HikariConfig hikariConfig = new HikariConfig();

    hikariConfig.setJdbcUrl(jdbcUrl);
    hikariConfig.setUsername(USERNAME);
    hikariConfig.setPassword(PASSWORD);
    hikariConfig.setMaximumPoolSize(50);
    hikariConfig.setMaxLifetime(180000);
    hikariConfig.setRegisterMbeans(true);
    hikariConfig.setIdleTimeout(5000);
    hikariConfig.setMinimumIdle(6);
    hikariConfig.setConnectionTimeout(30000);
//        hikariConfig.setIdleTimeout(30000);
    hikariConfig.setConnectionTestQuery("select * from information_schema.tables limit 1");
    hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

    return hikariConfig;
  }

}
