package de.will_smith_007.bedwars.shop.parser.interfaces;

import de.will_smith_007.bedwars.shop.enums.ShopItem;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public interface IShopParser {

    /**
     * Parses a {@link ShopItem} by checking the {@link ItemStack}.
     *
     * @param itemStack The ItemStack which should be checked.
     * @return An {@link Optional} which contains a {@link ShopItem} or is empty
     * if there couldn't be found a {@link ShopItem} for this {@link ItemStack}.
     * Can't be null.
     */
    Optional<ShopItem> parseShopItem(@NonNull ItemStack itemStack);

    /**
     * Parsses a {@link de.will_smith_007.bedwars.shop.enums.ShopItem.ShopCategory} by checking the
     * {@link ItemStack}.
     *
     * @param itemStack The ItemStack which should be checked.
     * @return The {@link de.will_smith_007.bedwars.shop.enums.ShopItem.ShopCategory}
     * which was found for this {@link ItemStack} or null if there couldn't be found a ShopCategory.
     */
    ShopItem.ShopCategory parseShopCategory(@NonNull ItemStack itemStack);
}
