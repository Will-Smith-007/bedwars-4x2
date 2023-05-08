package de.will_smith_007.bedwars.listeners.game;

import com.google.inject.Inject;
import com.google.inject.name.Named;
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

    private final IBedWarsInventory bedWarsInventory;
    private final IShopParser shopParser;
    private final ShopItem[] shopItems = ShopItem.values();

    @Inject
    public ShopListener(@NonNull @Named("ShopInventory") IBedWarsInventory bedWarsInventory,
                        @NonNull IShopParser shopParser) {
        this.bedWarsInventory = bedWarsInventory;
        this.shopParser = shopParser;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(@NonNull PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
        if (playerInteractAtEntityEvent.getHand() != EquipmentSlot.HAND) return;
        final Entity rightClickedEntity = playerInteractAtEntityEvent.getRightClicked();
        if (rightClickedEntity.getType() != EntityType.VILLAGER) return;

        final Player player = playerInteractAtEntityEvent.getPlayer();

        bedWarsInventory.openInventory(player);
        playerInteractAtEntityEvent.setCancelled(true);
    }

    @EventHandler
    public void onShopInventoryClick(@NonNull InventoryClickEvent inventoryClickEvent) {
        final Inventory clickedInventory = inventoryClickEvent.getClickedInventory();

        if (clickedInventory == null) return;
        if (inventoryClickEvent.getSlotType() == InventoryType.SlotType.OUTSIDE) return;

        final InventoryView inventoryView = inventoryClickEvent.getView();

        final String inventoryName = PlainTextComponentSerializer.plainText().serialize(inventoryView.title());

        if (inventoryName.equals(bedWarsInventory.getInventoryName())) {
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

            // Check if the clicked ItemStack is a shop item
            shopParser.parseShopItem(itemStack).ifPresentOrElse(shopItem -> {
                final ShopItem.CurrencyType currencyType = shopItem.getCurrencyType();
                final String currencyTypeName = currencyType.getCurrencyName();
                final int price = shopItem.getPrice();

                final Material currencyMaterial;

                // Check which currency is needed for this shop item
                switch (currencyType) {
                    case IRON -> currencyMaterial = Material.IRON_INGOT;
                    case GOLD -> currencyMaterial = Material.GOLD_INGOT;
                    default -> currencyMaterial = Material.BRICK;
                }

                // If player makes a shift click then the player buys so many items how he can buy
                if (inventoryClickEvent.isShiftClick()) {
                    final ItemStack shopItemStack = shopItem.buildItem();
                    // Available free space in the player inventory
                    final int freeSpace = getFreeInventorySpace(player, shopItemStack);
                    // How many bricks, iron ingots or gold ingots the player have in their own inventory
                    final int currencyItemAmount = getCurrencyItems(player, currencyMaterial);
                    // Amount of shop items that should be added by default per transaction
                    final int defaultItems = shopItem.getDefaultItems();
                    // How many bricks, iron ingots or gold ingots the shop item costs
                    final int pricePerItem = shopItem.getPrice();
                    // How often the player can make this transaction
                    final int howOftenPlayerCanBuyItem = (currencyItemAmount / pricePerItem);
                    // Amount of items that would be added to the player inventory after transaction
                    final int resultItems = (defaultItems * howOftenPlayerCanBuyItem);
                    final int itemsActuallyBuy = Math.min((freeSpace + currencyItemAmount), resultItems);

                    player.sendPlainMessage("items: " + itemsActuallyBuy);

                    /*if (itemsActuallyBuy < resultItems) {
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);
                        return;
                    }*/

                    final ItemStack currencyItemStack = new ItemStack(currencyMaterial);
                    currencyItemStack.setAmount((itemsActuallyBuy * pricePerItem));

                    shopItemStack.setAmount(itemsActuallyBuy);
                    shopItemStack.editMeta(itemMeta -> itemMeta.lore(null));

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
                final ShopItem.ShopCategory shopCategory = shopParser.parseShopCategory(itemStack);
                if (shopCategory == null) return;

                for (int clearingIndex = 18; clearingIndex != 27; clearingIndex++) {
                    clickedInventory.setItem(clearingIndex, null);
                }

                int currentSlot = 18;
                for (ShopItem shopItem : shopItems) {
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
