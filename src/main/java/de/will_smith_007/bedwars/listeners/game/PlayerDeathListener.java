package de.will_smith_007.bedwars.listeners.game;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.scoreboard.interfaces.IScoreboardManager;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.Optional;

public class PlayerDeathListener implements Listener {

    private final GameAssets GAME_ASSETS;
    private final ITeamHelper TEAM_HELPER;
    private final IScoreboardManager SCOREBOARD_MANAGER;

    public PlayerDeathListener(@NonNull GameAssets gameAssets,
                               @NonNull ITeamHelper teamHelper,
                               @NonNull IScoreboardManager scoreboardManager) {
        GAME_ASSETS = gameAssets;
        TEAM_HELPER = teamHelper;
        SCOREBOARD_MANAGER = scoreboardManager;
    }

    @EventHandler
    public void onPlayerDeath(@NonNull PlayerDeathEvent playerDeathEvent) {
        final GameState gameState = GAME_ASSETS.getGameState();

        if (gameState == GameState.LOBBY) return;

        playerDeathEvent.setCancelled(true);
        playerDeathEvent.deathMessage(null);

        final Player player = playerDeathEvent.getPlayer();
        final Optional<ITeam> optionalITeam = TEAM_HELPER.getTeam(player);

        optionalITeam.ifPresent(iTeam -> {
            final Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

            final Scoreboard scoreboard = player.getScoreboard();
            final Team playerTeam = scoreboard.getPlayerTeam(player);

            if (playerTeam == null) return;

            final TextColor textColor = playerTeam.color();
            final String playerName = player.getName();
            final String prefix = Message.PREFIX.toString();

            for (Player onlinePlayer : onlinePlayers) {
                onlinePlayer.sendMessage(Component.text(prefix)
                        .append(Component.text(playerName).color(textColor))
                        .append(Component.text("§7 died.")));
            }
        });
    }
}
