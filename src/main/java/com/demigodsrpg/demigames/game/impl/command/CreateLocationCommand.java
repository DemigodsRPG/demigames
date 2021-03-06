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

package com.demigodsrpg.demigames.game.impl.command;

import com.censoredsoftware.library.command.type.BaseCommand;
import com.censoredsoftware.library.command.type.CommandResult;
import com.demigodsrpg.demigames.game.Backend;
import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.game.GameLocation;
import com.demigodsrpg.demigames.game.impl.listener.LocationSelectListener;
import com.demigodsrpg.demigames.session.Session;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CreateLocationCommand extends BaseCommand {

    private final Backend INST;

    public CreateLocationCommand(Backend backend) {
        INST = backend;
    }

    // -- COMMAND EXECUTOR -- //

    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            return CommandResult.PLAYER_ONLY;
        }
        if (sender.hasPermission("demigames.admin")) {
            Player player = (Player) sender;
            Optional<Session> session = INST.getSessionRegistry().getSession(player);
            if (session.isPresent()) {
                if (args.length == 1) {
                    String name = args[0];
                    Optional<Game> game = session.get().getGame();
                    if (game.isPresent()) {
                        game.get().setLocation(name, new GameLocation(player.getLocation(), true));
                        sender.sendMessage(ChatColor.YELLOW + "Location " + name + " has been created!");
                        return CommandResult.SUCCESS;
                    } else {
                        return CommandResult.ERROR;
                    }
                } else if (args.length == 2 && "block".equalsIgnoreCase(args[0])) {
                    Optional<Game> game = session.get().getGame();
                    if (game.isPresent()) {
                        Bukkit.getServer().getPluginManager().registerEvents(new LocationSelectListener(args[1],
                                game.get(), player), INST);
                        sender.sendMessage(ChatColor.YELLOW + "Right click a block with your bare hands.");
                        return CommandResult.SUCCESS;
                    } else {
                        return CommandResult.ERROR;
                    }
                }
                return CommandResult.INVALID_SYNTAX;
            } else {
                sender.sendMessage(ChatColor.RED + "You are currently not in any mini-game session.");
                return CommandResult.QUIET_ERROR;
            }
        }
        return CommandResult.NO_PERMISSIONS;
    }
}
