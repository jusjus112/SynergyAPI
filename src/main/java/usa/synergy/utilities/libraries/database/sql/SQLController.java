package usa.synergy.utilities.libraries.database.sql;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.util.UUID;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import usa.synergy.utilities.Module;

/**
 *
 * @param <A>
 */
@Getter
public class SQLController<A extends JavaPlugin> extends Module<A> {

  private final SQLConnector<A> sqlConnector;

  public SQLController(A plugin, SQLConnector<A> sqlConnector) {
    super(plugin, "SQL");

    this.sqlConnector = sqlConnector;
  }

  /**
   *
   * @param queryClazz
   * @param <T>
   * @return
   */
  public <T extends SQLQuery> T query(@NotNull Class<T> queryClazz) {
    Validate.notNull(this.sqlConnector, "Cannot query SQL without a proper connection. See the Synergy docs.");

    try{
      return queryClazz.getDeclaredConstructor(SQLController.class).newInstance(this);
    }catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException exception){
      exception.printStackTrace();
    }

    return null;
  }

  /**
   * Converting a UUID to a string of bytes to save memory and storage.
   * Will output an unreadable string which can be used by SQL InputStream method.
   * To read the UUID again and to pass it to it's object, see {@link #convertBinaryStream(InputStream)}
   *
   * @see InputStream
   * @see Byte
   * @param uuid the uuid to be converted
   * @return the {@link InputStream} of the given UUID
   */
  public InputStream convertUniqueId(UUID uuid) {
    byte[] bytes = new byte[16];
    ByteBuffer.wrap(bytes)
        .putLong(uuid.getMostSignificantBits())
        .putLong(uuid.getLeastSignificantBits());
    return new ByteArrayInputStream(bytes);
  }

  /**
   *
   * @param stream
   * @return
   */
  public UUID convertBinaryStream(InputStream stream) {
    ByteBuffer buffer = ByteBuffer.allocate(16);
    try {
      buffer.put(ByteStreams.toByteArray(stream));
      buffer.flip();
      return new UUID(buffer.getLong(), buffer.getLong());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
