package de.will_smith_007.bedwars.shop.parser.interfaces;

import de.will_smith_007.bedwars.shop.enums.ShopItem;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public interface IShopParser {

    Optional<ShopItem> parseShopItem(@NonNull ItemStack itemStack);

    ShopItem.ShopCategory parseShopCategory(@NonNull ItemStack itemStack);
}
