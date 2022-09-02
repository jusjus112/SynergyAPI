package usa.synergy.utilities.libraries.user.impl;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.Module;
import usa.synergy.utilities.libraries.user.api.SynergyUser;

public class UserHandler<A extends JavaPlugin, U extends SynergyUser<? extends Player>> extends Module<A> {

  private final ConcurrentHashMap<UUID, U> users;

  public UserHandler(A plugin) {
    super(plugin, "User");

    this.users = new ConcurrentHashMap<>();
  }

  /**
   *
   * @see SynergyUser
   * @param user
   * @return
   */
  public boolean register(U user){
    if (!this.users.containsKey(user.getUUID())) {
      this.users.put(user.getUUID(), user);
      return true;
    }
    return false;
  }

  /**
   *
   * @param uuid
   * @return
   */
  public Optional<U> getUser(UUID uuid) {
    return Optional.ofNullable(this.users.get(uuid));
  }

}
