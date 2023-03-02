package usa.synergy.utilities.assets.utilities;

import com.google.common.collect.Maps;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import usa.synergy.utilities.utlities.SynergyLogger;

@Getter
public class InventoryStorage {

  private final Map<Integer, @Nullable ItemStack> cachedItems;
  private Predicate<ItemStack> rules = itemStack -> true;

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
   */
  public InventoryStorage(Predicate<ItemStack> rules) {
    this(InventoryType.PLAYER);

    this.rules = rules;
  }

  /**
   *
   */
  public InventoryStorage(@NonNull InventoryHolder inventoryHolder) {
    this(InventoryType.PLAYER);

    this.inventoryHolder = inventoryHolder;
  }

  /**
   *
   * @return
   */
  public boolean isEmpty() {
    if (cachedItems.isEmpty()){
      return true;
    }

    SynergyLogger.debug("IS all null?: " + cachedItems.values().stream().allMatch(Objects::isNull));

    return cachedItems.values().stream().allMatch(Objects::isNull);
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
      ItemStack itemStack = inventory.getContents()[i];

      if (itemStack != null && !this.rules.test(itemStack)) {
        continue;
      }

      this.cachedItems.put(i, inventory.getContents()[i]);
    }
  }

  public boolean save(@NotNull ItemStack itemStack){
    if (!this.rules.test(itemStack)) {
      return false;
    }

    for (Entry<Integer, ItemStack> integerItemStackEntry : this.cachedItems.entrySet()) {
      ItemStack item = integerItemStackEntry.getValue();

      if (itemStack.getType() == item.getType() && itemStack.hasItemMeta() && item.hasItemMeta() &&
          itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(item.getItemMeta().getDisplayName())) {
        ItemStack cachedItemStack = integerItemStackEntry.getValue();

        SynergyLogger.debug("Adding " + itemStack.getAmount() + " to " + cachedItemStack.getAmount());
        this.cachedItems.replace(integerItemStackEntry.getKey(), cachedItemStack.add(itemStack.getAmount()));
        return true;
      }
    }

    SynergyLogger.debug("Putting  " + itemStack.getAmount() + " items in cache.");
    this.cachedItems.put(cachedItems.size(), itemStack);

    return true;
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
  public void restoreFromBase64(@NotNull String base64) throws IOException {
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
