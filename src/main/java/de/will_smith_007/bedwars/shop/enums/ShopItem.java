package de.will_smith_007.bedwars.shop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
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

    private final String displayName;
    private final Material material;
    private final ShopCategory shopCategory;
    private final CurrencyType currencyType;
    private final int price, defaultItems;

    private Enchantment enchantment;
    private int enchantmentStrength;

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
