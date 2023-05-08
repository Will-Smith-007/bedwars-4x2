package de.will_smith_007.bedwars.listeners.game;

import com.google.inject.Inject;
import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Set;

public class GameChatListener implements Listener {

    private final GameAssets gameAssets;
    private final ITeamHelper teamHelper;

    @Inject
    public GameChatListener(@NonNull GameAssets gameAssets,
                            @NonNull ITeamHelper teamHelper) {
        this.gameAssets = gameAssets;
        this.teamHelper = teamHelper;
    }

    @EventHandler
    public void onGameChat(@NonNull AsyncChatEvent asyncChatEvent) {
        final GameState gameState = gameAssets.getGameState();
        final Player player = asyncChatEvent.getPlayer();
        final Scoreboard scoreboard = player.getScoreboard();
        final Team team = scoreboard.getPlayerTeam(player);

        if (team == null) return;

        final TextColor textColor = team.color();

        switch (gameState) {
            case LOBBY, ENDING -> asyncChatEvent.renderer((source, sourceDisplayName, message, viewer) ->
                    Component.text(player.getName()).color(textColor)
                            .append(Component.text(" §8» "))
                            .append(message.color(NamedTextColor.GRAY)));

            case PROTECTION, INGAME -> {
                final String message = ((TextComponent) asyncChatEvent.message()).content();
                asyncChatEvent.setCancelled(true);

                if (message.startsWith("@")) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.sendMessage(
                                Component.text("§7[§e@All§7] ")
                                        .append(Component.text(player.getName()).color(textColor))
                                        .append(Component.text(" §8» §7"))
                                        .append(Component.text(message.replace("@", "")))
                        );
                    }
                    return;
                }

                teamHelper.getTeam(player).ifPresent(iTeam -> {
                    final Set<Player> teamPlayers = iTeam.getPlayers();

                    for (Player teamPlayer : teamPlayers) {
                        teamPlayer.sendMessage(
                                Component.text(player.getName()).color(textColor)
                                        .append(Component.text(" §8» "))
                                        .append(Component.text(message, NamedTextColor.GRAY))
                        );
                    }
                });
            }
        }
    }
}
