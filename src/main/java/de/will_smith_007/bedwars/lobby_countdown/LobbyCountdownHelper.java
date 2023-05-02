package de.will_smith_007.bedwars.lobby_countdown;

import de.will_smith_007.bedwars.lobby_countdown.interfaces.ILobbyCountdownHelper;
import de.will_smith_007.bedwars.schedulers.LobbyCountdownScheduler;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

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

        int validTeams = 0;
        int playersWithoutTeam = playerSize;

        for (BedWarsTeam bedWarsTeam : BED_WARS_TEAMS) {
            final ITeam iTeam = bedWarsTeam.getTeam();
            final int teamPlayers = iTeam.getPlayers().size();
            if (teamPlayers > 0) validTeams++;
            playersWithoutTeam -= teamPlayers;
        }

        if (validTeams > 1 || playerSize >= 2 && playersWithoutTeam >= 1)
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

        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final int playerSize = players.size();

        int validTeams = 0;
        int playersWithoutTeam = playerSize;

        for (BedWarsTeam bedWarsTeam : BED_WARS_TEAMS) {
            final ITeam iTeam = bedWarsTeam.getTeam();
            final int teamPlayers = iTeam.getPlayers().size();
            if (teamPlayers > 0) validTeams++;
            playersWithoutTeam -= teamPlayers;
        }

        if (validTeams > 1 || playerSize >= 2 && playersWithoutTeam >= 1) return;

        LOBBY_COUNTDOWN_SCHEDULER.stop();
    }
}
