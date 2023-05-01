package de.will_smith_007.bedwars.lobby_countdown;

import de.will_smith_007.bedwars.lobby_countdown.interfaces.ILobbyCountdownHelper;
import de.will_smith_007.bedwars.schedulers.LobbyCountdownScheduler;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LobbyCountdownHelper implements ILobbyCountdownHelper {

    private final LobbyCountdownScheduler LOBBY_COUNTDOWN_SCHEDULER;
    private final BedWarsTeam[] BED_WARS_TEAMS = BedWarsTeam.values();

    public LobbyCountdownHelper(@NonNull LobbyCountdownScheduler lobbyCountdownScheduler) {
        LOBBY_COUNTDOWN_SCHEDULER = lobbyCountdownScheduler;
    }

    @Override
    public void startCountdownIfEnoughPlayers() {
        if (LOBBY_COUNTDOWN_SCHEDULER.isRunning()) return;

        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final int playerSize = players.size();

        final List<ITeam> iTeams = Arrays.stream(BED_WARS_TEAMS)
                .map(BedWarsTeam::getTeam)
                .filter(team -> team.getPlayers().size() > 0)
                .toList();

        if (playerSize < 2) return;
        if (iTeams.size() < 2) return;

        LOBBY_COUNTDOWN_SCHEDULER.start();
    }

    @Override
    public boolean shortenCountdown() {
        if (!LOBBY_COUNTDOWN_SCHEDULER.isRunning()) return false;
        if (LOBBY_COUNTDOWN_SCHEDULER.getCountdown() <= 10) return false;

        LOBBY_COUNTDOWN_SCHEDULER.setCountdown(10);

        return true;
    }

    @Override
    public void cancelCountdownIfNotEnoughPlayers(int players) {
        if (!LOBBY_COUNTDOWN_SCHEDULER.isRunning()) return;

        if (players >= 2) return;

        LOBBY_COUNTDOWN_SCHEDULER.stop();
    }

    @Override
    public void cancelCountdownIfNotEnoughTeams() {
        if (!LOBBY_COUNTDOWN_SCHEDULER.isRunning()) return;

        final List<ITeam> iTeams = Arrays.stream(BED_WARS_TEAMS)
                .map(BedWarsTeam::getTeam)
                .filter(team -> team.getPlayers().size() > 0)
                .toList();

        if (iTeams.size() > 1) return;

        LOBBY_COUNTDOWN_SCHEDULER.stop();
    }
}
