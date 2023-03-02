package usa.synergy.utilities.assets.command.api;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
@Deprecated
public abstract class SynergyCommand extends Command {

  //    private final JavaPlugin plugin;
//    private final boolean consoleAllowed;
//    private final String description;
  private String permission = null;
  //    private final String[] aliases;
  private String[] playerUsage, consoleUsage;
  @Setter
  private double cooldown = 1.0;

  public SynergyCommand(@NotNull String name,
      @NotNull String description, @NotNull String usageMessage,
      @NotNull List<String> aliases) {
    super(name, description, usageMessage, aliases);
  }

  public SynergyCommand(@NotNull String name, @NotNull String permission,
      @NotNull String description, @NotNull String usageMessage,
      @NotNull List<String> aliases) {
    super(name, description, usageMessage, aliases);

    this.permission = permission;
  }

  @Override
  public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel,
      @NotNull String[] args) {

    if (sender instanceof Player player) {
      if (getPermission() != null && player.hasPermission(getPermission())) {
        return true;
      }

      if (!onCommand(player, this, args)) {
        // Executing generic executor when main command failed.
        onGenericExecute(sender, this, args);
      }
      return true;
    } else {
      onGenericExecute(sender, this, args);
    }

    return false;
  }

  //    public SynergyCommand(JavaPlugin plugin, String permission, String description, boolean consoleAllowed, String... aliases) {
//        this.plugin = plugin;
//        this.consoleAllowed = consoleAllowed;
//        this.description = description;
//        this.aliases = aliases;
//        this.playerUsage = aliases;
//        this.consoleUsage = aliases;
//        this.permission = permission;
//    }

//    @Override
//    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
//        @NotNull String label, @NotNull String[] args) {
//
//        if (sender instanceof Player player){
//            if (player.hasPermission(getPermission())) {
//                onCommand(player, command, args);
//                return true;
//            }
//        }
//
//        return false;
//    }

  public void setPlayerUsage(String... usage) {
    this.playerUsage = usage;
  }

  public void setConsoleUsage(String... usage) {
    this.consoleUsage = usage;
  }

  public void couldNotFind(Player p, String thing, String attempt) {
//        p.sendMessage(
//                     Message.format("command.player.not_found", null)
//        );
//        p.sendMessage(C.PRIMARY + "Could not find " + thing + " " + C.MESSAGE_HIGHLIGHT + attempt + C.PRIMARY_MESSAGE + ".");
  }

  public abstract boolean onCommand(Player player, Command command, String[] args);

  public void onGenericExecute(CommandSender sender, Command command, String[] args) {
  }

  @Override
  public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
      @NotNull String[] args) throws IllegalArgumentException {
    return super.tabComplete(sender, alias, args);
  }

  //    public void sendUsageMessage(Player player) {
//        String usageString = "";
//
//        if(playerUsage.length != 0) {
//            for(String s : playerUsage)
//                usageString+=s + " ";
//        }
//
//        player.sendMessage("§cUsage: /" + aliases[0] + (playerUsage.length != 0 ? " " + usageString.trim() : ""));
//    }
//
//    public void sendUsageMessage(ConsoleCommandSender console) {
//        String usageString = "";
//
//        if(consoleUsage.length != 0) {
//            for(String s : consoleUsage)
//                usageString+=s + " ";
//        }
//
//        console.sendMessage("Usage: /" + aliases[0] + (consoleUsage.length != 0 ? " " + usageString.trim() : ""));
//    }
}
