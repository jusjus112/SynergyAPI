package usa.synergy.utilities.assets.utilities;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

@Getter
public class InventoryBackup {

  private final Map<Integer, ItemStack> cachedItems;

  @Nullable
  private InventoryHolder inventoryHolder;
  private final InventoryType inventoryType;

  /**
   *
   */
  public InventoryBackup() {
    this(InventoryType.PLAYER);
  }

  /**
   *
   * @param inventoryType
   */
  public InventoryBackup(InventoryType inventoryType) {
    this.cachedItems = Maps.newHashMap();
    this.inventoryType = inventoryType;
  }

  /**
   *
   * @param inventory
   */
  private void backup(Inventory inventory) {
    this.inventoryHolder = inventory.getHolder();

    for (int i = 0; i < 104; i++) {
      ItemStack item = inventory.getItem(i);
      if (item != null && item.getType() != Material.AIR) {
        this.cachedItems.put(i, item);
        inventory.remove(item);
      }
    }
  }

  /**
   *
   * @return
   */
  public Inventory restore() {
    Validate.notNull(this.inventoryHolder, "Cannot restore new inventory while holder is null.");
    return restoreTo(Bukkit.createInventory(this.inventoryHolder, InventoryType.PLAYER));
  }

  /**
   *
   * @param inventory
   * @return
   */
  public Inventory restoreTo(Inventory inventory) {
    Validate.notEmpty(this.cachedItems, "Cannot restore inventory when cache is empty.");

    if (inventory.getContents().length > 0)
      inventory.clear();
    this.cachedItems.forEach(inventory::setItem);

    return inventory;
  }

}
