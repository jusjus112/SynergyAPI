package usa.synergy.utilities.assets.utilities;

import net.md_5.bungee.api.ChatColor;

public class UtilMessage {

  public static String translateColors(String message) {
    return ChatColor.translateAlternateColorCodes('&', message);
  }

}
