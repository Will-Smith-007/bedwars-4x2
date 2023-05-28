package de.will_smith_007.bedwars.teams.helper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.schedulers.EndingCountdownScheduler;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

@Singleton
public class TeamHelper implements ITeamHelper {

    private final GameAssets gameAssets;
    private final EndingCountdownScheduler endingCountdownScheduler;
    private final BedWarsConfig bedWarsConfig;

    @Inject
    public TeamHelper(@NonNull GameAssets gameAssets,
                      @NonNull EndingCountdownScheduler endingCountdownScheduler,
                      @NonNull BedWarsConfig bedWarsConfig) {
        this.gameAssets = gameAssets;
        this.endingCountdownScheduler = endingCountdownScheduler;
        this.bedWarsConfig = bedWarsConfig;
    }

    @Override
    public void handleTeamElimination(@NonNull ITeam team, @NonNull Collection<? extends Player> players) {
        final Set<Player> teamPlayers = team.getPlayers();

        if (!teamPlayers.isEmpty()) return;

        for (Player player : players) {
            player.sendPlainMessage(Message.PREFIX + team.getTeamName() + "§c was§4 eliminated§c!");
        }

        final List<ITeam> aliveTeams = Arrays.stream(BED_WARS_TEAMS)
                .map(BedWarsTeam::getTeam)
                .filter(iTeam -> !iTeam.getPlayers().isEmpty())
                .toList();

        if (aliveTeams.size() == 1) {
            final ITeam winningTeam = aliveTeams.get(0);
            final String winningTeamName = winningTeam.getTeamName();
            final String lobbyWorldName = bedWarsConfig.getLobbyWorld();

            if (lobbyWorldName == null) return;

            final World lobbyWorld = Bukkit.getWorld(lobbyWorldName);

            if (lobbyWorld == null) return;

            final Location lobbySpawnLocation = lobbyWorld.getSpawnLocation();
            gameAssets.setGameState(GameState.ENDING);

            for (Player player : players) {
                player.teleport(lobbySpawnLocation);
                player.setGameMode(GameMode.ADVENTURE);
                player.setHealth(20.00d);
                player.setFoodLevel(20);
                player.getInventory().clear();
                player.sendPlainMessage(Message.PREFIX + winningTeamName + "§a has won the game!");
                final Location playerLocation = player.getLocation();
                player.playSound(playerLocation, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1.0f, 1.0f);
            }

            endingCountdownScheduler.start();
        }
    }

    @Override
    public ITeam selectBedWarsTeam(@NonNull Player player) {
        final Optional<ITeam> optionalMinITeam = Arrays.stream(BED_WARS_TEAMS)
                .map(BedWarsTeam::getTeam)
                .filter(iTeam -> iTeam.getPlayers().size() < 2)
                .min(Comparator.comparingInt(iTeam -> iTeam.getPlayers().size()));

        for (BedWarsTeam bedWarsTeam : BED_WARS_TEAMS) {
            final ITeam iTeam = bedWarsTeam.getTeam();
            final Set<Player> teamPlayers = iTeam.getPlayers();

            if (teamPlayers.isEmpty()) {
                iTeam.addPlayer(player);
                return iTeam;
            }
        }

        optionalMinITeam.ifPresent(iTeam -> iTeam.addPlayer(player));
        return (optionalMinITeam.orElse(null));
    }

    @Override
    public boolean canTeamJoined(@NonNull BedWarsTeam team) {
        final ITeam iTeam = team.getTeam();
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final int minPlayersPerTeam = (int) Math.ceil((double) players.size() / BED_WARS_TEAMS.length);
        final Set<Player> teamPlayers = iTeam.getPlayers();

        if (teamPlayers.isEmpty()) return true;

        return (teamPlayers.size() < minPlayersPerTeam);
    }

    @Override
    public Optional<ITeam> getTeam(@NonNull Player player) {
        return Arrays.stream(BED_WARS_TEAMS)
                .map(BedWarsTeam::getTeam)
                .filter(iTeam -> iTeam.getPlayers().contains(player))
                .findAny();
    }
}
