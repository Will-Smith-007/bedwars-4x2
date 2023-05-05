package de.will_smith_007.bedwars.inventories.game;

import de.will_smith_007.bedwars.inventories.interfaces.IBedWarsInventory;
import de.will_smith_007.bedwars.shop.enums.ShopItem;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BedWarsShopInventory implements IBedWarsInventory {

    private final String INVENTORY_NAME = "Shop";
    private final ShopItem[] SHOP_ITEMS = ShopItem.values();
    private final ShopItem.ShopCategory[] SHOP_CATEGORIES = ShopItem.ShopCategory.values();

    @Override
    public void openInventory(@NonNull Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 9 * 3,
                Component.text(INVENTORY_NAME, NamedTextColor.YELLOW));

        int currentSlot = 0;

        for (ShopItem.ShopCategory shopCategory : SHOP_CATEGORIES) {
            final Material material = shopCategory.getMaterial();
            final ItemStack itemStack = new ItemStack(material);
            final String categoryName = shopCategory.getCategoryName();

            itemStack.editMeta(itemMeta ->
                    itemMeta.displayName(Component.text(categoryName, NamedTextColor.YELLOW)
                            .decoration(TextDecoration.ITALIC, false)));

            inventory.setItem(currentSlot, itemStack);
            currentSlot++;
        }

        final ItemStack glassItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        glassItem.editMeta(itemMeta -> itemMeta.displayName(Component.text("")));

        for (int index = 9; index != 18; index++) {
            inventory.setItem(index, glassItem);
        }

        currentSlot = 18;

        for (ShopItem shopItem : SHOP_ITEMS) {
            final ShopItem.ShopCategory shopCategory = shopItem.getShopCategory();
            if (shopCategory != ShopItem.ShopCategory.BLOCKS) continue;
            final ShopItem.CurrencyType currencyType = shopItem.getCurrencyType();
            final String currencyTypeName = currencyType.getCurrencyName();
            final int price = shopItem.getPrice();
            final ItemStack itemStack = shopItem.toItem()
                    .setLore(
                            "",
                            "§e" + price + " " + currencyTypeName,
                            ""
                    ).buildItem();

            inventory.setItem(currentSlot, itemStack);
            currentSlot++;
        }

        player.openInventory(inventory);
    }

    @Override
    public @NonNull String getInventoryName() {
        return INVENTORY_NAME;
    }
}
