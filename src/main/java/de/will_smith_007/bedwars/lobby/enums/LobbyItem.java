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

    public ItemStack buildItem() {
        if (itemStack == null) itemStack = new ItemStack(material);
        if (itemMeta == null) itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text(displayName, NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false));

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

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
