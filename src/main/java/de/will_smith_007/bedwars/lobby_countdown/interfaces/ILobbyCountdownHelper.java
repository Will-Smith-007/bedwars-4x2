package de.will_smith_007.bedwars.lobby_countdown.interfaces;

public interface ILobbyCountdownHelper {

    boolean shortenCountdownIfEnoughPlayers();

    void cancelCountdownIfNotEnoughPlayers();
}
