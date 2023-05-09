package de.will_smith_007.bedwars.lobby.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
@RequiredArgsConstructor
public enum LobbyItem {

    TEAM_SELECTOR("Select Team", Material.RED_BED);

    private final String displayName;
    private final Material material;

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    /**
     * Builds and gets the {@link ItemStack} of a lobby item value.
     *
     * @return The built ItemStack.
     * @apiNote Should not be used to compare items, use {@link LobbyItem#getItemStack()} instead.
     */
    public ItemStack buildItem() {
        if (itemStack == null) itemStack = new ItemStack(material);
        if (itemMeta == null) itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text(displayName, NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Gets the built {@link ItemStack} but without setting
     * the display name of the item if it's already present.
     *
     * @return The built ItemStack.
     * @apiNote Should be only used to compare {@link ItemStack}s.
     */
    public ItemStack getItemStack() {
        if (itemStack == null) itemStack = new ItemStack(material);

        itemMeta = itemStack.getItemMeta();

        if (itemMeta.displayName() == null) {
            itemMeta.displayName(Component.text(displayName, NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false));
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
