package usa.synergy.utilities.service.sql;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import lombok.Getter;
import usa.synergy.utilities.service.SQLService;

@Getter
@Deprecated
public class DatabaseManager {

  /**
   * TODO: Disconnect connection after a while with no queries
   * <p>
   * https://www.baeldung.com/java-connection-pooling
   * https://stackoverflow.com/questions/34117164/java-async-mysql-queries
   */


  public DatabaseManager(SQLService service) {

  }

  public boolean update(String table, Map<String, Object> data, Map<String, Object> whereData) {
    HashMap<Integer, Object> indexed = new HashMap<>();
    try {
      StringBuilder query = new StringBuilder("UPDATE " + table + " SET ");

      data.remove("uuid");
      data.remove("id");

      final int[] a = {1};
      data.forEach((s, o) -> {
        if (a[0] > 1) {
          query.append(", ");
        }
        query.append("`").append(s).append("`").append("=?");
        indexed.put(a[0], o);
        a[0]++;
      });

      query.append(" WHERE ");

      AtomicInteger i = new AtomicInteger();
      whereData.forEach((s, o) -> {
        if (i.get() > 0) {
          query.append(" AND ");
        }
        query.append("`").append(s).append("`").append("=?");
        indexed.put(a[0], o);
        a[0]++;
        i.getAndIncrement();
      });

      try (Connection connection = SQLService.connection()) {

        PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

        for (Integer index : indexed.keySet()) {
          Object value = indexed.get(index);

          if (value instanceof InputStream) {
            preparedStatement.setBinaryStream(index, (InputStream) value);
            continue;
          }
          preparedStatement.setObject(index, value);
        }

        preparedStatement.executeUpdate();

        preparedStatement.close();
        connection.close();
      }
      return true;
    } catch (SQLException e) {
      System.out.println("Can't execute update statement. " + e.getMessage());
//            e.printStackTrace();
      return false;
    }
  }

  public ResultSet getResults(String table, String where, Map<Integer, Object> data)
      throws SQLException {
    return getResults(null, table, where, data);
  }

  public ResultSet getResults(String tablePrefix, String table, String where,
      Map<Integer, Object> data) throws SQLException {
    StringBuilder query = new StringBuilder(
        "SELECT * FROM " + (tablePrefix == null ? "" : tablePrefix + "_") + table + (where != null
            ? (" WHERE " + where) : "")
    );

    try (Connection connection = SQLService.connection()) {
      PreparedStatement statement = connection.prepareStatement(
          query.toString()
      );

      if (where != null) {
        for (int b : data.keySet()) {
          Object object = data.get(b);

          if (object instanceof InputStream) {
            statement.setBinaryStream(b, (InputStream) object);
            continue;
          }
          statement.setObject(b, object);
        }
      }

      ResultSet resultSet = statement.executeQuery();
      RowSetFactory factory = RowSetProvider.newFactory();
      CachedRowSet crs = factory.createCachedRowSet();
      crs.populate(resultSet);

      statement.close();
      resultSet.close();
      connection.close();

      return crs;
    }
  }

  public ResultSet executeQuery(String query) {
    try {
      try (Connection connection = SQLService.connection()) {
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();

        RowSetFactory factory = RowSetProvider.newFactory();
        CachedRowSet crs = factory.createCachedRowSet();
        crs.populate(resultSet);

        resultSet.close();
        connection.close();

        return crs;
      }
    } catch (SQLException e) {
      System.out.println("Can't executeQuery statement. " + e.getMessage());
    }
    return null;
  }

  public void execute(String query) {
    try {
      try (Connection connection = SQLService.connection()) {
        connection.prepareStatement(query).execute();

        connection.close();
      }
    } catch (SQLException e) {
      System.out.println("Can't execute statement. " + e.getMessage());
    }
  }

  public void executeUpdate(String query) {
    try {
      try (Connection connection = SQLService.connection()) {
        connection.prepareStatement(query).executeUpdate();

        connection.close();
      }
    } catch (SQLException e) {
      System.out.println("Can't executeUpdate statement. " + e.getMessage());
    }
  }

  public boolean insert(String table, Map<String, Object> data) {
    return execute("INSERT INTO", table, data, true);
  }

  public boolean remove(String table, Map<String, Object> whereData) {
    HashMap<Integer, Object> indexed = new HashMap<>();
    try {
      StringBuilder query = new StringBuilder("DELETE FROM " + table + " WHERE ");
      final int[] a = {1};

      AtomicInteger i = new AtomicInteger();
      whereData.forEach((s, o) -> {
        if (i.get() > 0) {
          query.append(" AND ");
        }
        query.append("`").append(s).append("`").append("=?");
        indexed.put(a[0], o);
        a[0]++;
        i.getAndIncrement();
      });

      try (Connection connection = SQLService.connection()) {

        PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

        for (Integer index : indexed.keySet()) {
          Object value = indexed.get(index);

          if (value instanceof InputStream) {
            preparedStatement.setBinaryStream(index, (InputStream) value);
            continue;
          }
          preparedStatement.setObject(index, value);
        }

        preparedStatement.executeUpdate();

        preparedStatement.close();
        connection.close();
      }
      return true;
    } catch (SQLException e) {
//            Synergy.warn("Can't execute remove statement. " + e.getMessage());
//            e.printStackTrace();
      return false;
    }
  }

  public boolean execute(String prefix, String table, Map<String, Object> data, boolean insert) {
    HashMap<Integer, Object> indexed = new HashMap<>();
    try {
      StringBuilder query = new StringBuilder(prefix + " " + table + " ("),
          values = new StringBuilder(") VALUES(");

      int a = 1;
      for (String key : data.keySet()) {
        if (a > 1) {
          query.append(", ");
          values.append(", ");
        }
        query.append("`").append(key).append("`");
        values.append('?');

        indexed.put(a, data.get(key));
        a++;
      }

      values.append(")");
      query.append(values);

//            System.out.println(query.toString());
//            System.out.println(data + " = VALUES data");

      try (Connection connection = SQLService.connection()) {
        PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

        for (Integer index : indexed.keySet()) {
          Object value = indexed.get(index);

          if (value instanceof InputStream) {
            preparedStatement.setBinaryStream(index, (InputStream) value);
            continue;
          }
          preparedStatement.setObject(index, value);
        }

        preparedStatement.executeUpdate();

        preparedStatement.close();
        connection.close();
      }
      return true;
    } catch (SQLException e) {
//            Synergy.warn("Can't execute statement. " + e.getMessage());
      return false;
    }
  }

}