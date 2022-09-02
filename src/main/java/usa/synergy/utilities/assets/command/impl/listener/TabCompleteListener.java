package usa.synergy.utilities.assets.command.impl.listener;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import usa.synergy.utilities.SynergyAPI;

public class TabCompleteListener implements Listener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void onCommand(TabCompleteEvent e) {
    List<String> list = new ArrayList<>();

    if (e.getBuffer().contains("/")) {
      String[] splitter = e.getBuffer().split(" ");
      if (splitter.length > 1) {
        if (SynergyAPI.getInstance().getCommandManager()
            .isCommand(splitter[0].replace("/", ""))) {
          for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase().startsWith(splitter[splitter.length - 1].toLowerCase())) {
              list.add(p.getName());
            }
          }
          e.setCompletions(list);
        }
      }
    }
  }

}
