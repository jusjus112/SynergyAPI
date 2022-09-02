package usa.synergy.utilities.libraries.user.api;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

public abstract class SynergyUser<P extends Player> extends OfflineUser {

  public SynergyUser(UUID UUID, String name) {
    super(UUID, name);
  }

  /**
   *
   * @return the custom player object for this user.
   */
  public abstract Optional<P> getPlayer();

  /**
   *
   */
  public void reset() {
    // Health/Food
    resetHealth();
    resetFood();

    // Movement
    resetSpeed();
    resetVelocity();

    // Vehicles
    fullyEject();

    // Items/Effects
    clearInventory();
    clearEffects();
    clearXP();

    // Entity Data
    getPlayer().ifPresent(player -> {
      player.setFireTicks(0);
      player.setRemainingAir(20);
      player.setCanPickupItems(true);

      // Fake Weather/Time
      player.resetPlayerWeather();
      player.resetPlayerTime();
    });
  }


  public void resetSpeed() {
    getPlayer().ifPresent(player -> {
      player.setWalkSpeed(0.2f);
      player.setFlySpeed(0.1f);
    });
  }

  public void clearXP() {
    getPlayer().ifPresent(player -> {
      player.setLevel(0);
      player.setExp(0);
      player.setTotalExperience(0);
    });
  }

  public void resetHealth() {
    getPlayer().ifPresent(player -> {
      player.setMaxHealth(20);
      player.setHealth(player.getMaxHealth());
    });
  }

  public void resetFood() {
    getPlayer().ifPresent(player -> {
      player.setFoodLevel(20);
      player.setSaturation(20);
      player.setExhaustion(0);
    });
  }

  public void fullyEject() {
    getPlayer().ifPresent(player -> {
      player.eject();
      if (player.getVehicle() != null) {
        player.getVehicle().eject();
      }
    });
  }

  public void clearEffects() {
    getPlayer().ifPresent(player -> {
      for (PotionEffect effect : player.getActivePotionEffects()) {
        player.removePotionEffect(effect.getType());
      }
    });
  }

  public void clearInventory() {
    getPlayer().ifPresent(player -> {
      player.closeInventory();
      player.getInventory().clear();
      player.getInventory().setArmorContents(null);
      player.updateInventory();
      player.setItemOnCursor(null);
    });
  }

  public void resetVelocity() {
    getPlayer().ifPresent(player -> {
      player.setFallDistance(0);
      player.setVelocity(new Vector());
    });
  }

//  @Override
//  public boolean equals(Object o) {
//    if (this == o) return true;
//    if (o == null || getClass() != o.getClass()) return false;
//    UserProvider<?> that = (UserProvider<?>) o;
//    // Override the object equals to check their uuid.
//    return this.UUID.equals(that.UUID);
//  }

}
