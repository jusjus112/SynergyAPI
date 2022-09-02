package usa.synergy.utilities.utlities;

import static org.bukkit.Bukkit.getServer;

import java.util.Arrays;
import usa.synergy.utilities.assets.assets.LinuxColorCodes;

public class SynergyLogger {

  public static void debug(String... messages){
    String name = getServer().getLogger().getName();

    Arrays.stream(messages).forEach(s -> System.out.println(
        LinuxColorCodes.ANSI_RED+"[" + name + " DEBUG] "+LinuxColorCodes.ANSI_YELLOW+s+LinuxColorCodes.ANSI_RESET));
  }

  public static void info(String... messages){
    Arrays.stream(messages).forEach(s -> getServer().getLogger().info(
        format("info", LinuxColorCodes.ANSI_YELLOW, s)));
  }

  public static void success(String... messages){
    Arrays.stream(messages).forEach(s -> getServer().getLogger().info(format("success", LinuxColorCodes.ANSI_GREEN, s)));
  }

  public static void normal(String... messages){
    Arrays.stream(messages).forEach(s -> System.out.println(format("", LinuxColorCodes.ANSI_RESET, s)));
  }

  public static void error(String... messages){
    Arrays.stream(messages).forEach(s -> System.out.println(format("error", LinuxColorCodes.ANSI_RED, s)));
  }

  public static void warn(String... messages){
    Arrays.stream(messages).forEach(s -> System.out.println(format("warn", LinuxColorCodes.ANSI_RED, s)));
  }

  public static String format(String prefix, String color, String message){
    return LinuxColorCodes.ANSI_YELLOW+"Synergy"+color+(prefix.length()>0?" "+prefix.toUpperCase():"")+LinuxColorCodes.ANSI_CYAN+" </> "+color+message+LinuxColorCodes.ANSI_RESET;
  }

}
