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
import com.demigodsrpg.demigames.impl.util.LocationUtil;
import com.demigodsrpg.demigames.session.Session;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CreateLocationCommand extends BaseCommand {
    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            return CommandResult.PLAYER_ONLY;
        }
        if (sender.hasPermission("demigames.admin")) {
            Player player = (Player) sender;
            Optional<Session> session = Demigames.getSessionRegistry().getSession(player);
            if (session.isPresent()) {
                if (args.length == 1) {
                    String name = args[0];
                    Optional<Game> game = session.get().getGame();
                    if (game.isPresent()) {
                        ConfigurationSection config = game.get().getConfig();
                        config.set(name, LocationUtil.stringFromLocation(player.getLocation(), true));
                        sender.sendMessage(ChatColor.YELLOW + "Location " + name + " has been created!");
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
