/*
 * Copyright (c) 2015 Demigods RPG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.demigodsrpg.demigames.impl.command;

import com.censoredsoftware.library.command.type.BaseCommand;
import com.censoredsoftware.library.command.type.CommandResult;
import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.impl.Demigames;
import com.demigodsrpg.demigames.session.Session;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SessionInfoCommand extends BaseCommand implements TabCompleter {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (sender instanceof Player) {
            if (!sender.hasPermission("demigames.admin")) {
                return CommandResult.NO_PERMISSIONS;
            }
            Player player = (Player) sender;

            if (args.length < 1) {
                Optional<Session> opSession = Demigames.getSessionRegistry().getSession(player);
                if (opSession.isPresent()) {
                    printInfo(player, opSession.get());
                    return CommandResult.SUCCESS;
                } else {
                    player.sendMessage(ChatColor.RED + "You currently aren't in a session.");
                    return CommandResult.QUIET_ERROR;
                }
            }

            if (args.length == 1) {
                Optional<Game> opGame = Demigames.getGameRegistry().getMinigame(args[0]);
                if (opGame.isPresent()) {
                    Demigames.getSessionRegistry().fromGame(opGame.get()).forEach(session ->
                            printInfo(player, session));
                    return CommandResult.SUCCESS;
                } else {
                    player.sendMessage(ChatColor.RED + "There is no such game \"" + args[0] + "\".");
                    return CommandResult.QUIET_ERROR;
                }
            }

            if (args.length == 2) {
                Optional<Game> opGame = Demigames.getGameRegistry().getMinigame(args[0]);
                if (opGame.isPresent()) {
                    Optional<Session> opSession = Demigames.getSessionRegistry().fromKey(args[1]);
                    if (opSession.isPresent()) {
                        printInfo(player, opSession.get());
                        return CommandResult.SUCCESS;
                    } else {
                        player.sendMessage(ChatColor.RED + "Invalid session id.");
                        return CommandResult.QUIET_ERROR;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "There is no such game \"" + args[0] + "\".");
                    return CommandResult.QUIET_ERROR;
                }
            }

            return CommandResult.INVALID_SYNTAX;
        }
        return CommandResult.PLAYER_ONLY;
    }

    private void printInfo(Player player, Session session) {
        player.sendMessage(ChatColor.YELLOW + session.getId() + ":");
        if (session.getGame().isPresent()) {
            player.sendMessage(ChatColor.GREEN + "  Game: " + ChatColor.WHITE + session.getGame().get().getName());
        }
        player.sendMessage(ChatColor.GREEN + "  Stage: " + ChatColor.WHITE + session.getStage());
        player.sendMessage(ChatColor.GREEN + "  # of Players: " + ChatColor.WHITE + session.getRawProfiles().size());
        player.sendMessage(ChatColor.GREEN + "  Joinable: " + session.isJoinable());
        player.sendMessage(ChatColor.GREEN + "  Done: " + session.isDone());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("demigames.admin")) {
            if (args.length == 1) {
                return Demigames.getGameRegistry().getMinigameNames().stream().filter(name -> name.toLowerCase().
                        startsWith(args[0].toLowerCase())).collect(Collectors.toList());
            }
            if (args.length == 2) {
                Optional<Game> opGame = Demigames.getGameRegistry().getMinigame(args[0]);
                if (opGame.isPresent()) {
                    return Demigames.getSessionRegistry().fromGame(opGame.get()).stream().map(Session::getId).
                            collect(Collectors.toList());
                }
            }
        }
        return new ArrayList<>();
    }
}
