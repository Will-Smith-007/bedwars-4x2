package de.will_smith_007.bedwars.shop.parser;

import de.will_smith_007.bedwars.shop.enums.ShopItem;
import de.will_smith_007.bedwars.shop.parser.interfaces.IShopParser;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class ShopParser implements IShopParser {

    private final ShopItem[] shopItems = ShopItem.values();
    private final ShopItem.ShopCategory[] shopCategories = ShopItem.ShopCategory.values();

    @Override
    public Optional<ShopItem> parseShopItem(@NonNull ItemStack itemStack) {
        for (ShopItem shopItem : shopItems) {
            final ItemStack shopItemStack = shopItem.buildItem();
            if (itemStack.equals(shopItemStack)) return Optional.of(shopItem);
        }
        return Optional.empty();
    }

    @Override
    public ShopItem.ShopCategory parseShopCategory(@NonNull ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final Component displayNameComponent = itemMeta.displayName();

        if (displayNameComponent == null) return null;

        final String displayName = ((TextComponent) displayNameComponent).content();

        for (ShopItem.ShopCategory shopCategory : shopCategories) {
            final String categoryName = shopCategory.getCategoryName();
            if (categoryName.equals(displayName)) return shopCategory;
        }

        return null;
    }
}
