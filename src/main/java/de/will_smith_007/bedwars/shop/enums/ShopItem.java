package de.will_smith_007.bedwars.shop.enums;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum ShopItem {

    SANDSTONE("Sandstone", Material.SANDSTONE, ShopCategory.BLOCKS,
            CurrencyType.BRONZE, 1, 2),
    GLOW_STONE("Glowstone", Material.GLOWSTONE, ShopCategory.BLOCKS,
            CurrencyType.BRONZE, 15, 4),
    END_STONE("Endstone", Material.END_STONE, ShopCategory.BLOCKS,
            CurrencyType.BRONZE, 7, 1),
    GLASS("Glass", Material.GLASS, ShopCategory.BLOCKS,
            CurrencyType.BRONZE, 4, 1),
    IRON_BLOCK("Ironblock", Material.IRON_BLOCK, ShopCategory.BLOCKS,
            CurrencyType.IRON, 3, 1),

    LEATHER_HELMET("Leather Helmet", Material.LEATHER_HELMET, ShopCategory.ARMOR,
            CurrencyType.BRONZE, 1, 1),
    LEATHER_LEGGINGS("Leather Leggings", Material.LEATHER_LEGGINGS, ShopCategory.ARMOR,
            CurrencyType.BRONZE, 1, 1),
    LEATHER_BOOTS("Leather Boots", Material.LEATHER_BOOTS, ShopCategory.ARMOR,
            CurrencyType.BRONZE, 1, 1),
    CHAIN_CHESTPLATE_PROTECTION_ONE("Chain Chestplate", Material.CHAINMAIL_CHESTPLATE, ShopCategory.ARMOR,
            CurrencyType.IRON, 1, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1),
    CHAIN_CHESTPLATE_PROTECTION_TWO("Chain Chestplate", Material.CHAINMAIL_CHESTPLATE, ShopCategory.ARMOR,
            CurrencyType.IRON, 3, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 2),
    CHAIN_CHESTPLATE_PROTECTION_THREE("Chain Chestplate", Material.CHAINMAIL_CHESTPLATE, ShopCategory.ARMOR,
            CurrencyType.IRON, 7, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 3),
    GOLDEN_BOOTS("Golden Boots", Material.GOLDEN_BOOTS, ShopCategory.ARMOR,
            CurrencyType.IRON, 3, 1),

    WOODEN_PICKAXE("Wooden Pickaxe", Material.WOODEN_PICKAXE, ShopCategory.TOOLS,
            CurrencyType.BRONZE, 4, 1, Enchantment.DIG_SPEED, 1),
    STONE_PICKAXE("Stone Pickaxe", Material.STONE_PICKAXE, ShopCategory.TOOLS,
            CurrencyType.IRON, 2, 1, Enchantment.DIG_SPEED, 1),
    IRON_PICKAXE("Iron Pickaxe", Material.IRON_PICKAXE, ShopCategory.TOOLS,
            CurrencyType.GOLD, 1, 1, Enchantment.DIG_SPEED, 1),

    STICK("Stick", Material.STICK, ShopCategory.WEAPONS,
            CurrencyType.BRONZE, 8, 1, Enchantment.KNOCKBACK, 1),
    GOLDEN_SWORD_SHARPNESS_ONE("Golden Sword", Material.GOLDEN_SWORD, ShopCategory.WEAPONS,
            CurrencyType.IRON, 1, 1, Enchantment.DAMAGE_ALL, 1),
    GOLDEN_SWORD_SHARPNESS_TWO("Golden Sword", Material.GOLDEN_SWORD, ShopCategory.WEAPONS,
            CurrencyType.IRON, 3, 1),
    IRON_SWORD_SHARPNESS_ONE("Iron Sword", Material.IRON_SWORD, ShopCategory.WEAPONS,
            CurrencyType.GOLD, 5, 1, Enchantment.DAMAGE_ALL, 1),

    BOW_STRENGTH_ONE("Bow", Material.BOW, ShopCategory.BOWS,
            CurrencyType.GOLD, 3, 1, Enchantment.ARROW_DAMAGE, 1),
    BOW_STRENGTH_TWO("Bow", Material.BOW, ShopCategory.BOWS,
            CurrencyType.GOLD, 7, 1, Enchantment.ARROW_DAMAGE, 2),
    BOW_STRENGTH_THREE("Bow", Material.BOW, ShopCategory.BOWS,
            CurrencyType.GOLD, 13, 1, Enchantment.ARROW_DAMAGE, 3),
    ARROW("Arrow", Material.ARROW, ShopCategory.BOWS,
            CurrencyType.GOLD, 1, 1),

    APPLE("Apple", Material.APPLE, ShopCategory.FOOD,
            CurrencyType.BRONZE, 1, 1),
    COOKED_BEEF("Steak", Material.COOKED_BEEF, ShopCategory.FOOD,
            CurrencyType.BRONZE, 2, 1),
    CAKE("Cake", Material.CAKE, ShopCategory.FOOD,
            CurrencyType.IRON, 1, 1),
    GOLDEN_APPLE("Golden Apple", Material.GOLDEN_APPLE, ShopCategory.FOOD,
            CurrencyType.GOLD, 2, 1),

    CHEST("Chest", Material.CHEST, ShopCategory.CHESTS,
            CurrencyType.IRON, 1, 1),
    TEAM_CHEST("Team Chest", Material.ENDER_CHEST, ShopCategory.CHESTS,
            CurrencyType.GOLD, 1, 1),

    INSTANT_HEAL("Instant heal", Material.POTION, ShopCategory.POTIONS,
            CurrencyType.IRON, 3, 1, PotionType.INSTANT_HEAL, false, false),
    INSTANT_HEAL_TWO("Instant heal", Material.POTION, ShopCategory.POTIONS,
            CurrencyType.IRON, 5, 1, PotionType.INSTANT_HEAL, false, true),
    REGENERATION("Regeneration", Material.POTION, ShopCategory.POTIONS,
            CurrencyType.IRON, 7, 1, PotionType.REGEN, false, false),
    JUMP("Jump", Material.POTION, ShopCategory.POTIONS,
            CurrencyType.IRON, 7, 1, PotionType.JUMP, false, false),
    REGENERATION_TWO("Regeneration", Material.POTION, ShopCategory.POTIONS,
            CurrencyType.IRON, 36, 1, PotionType.REGEN, false, true),
    REGENERATION_LONG_DURATION("Regeneration", Material.POTION, ShopCategory.POTIONS,
            CurrencyType.GOLD, 7, 1, PotionType.REGEN, true, false),

    LADDER("Ladder", Material.LADDER, ShopCategory.SPECIALS,
            CurrencyType.BRONZE, 1, 1),
    COBWEB("Cobweb", Material.COBWEB, ShopCategory.SPECIALS,
            CurrencyType.BRONZE, 16, 1),
    FISHING_ROD("Fishing Rod", Material.FISHING_ROD, ShopCategory.SPECIALS,
            CurrencyType.IRON, 5, 1),
    FLINT_AND_STEEL("Flint and Steel", Material.FLINT_AND_STEEL, ShopCategory.SPECIALS,
            CurrencyType.IRON, 7, 1),
    ENDER_PEARL("Enderpearl", Material.ENDER_PEARL, ShopCategory.SPECIALS,
            CurrencyType.GOLD, 13, 1);

    ShopItem(@NonNull String displayName, @NonNull Material material, @NonNull ShopCategory shopCategory,
             @NonNull CurrencyType currencyType, int price, int defaultItems) {
        this.displayName = displayName;
        this.material = material;
        this.shopCategory = shopCategory;
        this.currencyType = currencyType;
        this.price = price;
        this.defaultItems = defaultItems;
    }

    ShopItem(@NonNull String displayName, @NonNull Material material, @NonNull ShopCategory shopCategory,
             @NonNull CurrencyType currencyType, int price, int defaultItems,
             @NonNull Enchantment enchantment, int enchantmentStrength) {
        this.displayName = displayName;
        this.material = material;
        this.shopCategory = shopCategory;
        this.currencyType = currencyType;
        this.price = price;
        this.defaultItems = defaultItems;
        this.enchantment = enchantment;
        this.enchantmentStrength = enchantmentStrength;
    }

    ShopItem(@NonNull String displayName, @NonNull Material material, @NonNull ShopCategory shopCategory,
             @NonNull CurrencyType currencyType, int price, int defaultItems,
             @NonNull PotionType potionType, boolean isExtended, boolean isUpgraded) {
        this.displayName = displayName;
        this.material = material;
        this.shopCategory = shopCategory;
        this.currencyType = currencyType;
        this.price = price;
        this.defaultItems = defaultItems;
        this.potionType = potionType;
        this.isExtended = isExtended;
        this.isUpgraded = isUpgraded;
    }

    private final String displayName;
    private final Material material;
    private final ShopCategory shopCategory;
    private final CurrencyType currencyType;
    private final int price, defaultItems;

    private Enchantment enchantment;
    private int enchantmentStrength;

    private PotionType potionType;
    private boolean isExtended, isUpgraded;

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ShopItem toItem() {
        itemStack = new ItemStack(material);
        itemMeta = itemStack.getItemMeta();
        return this;
    }

    public ShopItem setLore(String @NonNull ... lore) {
        final List<Component> components = new ArrayList<>();
        for (String loreLine : lore) {
            components.add(Component.text(loreLine));
        }
        itemMeta.lore(components);
        return this;
    }

    public ItemStack buildItem() {
        if (itemStack == null) itemStack = new ItemStack(material);
        if (itemMeta == null) itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text(displayName, NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false));

        if (enchantment != null) {
            itemMeta.addEnchant(enchantment, enchantmentStrength, true);
        }

        if (potionType != null) {
            final PotionMeta potionMeta = (PotionMeta) itemMeta;
            final PotionData potionData = new PotionData(potionType, isExtended, isUpgraded);
            potionMeta.setBasePotionData(potionData);
        }

        itemStack.setAmount(defaultItems);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack getItemStack() {
        if (itemStack == null) itemStack = new ItemStack(material);

        itemMeta = itemStack.getItemMeta();

        if (itemMeta.displayName() == null) {
            itemMeta.displayName(Component.text(displayName, NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false));
        }

        if (enchantment != null) {
            itemMeta.addEnchant(enchantment, enchantmentStrength, true);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Getter
    @RequiredArgsConstructor
    public enum ShopCategory {
        BLOCKS("Blocks", Material.SANDSTONE),
        ARMOR("Armor", Material.CHAINMAIL_CHESTPLATE),
        TOOLS("Tools", Material.STONE_PICKAXE),
        WEAPONS("Weapons", Material.GOLDEN_SWORD),
        BOWS("Bows", Material.BOW),
        FOOD("Food", Material.APPLE),
        CHESTS("Chests", Material.CHEST),
        POTIONS("Potions", Material.POTION),
        SPECIALS("Specials", Material.TNT);

        private final String categoryName;
        private final Material material;
    }

    @Getter
    @RequiredArgsConstructor
    public enum CurrencyType {
        BRONZE("Bronze", Material.BRICK),
        IRON("Iron", Material.IRON_INGOT),
        GOLD("Gold", Material.GOLD_INGOT);

        private final String currencyName;
        private final Material material;
    }
}
