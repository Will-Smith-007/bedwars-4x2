package de.will_smith_007.bedwars.lobby_countdown.interfaces;

public interface ILobbyCountdownHelper {

    void startCountdownIfEnoughPlayers();

    boolean shortenCountdown();

    void cancelCountdownIfNotEnoughPlayers(int players);

    void cancelCountdownIfNotEnoughTeams();
}
