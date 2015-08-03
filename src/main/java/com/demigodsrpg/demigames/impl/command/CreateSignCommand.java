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
import com.demigodsrpg.demigames.impl.listener.SignSelectListener;
import com.demigodsrpg.demigames.session.Session;
import com.google.common.base.Joiner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CreateSignCommand extends BaseCommand {

    // -- CONFIG -- //

    private FileConfiguration CONFIG = Demigames.getInstance().getConfig();

    // -- COMMAND EXECUTOR -- //

    @Override
    protected CommandResult onCommand(CommandSender sender, Command c, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            return CommandResult.PLAYER_ONLY;
        }
        if (sender.hasPermission("demigames.admin")) {
            Player player = (Player) sender;
            Optional<Session> session = Demigames.getSessionRegistry().getSession(player);
            if (session.isPresent()) {
                if (args.length >= 2) {
                    Optional<Game> game = session.get().getGame();
                    List<String> commandParts = new ArrayList<>(Arrays.asList(args));
                    commandParts.remove(0);
                    String command = Joiner.on(" ").join(commandParts);
                    if (game.isPresent()) {
                        Bukkit.getServer().getPluginManager().registerEvents(new SignSelectListener(args[0],
                                game.get().getName(), command, player), Demigames.getInstance());
                        sender.sendMessage(ChatColor.YELLOW + "Right click a sign with your bare hands.");
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
