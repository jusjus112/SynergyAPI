package usa.synergy.utilities.assets.utilities;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.Nullable;
import usa.synergy.utilities.utlities.SynergyLogger;

public class ItemBuilder<I extends ItemBuilder> extends ItemStack {

  public ItemMeta itemMeta;
  @Getter
  private List<String> lore;
  @Getter
  private String itemName;

  public ItemBuilder(final Material material) {
    this(material, 1);
  }

  public ItemBuilder(final ItemStack stack) {
    super(stack);
    initItemMeta();
    if (this.itemMeta.hasLore()) {
      this.lore = this.itemMeta.getLore();
    }

//        if (stack instanceof CraftItemStack){
//
//        }
  }

  public ItemBuilder(final Material material, final int amount) {
    this(material, amount, (byte) 0);
  }

  public ItemBuilder(ItemBuilder itemBuilder){
    this(itemBuilder.build());
  }

  public ItemBuilder(final Material material, final int amount, final short damage) {
    super(material, amount, damage);
  }

  public static HashMap<String, Object> getDefaultConfigData(String prefix) {
    return new HashMap<>() {{
      put(prefix+".material", Material.STONE.name());
      put(prefix+".name", "&cDefault Name");
      put(prefix+".lore", Lists.newArrayList("&cDefault Lore"));
      put(prefix+".glow", false);
      put(prefix+".model", 0);
      put(prefix+".hideFlags", false);
      put(prefix+".enchantments", Map.of());
    }};
  }

  public static ItemBuilder fromConfig(@Nullable Player player, String prefix, ConfigurationSection fileConfiguration) {
    return fromConfig(player, prefix, fileConfiguration, (material, s) -> s);
  }

  public static ItemBuilder fromConfig(@Nullable Player player, String prefix, ConfigurationSection fileConfiguration, BiFunction<Material, String, String> editor) {
    try {
      String materialString = fileConfiguration.getString(prefix + ".material");
      ItemBuilder itemBuilder;

      if (materialString != null && materialString.contains("head")) {
        String username = "";
        if (player != null) {
          String name = materialString.split(":")[1];
          username = name.replaceAll("%player%", player.getName());
        }

        itemBuilder = new SkullItemBuilder(username);
      }else{
        Material material = Material.valueOf(materialString);
        itemBuilder = new ItemBuilder(material, 1);
      }
      String name = fileConfiguration.getString(prefix + ".name");
      List<String> lore = fileConfiguration.getStringList(prefix + ".lore");
      boolean glow = fileConfiguration.getBoolean(prefix + ".glow", false);
      boolean hideFlags = fileConfiguration.getBoolean(prefix + ".hideFlags", false);

      itemBuilder.setName(name).addLore(s -> editor.apply(itemBuilder.getType(), (String) s), lore);
      if(!(itemBuilder instanceof SkullItemBuilder) && glow) itemBuilder.glow();
      if(!(itemBuilder instanceof SkullItemBuilder) && hideFlags) itemBuilder.hideFlags();

      int modelData = fileConfiguration.getInt(prefix + ".model", 0);
      if (modelData != -1) {
        itemBuilder.setModelData(modelData);
      }

      if (fileConfiguration.contains(prefix + ".enchantments")){
        ConfigurationSection enchantments = fileConfiguration.getConfigurationSection(prefix + ".enchantments");
        if (enchantments != null){
          for (String enchantment : enchantments.getKeys(false)){
            if (enchantment != null) {
              int level = enchantments.getInt(enchantment);
              itemBuilder.addUnsafeEnchantment(Enchantment.getByName(enchantment), level);
            }
          }
        }
      }

      return itemBuilder;
    } catch (Exception exception) {
      SynergyLogger.error(
          "Error while generating an item from config.",
          "For item with prefix: " + prefix,
          exception.getMessage()
      );
    }

    return new ItemBuilder(Material.AIR);
  }

  public ItemBuilder setName(String displayName) {
    initItemMeta();
    String t = ChatColor.translateAlternateColorCodes('&', displayName);
    this.itemName = displayName;
    this.itemMeta.setDisplayName(ChatColor.RESET + t);
    this.itemMeta.setLocalizedName(ChatColor.RESET + t);
    return this;
  }

  private void initItemMeta() {
    this.initItemMeta(getItemMeta());
  }

  public void initItemMeta(ItemMeta itemMeta) {
    if (this.itemMeta == null) {
      this.itemMeta = itemMeta;
    }
  }

  public ItemBuilder addLore(UnaryOperator<String> editor, List<String> lore) {
    initItemMeta();
    List<String> modifiedLore = new ArrayList<>();
    for (String line : lore) {
      String editorLine = editor.apply(line);
      String modifiedLine = UtilString.toBukkitColors(
          UtilString.translateHexColorCodes("", "", editorLine));
      if (editorLine.contains("\n")){
        modifiedLore.addAll(List.of(modifiedLine.split("\n")));
        continue;
      }
      modifiedLore.add("§7" + modifiedLine);
    }
    if (this.lore == null) {
      this.lore = Lists.newArrayList();
    }
    this.lore.addAll(modifiedLore);
    return this;
  }

  public ItemBuilder addLore(UnaryOperator<String> editor, String... lore) {
    initItemMeta();
    addLore(editor, Lists.newArrayList(lore));
    return this;
  }

//    public ItemBuilder setLore(List<String> lore){
//        addLore(lore.toArray(new String[lore.size()]));
//        return this;
//    }

  public ItemBuilder resetLore() {
    initItemMeta();
    this.itemMeta.setLore(Lists.newArrayList());
    this.lore = Lists.newArrayList();
    return this;
  }

  public ItemBuilder setDamage(short damage) {
    setDurability(damage);
    return this;
  }

//    public ItemBuilder addItemFlags(ItemFlag... itemFlags){
//        initItemMeta();
//        this.itemMeta.addItemFlags(itemFlags);
//        return this;
//    }

  public ItemBuilder setUnbreakable(boolean unbreakable) {
    initItemMeta();
    this.itemMeta.setUnbreakable(unbreakable);
//        this.itemMeta.spigot().setUnbreakable(true);
    return this;
  }

  public ItemBuilder setMaterialData(MaterialData materialData) {
    this.setData(materialData);
    return this;
  }

  public ItemBuilder hideEnchants() {
    initItemMeta();
    this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    return this;
  }

  public ItemBuilder setModelData(int modelData) {
    initItemMeta();
    this.itemMeta.setCustomModelData(modelData);
    return this;
  }

  public ItemBuilder hideFlags(){
    initItemMeta();
    this.itemMeta.addItemFlags(ItemFlag.values());
    return this;
  }

  public ItemBuilder glow() {
    initItemMeta();
    this.itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
    hideEnchants();
    return this;
  }

  public ItemStack build() {
    if (getType() == Material.AIR) {
      return this;
    }
    if (this.itemMeta != null) {
      this.itemMeta.setLore(this.lore);
      setItemMeta(this.itemMeta);
    }
    return this;
  }

}
