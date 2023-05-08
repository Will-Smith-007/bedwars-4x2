package de.will_smith_007.bedwars.lobby_countdown;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.will_smith_007.bedwars.lobby_countdown.interfaces.ILobbyCountdownHelper;
import de.will_smith_007.bedwars.schedulers.LobbyCountdownScheduler;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

@Singleton
public class LobbyCountdownHelper implements ILobbyCountdownHelper {

    private final LobbyCountdownScheduler lobbyCountdownScheduler;
    private final BedWarsTeam[] bedWarsTeams = BedWarsTeam.values();

    @Inject
    public LobbyCountdownHelper(@NonNull LobbyCountdownScheduler lobbyCountdownScheduler) {
        this.lobbyCountdownScheduler = lobbyCountdownScheduler;
    }

    @Override
    public void startCountdownIfEnoughPlayers() {
        if (lobbyCountdownScheduler.isRunning()) return;

        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final int playerSize = players.size();

        int validTeams = 0;
        int playersWithoutTeam = playerSize;

        for (BedWarsTeam bedWarsTeam : bedWarsTeams) {
            final ITeam iTeam = bedWarsTeam.getTeam();
            final int teamPlayers = iTeam.getPlayers().size();
            if (teamPlayers > 0) validTeams++;
            playersWithoutTeam -= teamPlayers;
        }

        if (validTeams > 1 || playerSize >= 2 && playersWithoutTeam >= 1)
            lobbyCountdownScheduler.start();
    }

    @Override
    public boolean shortenCountdown() {
        if (!lobbyCountdownScheduler.isRunning()) return false;
        if (lobbyCountdownScheduler.getCountdown() <= 10) return false;

        lobbyCountdownScheduler.setCountdown(10);

        return true;
    }

    @Override
    public void cancelCountdownIfNotEnoughPlayers(int players) {
        if (!lobbyCountdownScheduler.isRunning()) return;

        if (players >= 2) return;

        lobbyCountdownScheduler.stop();
    }

    @Override
    public void cancelCountdownIfNotEnoughTeams() {
        if (!lobbyCountdownScheduler.isRunning()) return;

        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final int playerSize = players.size();

        int validTeams = 0;
        int playersWithoutTeam = playerSize;

        for (BedWarsTeam bedWarsTeam : bedWarsTeams) {
            final ITeam iTeam = bedWarsTeam.getTeam();
            final int teamPlayers = iTeam.getPlayers().size();
            if (teamPlayers > 0) validTeams++;
            playersWithoutTeam -= teamPlayers;
        }

        if (validTeams > 1 || playerSize >= 2 && playersWithoutTeam >= 1) return;

        lobbyCountdownScheduler.stop();
    }
}
