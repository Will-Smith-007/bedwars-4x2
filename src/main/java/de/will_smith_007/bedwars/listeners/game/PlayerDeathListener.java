package de.will_smith_007.bedwars.listeners.game;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Collection;
import java.util.Optional;

public class PlayerDeathListener implements Listener {

    private final GameAssets GAME_ASSETS;
    private final ITeamHelper TEAM_HELPER;
    private final BedWarsConfig BED_WARS_CONFIG = BedWarsConfig.getInstance();

    public PlayerDeathListener(@NonNull GameAssets gameAssets,
                               @NonNull ITeamHelper teamHelper) {
        GAME_ASSETS = gameAssets;
        TEAM_HELPER = teamHelper;
    }

    @EventHandler
    public void onPlayerDeath(@NonNull PlayerDeathEvent playerDeathEvent) {
        final GameState gameState = GAME_ASSETS.getGameState();

        if (gameState == GameState.LOBBY) return;

        playerDeathEvent.setCancelled(true);
        playerDeathEvent.deathMessage(null);

        final Player player = playerDeathEvent.getPlayer();
        final World playerWorld = player.getWorld();
        final Optional<ITeam> optionalITeam = TEAM_HELPER.getTeam(player);

        optionalITeam.ifPresent(iTeam -> {
            final boolean bedExists = iTeam.bedExists(playerWorld);
            final Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

            for (Player onlinePlayer : onlinePlayers) {
                onlinePlayer.sendMessage(Component.text(Message.PREFIX + " ")
                        .append(player.displayName())
                        .append(Component.text("§7 died.")));
            }

            if (bedExists) {
                final Location teamSpawnLocation = iTeam.getTeamSpawnLocation(playerWorld);
                player.teleport(teamSpawnLocation);
            } else {
                iTeam.removePlayer(player);
                final Location spectatorLocation = BED_WARS_CONFIG.getSpectatorLocation(playerWorld);
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(spectatorLocation);
                TEAM_HELPER.handleTeamElimination(iTeam, onlinePlayers);
            }
        });
    }
}
