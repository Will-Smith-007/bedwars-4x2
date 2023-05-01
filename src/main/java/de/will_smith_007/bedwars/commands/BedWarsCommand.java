package de.will_smith_007.bedwars.commands;

import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.setup.BedWarsSetup;
import de.will_smith_007.bedwars.teams.parser.TeamParser;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BedWarsCommand implements TabExecutor {

    @Getter
    private static final Map<Player, BedWarsSetup> PLAYERS_IN_SETUP = new HashMap<>();
    private final BedWarsConfig BED_WARS_CONFIG = BedWarsConfig.getInstance();
    private final TeamParser TEAM_PARSER;

    public BedWarsCommand(@NonNull TeamParser teamParser) {
        TEAM_PARSER = teamParser;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof final Player player)) {
            sender.sendPlainMessage(Message.PREFIX + "§cYou must be a player to execute this command.");
            return true;
        }

        if (!player.hasPermission("bedwars.setup")) {
            player.sendPlainMessage(Message.PREFIX + "§cYou don't have permission to execute this command.");
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("setup")) {
                if (PLAYERS_IN_SETUP.containsKey(player)) {
                    player.sendPlainMessage(Message.PREFIX + "§aYou've aborted the game setup.");
                    PLAYERS_IN_SETUP.remove(player);
                    return true;
                }

                final BedWarsSetup bedWarsSetup = new BedWarsSetup();
                bedWarsSetup.setSetupAction(BedWarsSetup.SetupAction.WORLD_SETUP);

                PLAYERS_IN_SETUP.put(player, bedWarsSetup);

                player.sendPlainMessage(Message.PREFIX + "§aYou've started the§e game setup§a, " +
                        "you can now set the game map by typing the name of world into the chat.");
            } else if (args[0].equalsIgnoreCase("setSpectator")) {
                if (!PLAYERS_IN_SETUP.containsKey(player)) {
                    player.sendPlainMessage(Message.PREFIX + "§cYou're currently not in a map setup.");
                    return true;
                }

                final BedWarsSetup bedWarsSetup = PLAYERS_IN_SETUP.get(player);
                final BedWarsSetup.SetupAction setupAction = bedWarsSetup.getSetupAction();

                if (setupAction != BedWarsSetup.SetupAction.SPECTATOR_SETUP) {
                    player.sendPlainMessage(Message.PREFIX + "§cThis configuration is a coming step.");
                    return true;
                }

                final Location playerLocation = player.getLocation();
                bedWarsSetup.setSpectatorLocation(playerLocation);
                PLAYERS_IN_SETUP.put(player, bedWarsSetup);

                player.sendPlainMessage(Message.PREFIX + "§aYou've set the§e spectator spawn location§a.");
                player.sendPlainMessage(Message.PREFIX + "§aFinish and save this setup with §e/bw setup finish");
            }
        } else if (args.length == 2) {
            final String subCommand = args[0];
            if (subCommand.equalsIgnoreCase("setLobby")) {
                final String worldName = args[1];
                final World world = Bukkit.createWorld(new WorldCreator(worldName));

                if (world == null) {
                    player.sendPlainMessage(Message.PREFIX + "§cWorld §e" + worldName + "§c could not be found.");
                    return true;
                }

                BED_WARS_CONFIG.setLobbyWorld(world);
                player.sendPlainMessage(Message.PREFIX + "§aYou've set the lobby world to §e" + worldName + "a.");
            } else if (subCommand.equalsIgnoreCase("setBed")) {
                if (!PLAYERS_IN_SETUP.containsKey(player)) {
                    player.sendPlainMessage(Message.PREFIX + "§cYou're currently not in a map setup.");
                    return true;
                }

                final BedWarsSetup bedWarsSetup = PLAYERS_IN_SETUP.get(player);

                if (bedWarsSetup.getSetupAction() != BedWarsSetup.SetupAction.BED_SETUP) {
                    player.sendPlainMessage(Message.PREFIX + "§cYou already set the§e beds§c or " +
                            "this is a coming setup step.");
                    return true;
                }

                final String teamName = args[1];

                TEAM_PARSER.parseTeam(teamName).ifPresentOrElse(team -> {
                    final Block block = player.getTargetBlock(null, 5);
                    final Material material = block.getType();

                    if (!material.toString().endsWith("BED")) {
                        player.sendPlainMessage(Message.PREFIX + "§cThe block you're looking at isn't a§e bed§c.");
                        return;
                    }

                    player.sendPlainMessage(Message.PREFIX + "§aYou've set the bed location for team §e" +
                            team.getTeamName() + "§a.");

                    bedWarsSetup.setBedLocation(team, block.getLocation());

                    PLAYERS_IN_SETUP.put(player, bedWarsSetup);
                }, () -> player.sendPlainMessage(Message.PREFIX + "§cThere isn't a team named §e" + teamName + "§c."));
            } else if (subCommand.equalsIgnoreCase("setSpawn")) {
                if (!PLAYERS_IN_SETUP.containsKey(player)) {
                    player.sendPlainMessage(Message.PREFIX + "§cYou're currently not in a map setup.");
                    return true;
                }

                final BedWarsSetup bedWarsSetup = PLAYERS_IN_SETUP.get(player);

                if (bedWarsSetup.getSetupAction() != BedWarsSetup.SetupAction.TEAM_SPAWN_SETUP) {
                    player.sendPlainMessage(Message.PREFIX + "§cYou already set the§e team spawn§c or " +
                            "this is a coming setup step.");
                    return true;
                }

                final String teamName = args[1];

                TEAM_PARSER.parseTeam(teamName).ifPresentOrElse(team -> {
                    final Location playerLocation = player.getLocation();

                    bedWarsSetup.setTeamSpawnLocation(team, playerLocation);
                    PLAYERS_IN_SETUP.put(player, bedWarsSetup);

                    player.sendPlainMessage(Message.PREFIX + "§aYou've set the team spawn for team §e" +
                            team.getTeamName() + "§a.");
                }, () -> player.sendPlainMessage(Message.PREFIX + "§cThere isn't a team named §e" + teamName + "§c."));
            } else if (subCommand.equalsIgnoreCase("setup")) {
                if (args[1].equalsIgnoreCase("next")) {
                    if (!PLAYERS_IN_SETUP.containsKey(player)) {
                        player.sendPlainMessage(Message.PREFIX + "§cYou're currently not in a map setup.");
                        return true;
                    }

                    final BedWarsSetup bedWarsSetup = PLAYERS_IN_SETUP.get(player);
                    final BedWarsSetup.SetupAction setupAction = bedWarsSetup.getSetupAction();

                    switch (setupAction) {
                        case BED_SETUP -> {
                            if (bedWarsSetup.getBED_LOCATIONS().size() < 4) {
                                player.sendPlainMessage(Message.PREFIX + "§cYou've not set all§e bed locations§c.");
                                return true;
                            }

                            bedWarsSetup.setSetupAction(BedWarsSetup.SetupAction.TEAM_SPAWN_SETUP);
                            PLAYERS_IN_SETUP.put(player, bedWarsSetup);

                            player.sendPlainMessage(Message.PREFIX + "§aNow set the§e team spawn§a locations by " +
                                    "using the command §e/bw setSpawn [Team] and use the command " +
                                    "§e/bw setup next§a if you're done.");
                        }

                        case TEAM_SPAWN_SETUP -> {
                            if (bedWarsSetup.getTEAM_SPAWN_LOCATIONS().size() < 4) {
                                player.sendPlainMessage(Message.PREFIX + "§cYou've not set all§e team spawn§c " +
                                        "locations.");
                                return true;
                            }

                            bedWarsSetup.setSetupAction(BedWarsSetup.SetupAction.BRONZE_SPAWNER_SETUP);
                            PLAYERS_IN_SETUP.put(player, bedWarsSetup);

                            player.sendPlainMessage(Message.PREFIX + "§aNow set the§e bronze spawners by right " +
                                    "clicking a block and use the command §e/bw setup next§a if you're done.");
                        }

                        case BRONZE_SPAWNER_SETUP, IRON_SPAWNER_SETUP, GOLD_SPAWNER_SETUP -> {
                            if (bedWarsSetup.getSPAWNER_LOCATIONS().isEmpty()) {
                                player.sendPlainMessage(Message.PREFIX + "§cYou haven't set a single spawner.");
                                return true;
                            }

                            BedWarsSetup.SetupAction nextSetupAction;
                            String nextSetupStep = null;
                            if (setupAction == BedWarsSetup.SetupAction.BRONZE_SPAWNER_SETUP) {
                                nextSetupAction = BedWarsSetup.SetupAction.IRON_SPAWNER_SETUP;
                                nextSetupStep = "iron";
                            } else if (setupAction == BedWarsSetup.SetupAction.IRON_SPAWNER_SETUP) {
                                nextSetupAction = BedWarsSetup.SetupAction.GOLD_SPAWNER_SETUP;
                                nextSetupStep = "gold";
                            } else {
                                nextSetupAction = BedWarsSetup.SetupAction.SPECTATOR_SETUP;
                            }

                            bedWarsSetup.setSetupAction(nextSetupAction);
                            PLAYERS_IN_SETUP.put(player, bedWarsSetup);

                            if (nextSetupStep != null) {
                                player.sendPlainMessage(Message.PREFIX + "§aNow set the§e" + nextSetupStep + " spawners by right " +
                                        "clicking a block and use the command §e/bw setup next§a if you're done.");
                            } else {
                                player.sendPlainMessage(Message.PREFIX + "§aFinally set the§e spectator spawn " +
                                        "location§a by using the command §e/bw setSpectator");
                            }
                        }

                        default ->
                                player.sendPlainMessage(Message.PREFIX + "§cYou currently can't go to the next step.");
                    }
                } else if (args[1].equalsIgnoreCase("finish")) {
                    if (!PLAYERS_IN_SETUP.containsKey(player)) {
                        player.sendPlainMessage(Message.PREFIX + "§cYou're currently not in a map setup.");
                        return true;
                    }

                    final BedWarsSetup bedWarsSetup = PLAYERS_IN_SETUP.get(player);
                    final BedWarsSetup.SetupAction setupAction = bedWarsSetup.getSetupAction();

                    if (setupAction != BedWarsSetup.SetupAction.SPECTATOR_SETUP) {
                        player.sendPlainMessage(Message.PREFIX + "§cYou can't finish and save the setup until " +
                                "you have completed the setup.");
                        return true;
                    }

                    if (bedWarsSetup.getSpectatorLocation() == null) {
                        player.sendPlainMessage(Message.PREFIX + "§cYou haven't set the§e spectator spawn location§c.");
                        return true;
                    }

                    bedWarsSetup.saveSetup();
                    PLAYERS_IN_SETUP.remove(player);

                    player.sendPlainMessage(Message.PREFIX + "§aYou've successfully§e finished and saved §athe setup.");
                }
            } else if (subCommand.equalsIgnoreCase("tp")
                    || subCommand.equalsIgnoreCase("teleport")) {
                final String worldName = args[1];
                final World world = Bukkit.createWorld(new WorldCreator(worldName));

                if (world == null) {
                    player.sendPlainMessage(Message.PREFIX + "§cWorld §e" + worldName + "§c couldn't be found.");
                    return true;
                }

                player.teleport(world.getSpawnLocation());
                player.sendPlainMessage(Message.PREFIX + "§aYou've been teleported to §e" + worldName + "§a.");
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return null;
    }
}
