package usa.synergy.utilities.assets.version;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.Module;

public class VersionManager extends Module {

  public VersionManager(JavaPlugin plugin) {
    super(plugin, "Version Manager");
  }

  public Class<?> getServerClass(String afterPackage) {
    String servPack = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    try {
      return Class.forName("net.minecraft.server." + servPack + "." + afterPackage);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public String getServerVersion() {
    return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
  }

  public boolean checkVersion(double version) {
    String pack = Bukkit.getServer().getClass().getPackage().getName().replaceAll("_", ".");
    return pack.contains(Double.toString(version));
  }

}
