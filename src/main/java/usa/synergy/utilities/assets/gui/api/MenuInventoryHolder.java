package usa.synergy.utilities.assets.gui.api;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import usa.synergy.utilities.assets.gui.impl.Gui;

@Getter
public class MenuInventoryHolder implements InventoryHolder {

  private final Player viewer;
  private final Gui menu;
  private final boolean openParent;
  private Inventory inventory;
  @Setter
  private int pageNumber;

  @Setter
  private boolean transitingPageState;

  private Map<Integer, GuiElement> paginatedItems;

  public MenuInventoryHolder(Player viewer, Gui menu) {
    this.viewer = viewer;
    this.menu = menu;
    openParent = true;
  }

  @Override
  public Inventory getInventory() {
    return inventory;
  }

  public void setInventory(Inventory inventory) {
    Validate.isTrue(this.inventory == null, "Inventory is already set");
    this.inventory = inventory;
  }

  public void createPaginatedItems() {
    paginatedItems = new HashMap<>();
  }
}
