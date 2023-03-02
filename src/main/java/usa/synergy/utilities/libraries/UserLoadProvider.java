package usa.synergy.utilities.libraries;

import org.bukkit.entity.Player;
import usa.synergy.utilities.libraries.user.api.SynergyUser;

public interface UserLoadProvider<U extends SynergyUser<? extends Player>> {

  /**
   *
   * @param user
   */
  void onUserLoad(U user);

  /**
   *
   * @param user
   */
  void onUserUnLoad(U user);

}
