package usa.synergy.utilities.assets.command.impl;

import static org.bukkit.Bukkit.getServer;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.Module;
import usa.synergy.utilities.assets.command.api.SynergyCommand;

public class CommandManager extends Module {

  private final List<SynergyCommand> commands = Lists.newArrayList();
  private Map<String, Command> knownCommands;

  public CommandManager(JavaPlugin plugin) {
    super(plugin, "Command Manager");

//        if (!SynergyUtilitiesAPI
//                .getApi()
//                .getVersionManager()
//                .checkVersion(1.8)) {
//            registerListener(
//                new TabCompleteListener()
//            );
//        }

    try {
//            Object commandMap = getPlugin().getServer().getClass().getMethod("getCommandMap").invoke(getPlugin().getServer());
//            Field field = commandMap.getClass().getDeclaredField("knownCommands");
//            field.setAccessible(true);
//            this.knownCommands = (Map<String, SynergyCommand>) field.get(commandMap); // << 1.13

      final SimpleCommandMap commandMap = (SimpleCommandMap) getServer().getCommandMap();
      this.knownCommands = commandMap.getKnownCommands(); // Line 293
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onCommand(PlayerCommandPreprocessEvent e) {
//        List<String> args = UtilString.convert(e.getMessage().split(" "));
//        String cmd = args.get(0).replaceAll("/", "");
//
//        for(SynergyCommanderfdfdgvb command : commands) {
//            if(Arrays.asList(command.getAliases()).contains(cmd.toLowerCase())) {
//                e.setCancelled(true);
//                args.remove(0);
//                if (e.getPlayer().hasPermission(command.getPermission())) {
//                    command.execute(e.getPlayer(), cmd, args.toArray(new String[args.size()]));
//                    return;
//                }
//            }
//        }
//    }
//
//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onCommand(ServerCommandEvent e) {
//        if (e.getSender() instanceof ConsoleCommandSender){
//            List<String> args = UtilString.convert(e.getCommand().split(" "));
//            String cmd = args.get(0).replaceAll("/", "");
//            for(SynergyCommanderfdfdgvb command : commands) {
//                if (command.isConsoleAllowed()) {
//                    if (Arrays.asList(command.getAliases()).contains(cmd.toLowerCase())) {
//                        e.setCancelled(true);
//                        args.remove(0);
//                        command.execute(((ConsoleCommandSender) e.getSender()), cmd, args.toArray(new String[args.size()]));
//                        return;
//                    }
//                }
//            }
//        }
//    }

  public void registerCommand(SynergyCommand synergyCommand) {
//        Arrays.stream(synergyCommand.getAliases()).forEachOrdered(s -> {
//            Objects.requireNonNull(getPlugin().getCommand(s)).setExecutor(synergyCommand);
//
//            this.knownCommands.put(synergyCommand)
//        });
    Lists.newArrayList(synergyCommand.getAliases()).forEach(alias -> {
      getServer().getCommandMap().register(alias, synergyCommand);
      this.knownCommands.put(alias, synergyCommand);
    });
  }

  public boolean isCommand(String command) {
    for (SynergyCommand cmd : commands) {
      for (String alias : cmd.getAliases()) {
        if (alias.equalsIgnoreCase(command)) {
          return true;
        }
      }
    }
    return false;
  }

  public void unregisterMinecraftCommand(String command) {
    Bukkit.getScheduler().runTaskLater(getLoader(), () -> {
      this.knownCommands.remove(command);
      this.knownCommands.remove("minecraft:" + command);
      this.knownCommands.remove("bukkit:" + command);
      this.knownCommands.remove("essentials:" + command);
      this.knownCommands.remove("essentials:e" + command);
    }, 1L);
  }

  public List<SynergyCommand> getCommands() {
    return commands;
  }
}
