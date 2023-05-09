package de.will_smith_007.bedwars.commands;

import com.google.inject.Inject;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.setup.BedWarsSetup;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
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

import java.util.*;

/**
 * This command handles the setup of worlds.
 */
public class BedWarsCommand implements TabExecutor {

    @Getter
    private static final Map<Player, BedWarsSetup> PLAYERS_IN_SETUP = new HashMap<>();
    private final BedWarsConfig bedWarsConfig;
    private final TeamParser teamParser;

    @Inject
    public BedWarsCommand(@NonNull TeamParser teamParser,
                          @NonNull BedWarsConfig bedWarsConfig) {
        this.teamParser = teamParser;
        this.bedWarsConfig = bedWarsConfig;
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
            // Starting or stopping the current game world setup
            if (args[0].equalsIgnoreCase("setup")) {
                if (PLAYERS_IN_SETUP.containsKey(player)) {
                    player.sendPlainMessage(Message.PREFIX + "§aYou've aborted the game setup.");
                    PLAYERS_IN_SETUP.remove(player);
                    return true;
                }

                final BedWarsSetup bedWarsSetup = new BedWarsSetup(bedWarsConfig);
                bedWarsSetup.setSetupAction(BedWarsSetup.SetupAction.WORLD_SETUP);

                PLAYERS_IN_SETUP.put(player, bedWarsSetup);

                player.sendPlainMessage(Message.PREFIX + "§aYou've started the§e game setup§a, " +
                        "you can now set the game map by typing the name of world into the chat.");
                // Sets the spectator spawn to which eliminated player should be teleported to
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
            } else {
                sendHelpDescription(player);
            }
        } else if (args.length == 2) {
            final String subCommand = args[0];
            // Sets the waiting lobby world
            if (subCommand.equalsIgnoreCase("setLobby")) {
                final String worldName = args[1];
                final World world = Bukkit.createWorld(new WorldCreator(worldName));

                if (world == null) {
                    player.sendPlainMessage(Message.PREFIX + "§cWorld §e" + worldName + "§c could not be found.");
                    return true;
                }

                bedWarsConfig.setLobbyWorld(world);
                player.sendPlainMessage(Message.PREFIX + "§aYou've set the lobby world to §e" + worldName + "§a.");
                // Sets a bed location for the given team
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

                teamParser.parseTeam(teamName).ifPresentOrElse(team -> {
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
                // Sets the team spawn location of the given team
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

                teamParser.parseTeam(teamName).ifPresentOrElse(team -> {
                    final Location playerLocation = player.getLocation();

                    bedWarsSetup.setTeamSpawnLocation(team, playerLocation);
                    PLAYERS_IN_SETUP.put(player, bedWarsSetup);

                    player.sendPlainMessage(Message.PREFIX + "§aYou've set the team spawn for team §e" +
                            team.getTeamName() + "§a.");
                }, () -> player.sendPlainMessage(Message.PREFIX + "§cThere isn't a team named §e" + teamName + "§c."));
                // Goes to the next BedWars setup step if all requirements of the current step are met
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
                            if (bedWarsSetup.getBedLocations().size() < 4) {
                                player.sendPlainMessage(Message.PREFIX + "§cYou've not set all§e bed locations§c.");
                                return true;
                            }

                            bedWarsSetup.setSetupAction(BedWarsSetup.SetupAction.TEAM_SPAWN_SETUP);
                            PLAYERS_IN_SETUP.put(player, bedWarsSetup);

                            player.sendPlainMessage(Message.PREFIX + "§aNow set the§e team spawn§a locations by " +
                                    "using the command §e/bw setSpawn [Team]§a and use the command " +
                                    "§e/bw setup next§a if you're done.");
                        }

                        case TEAM_SPAWN_SETUP -> {
                            if (bedWarsSetup.getTeamSpawnLocations().size() < 4) {
                                player.sendPlainMessage(Message.PREFIX + "§cYou've not set all§e team spawn§c " +
                                        "locations.");
                                return true;
                            }

                            bedWarsSetup.setSetupAction(BedWarsSetup.SetupAction.BRONZE_SPAWNER_SETUP);
                            PLAYERS_IN_SETUP.put(player, bedWarsSetup);

                            player.sendPlainMessage(Message.PREFIX + "§aNow set the§e bronze spawners§a by right " +
                                    "clicking a block and use the command §e/bw setup next§a if you're done.");
                        }

                        case BRONZE_SPAWNER_SETUP, IRON_SPAWNER_SETUP, GOLD_SPAWNER_SETUP -> {
                            if (bedWarsSetup.getSpawnerLocations().isEmpty()) {
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
                                player.sendPlainMessage(Message.PREFIX + "§aNow set the§e " + nextSetupStep + " spawners§a by right " +
                                        "clicking a block and use the command §e/bw setup next§a if you're done.");
                            } else {
                                player.sendPlainMessage(Message.PREFIX + "§aFinally set the§e spectator spawn " +
                                        "location§a by using the command §e/bw setSpectator");
                            }
                        }

                        default -> player.sendPlainMessage(Message.PREFIX + "§cYou currently can't go to the next step.");
                    }
                    // Finish and saved the current, completed game world setup if all requirements of this setup are met
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
                } else {
                    sendHelpDescription(player);
                }
                // Teleport a player to the specified world, loads the world if it isn't currently loaded
            } else if (subCommand.equalsIgnoreCase("tp")
                    || subCommand.equalsIgnoreCase("teleport")) {
                final String worldName = args[1];
                final World world = Bukkit.createWorld(new WorldCreator(worldName));

                if (world == null) {
                    player.sendPlainMessage(Message.PREFIX + "§cWorld §e" + worldName + "§c couldn't be found.");
                    return true;
                }

                player.teleport(world.getSpawnLocation());
                player.setGameMode(GameMode.CREATIVE);
                player.sendPlainMessage(Message.PREFIX + "§aYou've been teleported to §e" + worldName + "§a.");
            } else {
                sendHelpDescription(player);
            }
        } else {
            sendHelpDescription(player);
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!sender.hasPermission("bedwars.setup")) return null;

        final List<String> subCommands = new ArrayList<>();

        if (args.length == 1) {
            return Arrays.asList("tp", "teleport", "setup", "setBed", "setSpectator", "setSpawn", "setLobby");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("tp")
                    || args[0].equalsIgnoreCase("setLobby")) {
                subCommands.add("WorldName");
            } else if (args[0].equalsIgnoreCase("setBed")
                    || args[0].equalsIgnoreCase("setSpawn")) {
                return Arrays.stream(BedWarsTeam.values())
                        .map(BedWarsTeam::getTeamName)
                        .map(teamName -> teamName.replace("Team ", ""))
                        .toList();
            } else if (args[0].equalsIgnoreCase("setup")) {
                subCommands.add("next");
                subCommands.add("finish");
            }
        }

        return subCommands;
    }

    /**
     * Sends a help description the given {@link Player}.
     * It includes all available commands.
     *
     * @param player The player to which the help description should be sent.
     */
    private void sendHelpDescription(@NonNull Player player) {
        final String prefix = Message.PREFIX.toString();

        player.sendPlainMessage(prefix + "§e/bw setup §8- §7Starts/Stops the map setup");
        player.sendPlainMessage(prefix + "§e/bw setSpectator §8- §7Sets the spectator location at your position");
        player.sendPlainMessage(prefix + "§e/bw setup next §8- §7Go to the next setup step if you're done with previous");
        player.sendPlainMessage(prefix + "§e/bw setup finish §8- §7Finish your map setup and save it into the config");
        player.sendPlainMessage(prefix + "§e/bw tp [WorldName] §8- §7Teleports you into a world");
        player.sendPlainMessage(prefix + "§e/bw setLobby [WorldName] §8- §7Sets the waiting lobby");
        player.sendPlainMessage(prefix + "§e/bw setBed [Team] §8- §7Sets a bed for a team at position you're looking");
        player.sendPlainMessage(prefix + "§e/bw setSpawn [Team] §8- §7Sets the spawn for a team at your position");
    }
}
