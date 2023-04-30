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
    public boolean shortenCountdownIfEnoughPlayers() {
        if (LOBBY_COUNTDOWN_SCHEDULER.isRunning()) return false;
        if (LOBBY_COUNTDOWN_SCHEDULER.getCountdown() <= 10) return false;

        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final int playerSize = players.size();

        LOBBY_COUNTDOWN_SCHEDULER.setCountdown(10);

        return (playerSize >= 2);
    }

    @Override
    public void cancelCountdownIfNotEnoughPlayers() {
        if (!LOBBY_COUNTDOWN_SCHEDULER.isRunning()) return;

        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final int playerSize = players.size();

        if (playerSize >= 2) return;

        LOBBY_COUNTDOWN_SCHEDULER.stop();
    }
}
