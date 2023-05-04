package de.will_smith_007.bedwars.listeners.game;

import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.inventories.interfaces.IBedWarsInventory;
import de.will_smith_007.bedwars.shop.enums.ShopItem;
import de.will_smith_007.bedwars.shop.parser.interfaces.IShopParser;
import lombok.NonNull;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.*;

public class ShopListener implements Listener {

    private final IBedWarsInventory BED_WARS_INVENTORY;
    private final IShopParser SHOP_PARSER;
    private final ShopItem[] SHOP_ITEMS = ShopItem.values();

    public ShopListener(@NonNull IBedWarsInventory bedWarsInventory,
                        @NonNull IShopParser shopParser) {
        BED_WARS_INVENTORY = bedWarsInventory;
        SHOP_PARSER = shopParser;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(@NonNull PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
        if (playerInteractAtEntityEvent.getHand() != EquipmentSlot.HAND) return;
        final Entity rightClickedEntity = playerInteractAtEntityEvent.getRightClicked();
        if (rightClickedEntity.getType() != EntityType.VILLAGER) return;

        final Player player = playerInteractAtEntityEvent.getPlayer();

        BED_WARS_INVENTORY.openInventory(player);
        playerInteractAtEntityEvent.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(@NonNull InventoryClickEvent inventoryClickEvent) {
        final Inventory clickedInventory = inventoryClickEvent.getClickedInventory();

        if (clickedInventory == null) return;
        if (inventoryClickEvent.getSlotType() == InventoryType.SlotType.OUTSIDE) return;

        final InventoryView inventoryView = inventoryClickEvent.getView();

        final String inventoryName = PlainTextComponentSerializer.plainText().serialize(inventoryView.title());

        if (inventoryName.equals(BED_WARS_INVENTORY.getInventoryName())) {
            inventoryClickEvent.setCancelled(true);

            final ItemStack itemStack = inventoryClickEvent.getCurrentItem();
            final Player player = (Player) inventoryClickEvent.getWhoClicked();
            final PlayerInventory playerInventory = player.getInventory();

            if (clickedInventory == playerInventory) {
                inventoryClickEvent.setCancelled(false);
                return;
            }

            if (itemStack == null) return;
            if (!itemStack.hasItemMeta()) return;

            SHOP_PARSER.parseShopItem(itemStack).ifPresentOrElse(shopItem -> {
                final ShopItem.CurrencyType currencyType = shopItem.getCurrencyType();
                final String currencyTypeName = currencyType.getCurrencyName();
                final int price = shopItem.getPrice();

                final Material currencyMaterial;

                switch (currencyType) {
                    case IRON -> currencyMaterial = Material.IRON_INGOT;
                    case GOLD -> currencyMaterial = Material.GOLD_INGOT;
                    default -> currencyMaterial = Material.BRICK;
                }

                if (inventoryClickEvent.isShiftClick()) {
                    final ItemStack shopItemStack = shopItem.buildItem();
                    final int freeSpace = getFreeInventorySpace(player, shopItemStack);
                    final int currencyItemAmount = getCurrencyItems(player, currencyMaterial);
                    final int defaultItems = shopItem.getDefaultItems();
                    final int pricePerItem = shopItem.getPrice();
                    final int itemsThatCanBuy = (currencyItemAmount / pricePerItem);
                    final int resultItems = (defaultItems * itemsThatCanBuy);
                    final int itemsActuallyBuy = Math.min((freeSpace + currencyItemAmount), resultItems);

                    //TODO: if resultItems higher than space than idk. This method isn't working
                    if (resultItems > (freeSpace + currencyItemAmount)) {
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);
                        return;
                    }

                    final ItemStack currencyItemStack = new ItemStack(currencyMaterial);
                    currencyItemStack.setAmount((itemsActuallyBuy * pricePerItem));

                    shopItemStack.setAmount(itemsActuallyBuy);

                    playerInventory.removeItem(currencyItemStack);
                    playerInventory.addItem(shopItemStack);
                } else {
                    final int currencyItemAmount = getCurrencyItems(player, currencyMaterial);

                    if (currencyItemAmount < price) {
                        player.sendPlainMessage(Message.PREFIX + "§cYou don't have enough §e" + currencyTypeName + "§c.");
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);
                        return;
                    }

                    final ItemStack shopItemStack = shopItem.buildItem();
                    final int freeSpace = getFreeInventorySpace(player, shopItemStack);
                    final int defaultItems = shopItem.getDefaultItems();

                    if (freeSpace < defaultItems) {
                        player.sendPlainMessage(Message.PREFIX + "§cYou don't have enough§e inventory space§c left.");
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);
                        return;
                    }

                    final ItemStack currencyItemStack = new ItemStack(currencyMaterial);
                    currencyItemStack.setAmount(price);

                    shopItemStack.editMeta(itemMeta -> itemMeta.lore(null));

                    playerInventory.removeItem(currencyItemStack);
                    playerInventory.addItem(shopItemStack);
                }
            }, () -> {
                final ShopItem.ShopCategory shopCategory = SHOP_PARSER.parseShopCategory(itemStack);
                if (shopCategory == null) return;

                for (int clearingIndex = 18; clearingIndex != 27; clearingIndex++) {
                    clickedInventory.setItem(clearingIndex, null);
                }

                int currentSlot = 18;
                for (ShopItem shopItem : SHOP_ITEMS) {
                    if (shopItem.getShopCategory() != shopCategory) continue;
                    final int price = shopItem.getPrice();
                    final ShopItem.CurrencyType currencyType = shopItem.getCurrencyType();
                    final String currencyTypeName = currencyType.getCurrencyName();

                    final ItemStack shopItemStack = shopItem.toItem()
                            .setLore(
                                    "",
                                    "§e" + price + " " + currencyTypeName,
                                    ""
                            ).buildItem();

                    clickedInventory.setItem(currentSlot, shopItemStack);
                    currentSlot++;
                }
            });
        }
    }

    private int getFreeInventorySpace(@NonNull Player player,
                                      @NonNull ItemStack shopItem) {
        final ItemStack[] inventoryContents = player.getInventory().getStorageContents();
        int freeSpace = 0;

        for (ItemStack inventoryContent : inventoryContents) {
            if (inventoryContent == null || inventoryContent.getType() == Material.AIR) {
                freeSpace += 64;
            } else if (inventoryContent.isSimilar(shopItem)) {
                freeSpace += (shopItem.getMaxStackSize() - inventoryContent.getAmount());
            }
        }

        return freeSpace;
    }

    private int getCurrencyItems(@NonNull Player player, @NonNull Material currencyMaterial) {
        final ItemStack[] inventoryContents = player.getInventory().getContents();
        int itemCount = 0;

        for (ItemStack inventoryContent : inventoryContents) {
            if (inventoryContent == null) continue;
            if (inventoryContent.getType() != currencyMaterial) continue;
            itemCount += inventoryContent.getAmount();
        }

        return itemCount;
    }
}
