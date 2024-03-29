package usa.synergy.utilities.assets.gui.impl;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.assets.Pair;
import usa.synergy.utilities.assets.gui.api.GuiElement;
import usa.synergy.utilities.assets.gui.api.GuiSize;
import usa.synergy.utilities.assets.gui.api.MenuInventoryHolder;
import usa.synergy.utilities.assets.utilities.ItemBuilder;

public abstract class PaginatedGui extends Gui {

  private final Paginator paginator;

  public PaginatedGui(JavaPlugin plugin, String name, GuiSize guiSize, Paginator paginator) {
    this(plugin, name, guiSize, paginator, true);
  }

  public PaginatedGui(JavaPlugin plugin, String name, GuiSize guiSize, Paginator paginator,
      boolean setup) {
    super(plugin, name, guiSize, setup);

    this.paginator = paginator;
  }

  /**
   * Sets the page for the menu to the given page, if there is no items available for that page then
   * it'll transverse backwards until it finds items or stops at page 1. If they don't have this
   * menu open, it'll silently ignore.
   *
   * @param player the viewer that has the menu open
   * @param page   the page to start at
   */
  public final void setPage(Player player, int page) {
    Inventory inventory = player.getOpenInventory().getTopInventory();
    if (inventory.getHolder() instanceof MenuInventoryHolder) {
      MenuInventoryHolder holder = (MenuInventoryHolder) inventory.getHolder();
      if (holder.getMenu().equals(this)) {
        transitionToPage(holder, page);
      }
    }
  }

  @Override
  void setItems(Inventory inventory, Player player) {
    super.setItems(inventory, player);

    ItemBuilder goLeft = new ItemBuilder(paginator.changePageItems.getLeft())
        .setName("§d§lPrevious Page");
    ItemBuilder goRight = new ItemBuilder(paginator.changePageItems.getRight())
        .setName("§d§lNext Page");

    inventory.setItem(paginator.changePageSlots.getLeft(), goLeft.build());
    inventory.setItem(paginator.changePageSlots.getRight(), goRight.build());

    MenuInventoryHolder holder = (MenuInventoryHolder) Objects
        .requireNonNull(inventory.getHolder());
    holder.createPaginatedItems();
    transitionToPage(holder, 1);
  }

  /**
   * This will change the page dynamically based on the amount of items it has received and how many
   * slots are available. It will switch between pages and show all the items that can fit inside
   * the page, if it has more items than slots, it brings you to the second page and so on. Till
   * you've reached the end.
   *
   * @param holder Holder of the inventory/gui.
   * @param page   Which page to show.
   */
  private void transitionToPage(MenuInventoryHolder holder, int page) {
    Inventory inventory = holder.getInventory();
    Player viewer = holder.getViewer();

    if (!holder.isTransitingPageState()) {
      holder.setTransitingPageState(true);
      int[] slots = paginator.getPageSlots();

      paginator.getItemsFor(viewer, page, items -> {
        holder.setTransitingPageState(false);
        holder.getPaginatedItems().clear();
        holder.setPageNumber(page);

        if (items.isEmpty()) {
          if (page > 1) {
            transitionToPage(holder, page - 1);
          } else {
            for (int slot : slots) {
              inventory.setItem(slot, null);
            }
          }
        } else {
          int j = page > 1 ? (page * slots.length) - slots.length : 0;

          if (j >= items.size()) {
            transitionToPage(holder, page - 1);
            return;
          }
          for (int i = 0; i < slots.length; i++) {
            int slot = slots[i];

            if (i + j >= items.size()) {
              inventory.setItem(slot, null);
            } else {
              GuiElement item = items.get(j + i);

              inventory.setItem(slot, item.getIcon(viewer));
              holder.getPaginatedItems().put(slot, item);
            }
          }
        }
      });
    }
  }

  /**
   * @return the slot of the paginator change slot item
   */
  public final Pair<Integer, Integer> getChangePageSlots() {
    return paginator.getChangePageSlots();
  }

  public abstract static class Paginator {

    private final Pair<ItemStack, ItemStack> changePageItems;
    @Getter
    private final Pair<Integer, Integer> changePageSlots;
    @Getter
    private final int[] pageSlots;

    /**
     * Constructs a paginator used by PaginatedMenu for pagination stuff. There is only one item
     * used to transverse between pages, left click to go back, and right click to go forward.
     *
     * @param changePageItems The item that will display to the user to change pages.
     * @param changePageSlots The slot to put the change page item at.
     * @param pageSlots       An array of page slots that will be used for the paged items.
     */
    public Paginator(Pair<ItemStack, ItemStack> changePageItems,
        Pair<Integer, Integer> changePageSlots, int[] pageSlots) {
      this.changePageItems = changePageItems;
      this.changePageSlots = changePageSlots;

      this.pageSlots = new int[pageSlots.length];
      System.arraycopy(pageSlots, 0, this.pageSlots, 0, pageSlots.length);
    }

    /**
     * Method called when obtaining the items for the given page, it uses a callback so you can do a
     * database look if required, it will ignore changes until the transition has complete so make
     * sure to call the callback.
     * <p>
     * You can use createPage method below to extract from a big list, however you should only
     * constrct MenuItem for those being passed to the consumer.
     *
     * @param viewer       the viewer of the menu
     * @param page         the page the viewer is on
     * @param itemConsumer the callback method to be called with the items for that page
     */
    public abstract void getItemsFor(Player viewer, int page,
        Consumer<LinkedList<GuiElement>> itemConsumer);
  }

}




