package usa.synergy.utilities.libraries.user.api;

import java.io.InputStream;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import usa.synergy.utilities.libraries.database.sql.annotations.SQLField;
import usa.synergy.utilities.service.sql.UtilSQL;

@RequiredArgsConstructor
@Getter
public abstract class OfflineUser {

  /**
   * @see UUID
   * @see Player
   * @see OfflinePlayer#getUniqueId()
   * @return the unique id of the user.
   */
  @SQLField
  private final UUID UUID;

  /**
   * Name given upon join or retrieved from database.
   *
   * @see OfflinePlayer#getName()
   * @return the user's name
   */
  @SQLField
  private final String name;

  public InputStream getUUIDStream(){
    return UtilSQL.convertUniqueId(this.UUID);
  }

}
