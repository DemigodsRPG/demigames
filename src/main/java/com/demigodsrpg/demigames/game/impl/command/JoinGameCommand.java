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
import com.demigodsrpg.demigames.event.PlayerQuitMinigameEvent;
import com.demigodsrpg.demigames.game.Backend;
import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.game.lobby.Lobby;
import com.demigodsrpg.demigames.game.lobby.LobbySession;
import com.demigodsrpg.demigames.session.Session;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class JoinGameCommand extends BaseCommand {

    private final Backend INST;

    public JoinGameCommand(Backend backend) {
        INST = backend;
    }

    // -- COMMAND EXECUTOR -- //

    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length < 1) {
            return CommandResult.INVALID_SYNTAX;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // Stop them if they are in a game already
            Optional<Session> opSession = INST.getSessionRegistry().getSession(player);
            if (opSession.isPresent() && !(opSession.get() instanceof LobbySession)) {
                player.sendMessage(ChatColor.RED + "You are already in a game!");
                return CommandResult.QUIET_ERROR;
            }

            // Join a game if possible
            Optional<Game> opGame = INST.getGameRegistry().getMinigameByDirectory(args[0]);
            if (opGame.isPresent() && opGame.get() != Lobby.LOBBY) {
                sender.sendMessage(ChatColor.YELLOW + "Joining " + args[0] + ".");
                if (opSession.isPresent() && opSession.get() instanceof LobbySession) {
                    Lobby.LOBBY.quit(player, opSession.get(), PlayerQuitMinigameEvent.QuitReason.LEAVE_SESSION);
                }
                opGame.get().join((Player) sender);
                return CommandResult.SUCCESS;
            } else {
                sender.sendMessage(ChatColor.RED + "No such game called " + args[0] + ".");
                return CommandResult.QUIET_ERROR;
            }
        }
        return CommandResult.PLAYER_ONLY;
    }
}
