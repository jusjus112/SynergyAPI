package usa.synergy.utilities.assets.gui.impl;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import usa.synergy.utilities.Module;
import usa.synergy.utilities.assets.gui.api.ClickableGuiElement;
import usa.synergy.utilities.assets.gui.api.GuiElement;
import usa.synergy.utilities.assets.gui.api.GuiInteract;
import usa.synergy.utilities.assets.gui.api.GuiInteractElement;
import usa.synergy.utilities.assets.gui.api.InteractItem;
import usa.synergy.utilities.assets.gui.api.MenuInventoryHolder;
import usa.synergy.utilities.utlities.SynergyLogger;

public class GuiManager extends Module {

  @Getter
  private final List<GuiInteract> interactMenus = Lists.newArrayList();

  public GuiManager(JavaPlugin plugin) {
    super(plugin, "Gui Manager");
  }

  @EventHandler
  public void on(InventoryClickEvent event) {
    InventoryView view = event.getView();
    Inventory topInventory = view.getTopInventory();

    if (topInventory.getHolder() instanceof MenuInventoryHolder holder) {
      Gui menu = holder.getMenu();
      SynergyLogger.debug("GUIMANAGER CLICK 1");
      if (event.getRawSlot() == event.getSlot()) {
        int slot = event.getSlot();

        if (menu instanceof PaginatedGui) {

          boolean navigational = false;
          int newPage = holder.getPageNumber();
          if (slot == ((PaginatedGui) menu).getChangePageSlots().getLeft()) {
            newPage--;
            navigational = true;
          } else if (slot == ((PaginatedGui) menu).getChangePageSlots().getRight()) {
            newPage++;
            navigational = true;
          }
          event.setCancelled(true);

          if (newPage >= 1) {
            ((PaginatedGui) menu).setPage(holder.getViewer(), newPage);
          }
          if (navigational) {
            return;
          }
        }
        SynergyLogger.debug("GUIMANAGER CLICK 2");
        GuiElement item = menu.getElement(slot);

        if (item == null && holder.getPaginatedItems() != null) {
          item = holder.getPaginatedItems().get(slot);
        }
        SynergyLogger.debug("GUIMANAGER CLICK 3");
        boolean onInsert = menu.onInsert(event.getCurrentItem(), topInventory);

        if (item != null) {
//          if (item instanceof GuiElement) {
            if (onInsert) {
              SynergyLogger.debug("GUIMANAGER CLICK 4");
              event.setCancelled(false);
            } else {
              SynergyLogger.debug("GUIMANAGER CLICK 5");
              event.setCancelled((event.getRawSlot() == event.getSlot() || event.isShiftClick()
                  || event.getClick() == ClickType.DOUBLE_CLICK));
            }
//          }
          menu.setIgnoringParent(true);
          if (item instanceof ClickableGuiElement) {
            SynergyLogger.debug("GUIMANAGER CLICK 6");
            event.setCancelled(((ClickableGuiElement) item).click(holder.getViewer(), event.getClick(), menu));
          }
          SynergyLogger.debug("GUIMANAGER CLICK 6 - 1");
          menu.setIgnoringParent(false);
        } else {
          SynergyLogger.debug("GUIMANAGER CLICK 7");
          event.setCancelled(!onInsert);
        }
      } else {
        SynergyLogger.debug("GUIMANAGER CLICK 8");
        if (menu.onInsert(event.getCurrentItem(), topInventory)) {
          event.setCancelled(false);
        } else {
          SynergyLogger.debug("GUIMANAGER CLICK 9");
          event.setCancelled((event.getRawSlot() == event.getSlot() || event.isShiftClick()
              || event.getClick() == ClickType.DOUBLE_CLICK));
        }
      }
    }
  }

  @EventHandler
  public void on(InventoryDragEvent event) {
    if (event.getInventory().getHolder() instanceof MenuInventoryHolder menuInventoryHolder) {
      SynergyLogger.debug("GUIMANAGER DRAG 2");
      if (event.getOldCursor() != null) {
        if (event.getInventory() == event.getView().getTopInventory()) {
          Gui gui = menuInventoryHolder.getMenu();
          SynergyLogger.debug("GUIMANAGER DRAG 3");
          boolean onInsert = gui.onInsert(event.getOldCursor(), event.getInventory());
          event.setCancelled(!onInsert);
          SynergyLogger.debug(onInsert + " = GUIMANAGER DRAG 4");
        }
      }
    }
  }

  @EventHandler
  public void on(InventoryMoveItemEvent event) {
    Inventory destination = event.getDestination();
		SynergyLogger.debug("ON INSERT GUI 1");
    if (destination instanceof Gui) {
      boolean onInsert = ((Gui) destination).onInsert(event.getItem(), destination);
      SynergyLogger.debug("ON INSERT GUI 2");
      SynergyLogger.debug(onInsert + " = ON INSERT GUI 2");
      event.setCancelled(!onInsert);
      event.setItem(onInsert ? event.getItem() : null);
    }
  }

	/*
		Player player = (Player) event.getWhoClicked();
		SynergyUser synergyUser = Core.getPlugin().getUserManager().getUser(player);
		int slot = event.getSlot();

		for(Gui menu : Lists.newArrayList(menus)) {
			if(menu.getName().equalsIgnoreCase("Player Inventory")||
				(event.getView().getTitle().equals(menu.getName())&&menu.getCurrentSessions().containsKey(player.getUniqueId()))) {
				if(event.getCurrentItem() != null) {
					if (!menu.onInsert(event.getCurrentItem())){
						event.setCancelled(true);
					}
					if(menu.getElements().containsKey(slot)) {
						menu.getElements().get(slot).click(synergyUser, event.getClick());
					}
				}
			}
		}
	 */

  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    for (GuiInteract menu : Lists.newArrayList(interactMenus)) {
      if (e.getItem() != null && e.getItem().hasItemMeta()) {
        for (GuiInteractElement guiInteractElement : menu.getElements().keySet()) {
          if (guiInteractElement.getIcon(player).equals(e.getItem())) {
            guiInteractElement.click(player, e.getAction());
            //TODO: Add a click cooldown to prevent spamming
            break;
          }
        }
      }
    }
  }

  @EventHandler
  public void onItemInteract(PlayerInteractEvent e) {
    if (e.getAction() != Action.PHYSICAL) {
      for (InteractItem interactItem : InteractItem.getInteractItems()) {
        ItemStack itemStack = e.getItem();
        ItemStack filler = interactItem;

        if (itemStack == null){
          continue;
        }

        if (itemStack.getType() == filler.getType() && itemStack.hasItemMeta() && filler.hasItemMeta() &&
            itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(filler.getItemMeta().getDisplayName())) {
          e.setCancelled(true);

          interactItem.onClick(e.getPlayer(), e.getAction());
          break;
        }
      }
    }
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e) {
    if (e.getPlayer() instanceof Player player) {
      InventoryView view = e.getView();
      Inventory topInventory = view.getTopInventory();

      if (topInventory.getHolder() instanceof MenuInventoryHolder holder) {
        Gui parent = holder.getMenu().getParent();
        holder.getMenu().getCurrentSessions().remove(player.getUniqueId());
        if (!holder.getMenu().onClose(topInventory, player)) {
          return;
        }
        if (parent != null && !holder.getMenu().isIgnoringParent()) {
          new BukkitRunnable() {
            @Override
            public void run() {
              parent.open(holder.getViewer());
            }
          }.runTaskLater(getLoader(), 1L);
        }
      }
    }
  }

}
