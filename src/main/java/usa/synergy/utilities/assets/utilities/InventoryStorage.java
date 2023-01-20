package usa.synergy.utilities.assets.utilities;

import com.google.common.collect.Maps;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

@Getter
public class InventoryStorage {

  private final Map<Integer, ItemStack> cachedItems;

  @Nullable
  private InventoryHolder inventoryHolder;
  private final InventoryType inventoryType;

  /**
   *
   */
  public InventoryStorage() {
    this(InventoryType.PLAYER);
  }

  /**
   *
   * @param inventoryType
   */
  public InventoryStorage(InventoryType inventoryType) {
    this.cachedItems = Maps.newHashMap();
    this.inventoryType = inventoryType;
  }

  /**
   *
   * @param inventory
   */
  public void save(Inventory inventory) {
    this.inventoryHolder = inventory.getHolder();

    for (int i = 0; i < inventory.getContents().length; i++) {
      this.cachedItems.put(i, inventory.getContents()[i]);
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
   * Retrieves the contents of the inventory from the provided base64 string.
   *
   * @param base64 Base64 string of data containing the inventory's contents.
   * @return Inventory with contents from the base64 string.
   * @throws IOException If there is an issue with reading or writing the data.
   */
  public Inventory restoreFromBase64(@NotNull String base64) throws IOException {
    ByteArrayInputStream in = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
    BukkitObjectInputStream dataInput = new BukkitObjectInputStream(in);

    for (int i = 0; i < in.available(); i++) {
      ItemStack object = null;

      try{
        object = (ItemStack) dataInput.readObject();
      }catch (Exception ignored){}

      cachedItems.put(i, object);
    }
    dataInput.close();

    return restore();
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

  /**
   * Encode the contents of the inventory to a Base64 string.
   * Useful for storing the contents in a database or to reduce file storage.
   * This will also protect the contents from being modified.
   *
   * @return Base64 string of the contents.
   * @see Base64Coder#encodeLines(byte[])
   * @throws IOException When writing fails of the contens.
   */
  public String exportToBase64String() throws IOException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(out);

    // Loop through the inventory and write every item to the stream.
    cachedItems.forEach((integer, itemStack) -> {
      try {
        bukkitObjectOutputStream.writeObject(itemStack);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    bukkitObjectOutputStream.close();

    return Base64Coder.encodeLines(out.toByteArray());
  }

}
