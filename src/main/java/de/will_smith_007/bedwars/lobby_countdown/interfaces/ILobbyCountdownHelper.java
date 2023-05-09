package de.will_smith_007.bedwars.lobby_countdown.interfaces;

public interface ILobbyCountdownHelper {

    /**
     * Starts the lobby countdown if the server has enough players to start the game.
     * There is currently a minimum of 2 players required.
     */
    void startCountdownIfEnoughPlayers();

    /**
     * Shortens the lobby countdown only if the countdown is running and not at less
     * than 10 seconds.
     *
     * @return True if the countdown was shortened.
     */
    boolean shortenCountdown();

    /**
     * Cancels the lobby countdown if there aren't enough players to start the game.
     * This method should be only used on player quits.
     *
     * @param players The amount of players calculated by getting the amount of
     *                online players minus one.
     * @apiNote It needs to be calculated manually because the player isn't removed from
     * the bukkit online players collection on the player quit event.
     */
    void cancelCountdownIfNotEnoughPlayers(int players);

    /**
     * Cancels the lobby countdown if there aren't enough valid teams.
     * <br><br>
     * A valid team is:
     * <br>
     * <ol>
     *     <li>If the team have a minimum of one player.</li>
     *     <li>If these teams haven't more than the max players per team amount of players.</li>
     * </ol>
     */
    void cancelCountdownIfNotEnoughTeams();
}
