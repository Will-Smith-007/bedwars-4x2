package de.will_smith_007.bedwars.scoreboard;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.game_config.GameConfiguration;
import de.will_smith_007.bedwars.scoreboard.interfaces.IScoreboardManager;
import de.will_smith_007.bedwars.teams.TeamBlue;
import de.will_smith_007.bedwars.teams.TeamGreen;
import de.will_smith_007.bedwars.teams.TeamRed;
import de.will_smith_007.bedwars.teams.TeamYellow;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Optional;

public class ScoreboardManager implements IScoreboardManager {

    private final GameAssets gameAssets;
    private final ITeamHelper teamHelper;
    private final BedWarsTeam[] bedWarsTeams = BedWarsTeam.values();

    public ScoreboardManager(@NonNull GameAssets gameAssets,
                             @NonNull ITeamHelper teamHelper) {
        this.gameAssets = gameAssets;
        this.teamHelper = teamHelper;
    }

    @Override
    public void createScoreboard(@NonNull Player player) {
        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        final Objective objective = scoreboard.registerNewObjective("aaa", Criteria.DUMMY,
                Component.text("BEDWARS", NamedTextColor.WHITE));

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.displayName(Component.text("BEDWARS", NamedTextColor.WHITE));

        final GameConfiguration gameConfiguration = gameAssets.getGameConfiguration();

        {
            final Team team = scoreboard.registerNewTeam("x14");
            team.prefix(Component.text("§0"));
            team.addEntry("§0");
            objective.getScore("§0").setScore(14);
        }

        {
            final Team team = scoreboard.registerNewTeam("x13");
            team.prefix(Component.text("§1"));
            team.suffix(Component.text("Map:", NamedTextColor.GRAY));
            team.addEntry("§1");
            objective.getScore("§1").setScore(13);
        }

        {
            final String mapName = (gameConfiguration == null ? "Selection..." : gameConfiguration.gameWorld().getName());
            final Team team = scoreboard.registerNewTeam("x12");
            team.prefix(Component.text("§2"));
            team.suffix(Component.text(mapName, NamedTextColor.YELLOW));
            team.addEntry("§2");
            objective.getScore("§2").setScore(12);
        }

        {
            final Team team = scoreboard.registerNewTeam("x11");
            team.prefix(Component.text("§3"));
            team.addEntry("§3");
            objective.getScore("§3").setScore(11);
        }

        final GameState gameState = gameAssets.getGameState();

        switch (gameState) {
            case LOBBY -> {
                {
                    final Team team = scoreboard.registerNewTeam("x10");
                    team.prefix(Component.text("§4"));
                    team.suffix(Component.text("Game variant:", NamedTextColor.GRAY));
                    team.addEntry("§4");
                    objective.getScore("§4").setScore(10);
                }

                {
                    final Team team = scoreboard.registerNewTeam("x9");
                    team.prefix(Component.text("§5"));
                    team.suffix(Component.text("4x2", NamedTextColor.YELLOW));
                    team.addEntry("§5");
                    objective.getScore("§5").setScore(9);
                }
            }

            case PROTECTION, INGAME, ENDING -> {
                {
                    final ITeam iTeam = TeamBlue.getInstance();
                    final String bedExistsSymbol = (iTeam.bedExists() ? "§a✔ " : "§c✘ ");
                    final String teamName = iTeam.getTeamName().replace("Team ", "");
                    final int playerSize = iTeam.getPlayers().size();
                    final Team team = scoreboard.registerNewTeam("x10");

                    team.prefix(Component.text(bedExistsSymbol));
                    team.suffix(Component.text(teamName + " §7» §e" + playerSize));
                    team.addEntry("§4");
                    objective.getScore("§4").setScore(10);
                }

                {
                    final ITeam iTeam = TeamRed.getInstance();
                    final String bedExistsSymbol = (iTeam.bedExists() ? "§a✔ " : "§c✘ ");
                    final String teamName = iTeam.getTeamName().replace("Team ", "");
                    final int playerSize = iTeam.getPlayers().size();
                    final Team team = scoreboard.registerNewTeam("x9");

                    team.prefix(Component.text(bedExistsSymbol));
                    team.suffix(Component.text(teamName + " §7» §e" + playerSize));
                    team.addEntry("§5");
                    objective.getScore("§5").setScore(9);
                }

                {
                    final ITeam iTeam = TeamGreen.getInstance();
                    final String bedExistsSymbol = (iTeam.bedExists() ? "§a✔ " : "§c✘ ");
                    final String teamName = iTeam.getTeamName().replace("Team ", "");
                    final int playerSize = iTeam.getPlayers().size();
                    final Team team = scoreboard.registerNewTeam("x8");

                    team.prefix(Component.text(bedExistsSymbol));
                    team.suffix(Component.text(teamName + " §7» §e" + playerSize));
                    team.addEntry("§6");
                    objective.getScore("§6").setScore(8);
                }

                {
                    final ITeam iTeam = TeamYellow.getInstance();
                    final String bedExistsSymbol = (iTeam.bedExists() ? "§a✔ " : "§c✘ ");
                    final String teamName = iTeam.getTeamName().replace("Team ", "");
                    final int playerSize = iTeam.getPlayers().size();
                    final Team team = scoreboard.registerNewTeam("x7");

                    team.prefix(Component.text(bedExistsSymbol));
                    team.suffix(Component.text(teamName + " §7» §e" + playerSize));
                    team.addEntry("§7");
                    objective.getScore("§7").setScore(7);
                }
            }
        }

        player.setScoreboard(scoreboard);
    }

    @Override
    public void updateScoreboard(@NonNull Player player) {
        final Scoreboard scoreboard = player.getScoreboard();
        final GameConfiguration gameConfiguration = gameAssets.getGameConfiguration();
        final GameState gameState = gameAssets.getGameState();

        if (scoreboard.getObjective(DisplaySlot.SIDEBAR) == null) createScoreboard(player);

        final Team mapTeam = scoreboard.getTeam("x12");
        final Team blueTeam = scoreboard.getTeam("x10");
        final Team redTeam = scoreboard.getTeam("x9");
        final Team greenTeam = scoreboard.getTeam("x8");
        final Team yellowTeam = scoreboard.getTeam("x7");

        if (mapTeam != null) {
            final String mapName = (gameConfiguration == null ? "Selection..." : gameConfiguration.gameWorld().getName());
            mapTeam.suffix(Component.text(mapName, NamedTextColor.YELLOW));
        }

        if (gameState == GameState.LOBBY) return;

        for (BedWarsTeam bedWarsTeam : bedWarsTeams) {
            final ITeam iTeam = bedWarsTeam.getTeam();
            final String bedExistsSymbol = (iTeam.bedExists() ? "§a✔ " : "§c✘ ");
            final String teamName = iTeam.getTeamName().replace("Team ", "");
            final int playerSize = iTeam.getPlayers().size();

            final Team team;

            switch (bedWarsTeam) {
                case BLUE -> team = blueTeam;
                case RED -> team = redTeam;
                case GREEN -> team = greenTeam;
                default -> team = yellowTeam;
            }

            if (team == null) continue;

            team.prefix(Component.text(bedExistsSymbol));
            team.suffix(Component.text(teamName + " §7» §e" + playerSize));
        }
    }

    @Override
    public void setTablist(@NonNull Player player) {
        final Scoreboard scoreboard = player.getScoreboard();

        final ITeam iBlueTeam = TeamBlue.getInstance();
        final ITeam iRedTeam = TeamRed.getInstance();
        final ITeam iGreenTeam = TeamGreen.getInstance();
        final ITeam iYellowTeam = TeamYellow.getInstance();

        final Team blueTeam = iBlueTeam.getTeam(scoreboard);
        final Team redTeam = iRedTeam.getTeam(scoreboard);
        final Team greenTeam = iGreenTeam.getTeam(scoreboard);
        final Team yellowTeam = iYellowTeam.getTeam(scoreboard);

        Team playerTeam;
        if ((playerTeam = scoreboard.getTeam("player")) == null) {
            playerTeam = scoreboard.registerNewTeam("player");
        }
        playerTeam.color(NamedTextColor.GRAY);
        final Team finalPlayerTeam = playerTeam;

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            final Optional<ITeam> optionalITeam = teamHelper.getTeam(onlinePlayer);
            final String playerName = onlinePlayer.getName();
            optionalITeam.ifPresentOrElse(team -> {
                if (team instanceof TeamBlue) blueTeam.addEntry(playerName);
                else if (team instanceof TeamRed) redTeam.addEntry(playerName);
                else if (team instanceof TeamGreen) greenTeam.addEntry(playerName);
                else if (team instanceof TeamYellow) yellowTeam.addEntry(playerName);
            }, () -> finalPlayerTeam.addEntry(playerName));
        }
    }

    @Override
    public void setScoreboardAndTablist(@NonNull Player player) {
        createScoreboard(player);
        setTablist(player);
    }
}
