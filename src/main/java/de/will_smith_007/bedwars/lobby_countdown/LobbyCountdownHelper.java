package de.will_smith_007.bedwars.lobby_countdown;

import de.will_smith_007.bedwars.lobby_countdown.interfaces.ILobbyCountdownHelper;
import de.will_smith_007.bedwars.schedulers.LobbyCountdownScheduler;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class LobbyCountdownHelper implements ILobbyCountdownHelper {

    private final LobbyCountdownScheduler LOBBY_COUNTDOWN_SCHEDULER;

    public LobbyCountdownHelper(@NonNull LobbyCountdownScheduler lobbyCountdownScheduler) {
        LOBBY_COUNTDOWN_SCHEDULER = lobbyCountdownScheduler;
    }

    @Override
    public void startCountdownIfEnoughPlayers() {
        if (LOBBY_COUNTDOWN_SCHEDULER.isRunning()) return;

        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final int playerSize = players.size();

        if (playerSize < 2) return;

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
}
