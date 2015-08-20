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
import com.demigodsrpg.demigames.event.PlayerQuitMinigameEvent;
import com.demigodsrpg.demigames.game.Game;
import com.demigodsrpg.demigames.impl.Demigames;
import com.demigodsrpg.demigames.impl.lobby.LobbySession;
import com.demigodsrpg.demigames.session.Session;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class LeaveGameCommand extends BaseCommand {

    @Override
    protected CommandResult onCommand(CommandSender sender, Command command, String[] ags) {
        if (sender instanceof Player) {
            // Stop them if they are in a game already
            Optional<Session> opSession = Demigames.getSessionRegistry().getSession((Player) sender);
            if (opSession.isPresent() && !(opSession.get() instanceof LobbySession)) {
                Optional<Game> opGame = opSession.get().getGame();
                if (opGame.isPresent()) {
                    opGame.get().quit((Player) sender, opSession.get(),
                            PlayerQuitMinigameEvent.QuitReason.LEAVE_SESSION);
                    sender.sendMessage(ChatColor.YELLOW + "Leaving...");
                    return CommandResult.SUCCESS;
                }
                return CommandResult.ERROR;
            }

            sender.sendMessage(ChatColor.RED + "You can't leave from here.");
            return CommandResult.QUIET_ERROR;
        }
        return CommandResult.PLAYER_ONLY;
    }
}
