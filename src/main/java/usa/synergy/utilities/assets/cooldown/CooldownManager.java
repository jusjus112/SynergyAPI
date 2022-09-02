package usa.synergy.utilities.assets.cooldown;

import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import usa.synergy.utilities.Module;

public class CooldownManager<A extends JavaPlugin> extends Module<A> {

  private final HashMap<Object, HashMap<Long, Boolean>> cd = new HashMap<>();

  public CooldownManager(A plugin) {
    super(plugin, "Cooldown");

    run();
  }

  /**
   *
   */
  public void run() {
    new BukkitRunnable() {
      @Override
      public void run() {
        Iterator<Object> iter = cd.keySet().iterator();
        while (iter.hasNext()) {
          Object ob = iter.next();
          if (cd.containsKey(ob)) {
            HashMap<Long, Boolean> map = cd.get(ob);
            if (map.containsValue(Boolean.TRUE)) {
              long i = 0;
              for (long k : map.keySet()) {
                i = k;
              }
              i--;
              map.clear();
              if (i <= 0) {
                iter.remove();
                cd.remove(ob);
              } else {
                map.put(i, true);
              }
            }
          }
        }
      }
    }.runTaskTimerAsynchronously(getLoader(), 100, 1);
  }

  /**
   * @param ob
   * @param ticks
   */
  public void addCooldown(Object ob, long ticks) {
    if (cd.containsKey(ob)) {
      return;
    }
    HashMap<Long, Boolean> map = new HashMap<>();
    map.put(ticks, true);
    cd.put(ob, map);
  }

  /**
   * @param ob
   * @return
   */
  public boolean isOnCooldown(Object ob) {
    HashMap<Long, Boolean> map = cd.get(ob);
    boolean value = false;
    if (map == null || map.isEmpty()) {
      return false;
    }
    for (boolean i : map.values()) {
      value = i;
    }
    return value;
  }

  /**
   * @param ob
   * @return
   */
  public Long getLastMiliSeconds(Object ob) {
    if (!cd.containsKey(ob)) {
      return 0L;
    }
    HashMap<Long, Boolean> map = cd.get(ob);
    long key = 0;
    for (long i : map.keySet()) {
      key = i;
    }
    return key;
  }

}
