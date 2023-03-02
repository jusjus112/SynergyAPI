package usa.synergy.utilities.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.assets.PropertiesFile;
import usa.synergy.utilities.service.sql.SynergySQLException;
import usa.synergy.utilities.utlities.SynergyLogger;

@Deprecated
public class SQLService {

  public static String DATABASE_NAME;
  public static String JDBC_URL;
  private static String HOST;
  private static String USERNAME;
  private static String PASSWORD;
  private static Integer PORT;
  private static String PROPERTIES_FILE_PATH = null;

  private static HikariDataSource dataSource1, dataSource2;
  private static Properties properties;

  private SQLService(String host, String databaseName, int port, String user, String password) {
    HOST = host;
    DATABASE_NAME = databaseName;
    PORT = port;
    USERNAME = user;
    PASSWORD = password;
  }

  private final Map<String, String> propertiesMap = Map.of(
    "sql.jdbc", "jdbc:mysql://",
    "sql.server", "127.0.0.1",
    "sql.database", "backpacks",
    "sql.port", "3308",
    "sql.user", "root",
    "sql.password", ""
  );

  private SQLService(JavaPlugin javaPlugin){
    PropertiesFile propertiesFile = new PropertiesFile(javaPlugin.getDataFolder().getPath(), "sql");
    Properties prop = new Properties();

    try(FileInputStream inputStream = new FileInputStream(propertiesFile.getFile())) {
      SynergyLogger.info("Found sql.properties file, loading properties...");
      prop.load(inputStream);

      JDBC_URL = prop.getProperty("sql.jdbc");
      DATABASE_NAME = prop.getProperty("sql.database");
      HOST = prop.getProperty("sql.server");
      PORT = Integer.parseInt(prop.getProperty("sql.port"));
      USERNAME = prop.getProperty("sql.user");
      PASSWORD = prop.getProperty("sql.password");
    }catch (Exception e){
      SynergyLogger.info("sql.properties file not found, creating new file...");
      try(OutputStream outputStream = new FileOutputStream(propertiesFile.getFile())) {
        prop.putAll(propertiesMap);
        prop.store(outputStream, "SQL Properties");
      }catch (IOException ex){
        ex.printStackTrace();
      }
    }

    properties = prop;
  }

  public static SQLService fromProperties(JavaPlugin javaPlugin){
    return new SQLService(javaPlugin);
  }

  public static SQLService fromFile(String file){
    return null;
  }

  public SQLService(FileConfiguration fileConfiguration) {
    Validate.isTrue(fileConfiguration.contains("sql"),
        "No SQL entry found in the given configuration file.");

    try {
      JDBC_URL = fileConfiguration.getString("sql.jdbc");
      HOST = fileConfiguration.getString("sql.host");
      DATABASE_NAME = fileConfiguration.getString("sql.database");
      PORT = fileConfiguration.getInt("sql.port");
      USERNAME = fileConfiguration.getString("sql.username");
      PASSWORD = fileConfiguration.getString("sql.password");
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void testConnection() throws SynergySQLException {
    try (Connection connection = connection()) {
      if (connection == null) {
        throw new SynergySQLException("Connection is null.");
      }
      SynergyLogger.success("Connection to SQL was successful.");
    } catch (Exception e) {
      throw new SynergySQLException(e);
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
    String jdbcUrl = JDBC_URL + HOST + ":" + PORT + "/" + DATABASE_NAME;

//    if (PROPERTIES_FILE_PATH != null && properties != null) {
//      return new HikariConfig(properties);
//    }
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
