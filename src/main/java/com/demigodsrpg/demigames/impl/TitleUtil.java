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

package com.demigodsrpg.demigames.impl;

import com.demigodsrpg.demigames.profile.Profile;
import com.demigodsrpg.demigames.session.Session;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("unchecked")
public class TitleUtil {
    final String NMS;
    final String CB;

    final Class<? extends Player> CB_CRAFTPLAYER;
    final Class NMS_ENTITY_PLAYER;
    final Class NMS_PLAYER_CONN;
    final Class NMS_ICHAT_BASE;
    final Class NMS_PACKET;
    final Class NMS_PACKET_PLAY_TITLE;
    final Class<? extends Enum> NMS_TITLE_ACTION;
    final Class NMS_CHAT_SERIALIZER;

    final Method GET_HANDLE;
    final Method SEND_PACKET;
    final Method ICHAT_A;

    final Field PLAYER_CONN;

    final Object[] ACTION_ARRAY;

    TitleUtil() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1) + ".";

        // Common classpaths
        NMS = "net.minecraft.server." + version;
        CB = "org.bukkit.craftbukkit." + version;

        // Classes being used
        CB_CRAFTPLAYER = (Class<? extends Player>) Class.forName(CB + "entity.CraftPlayer");
        NMS_ENTITY_PLAYER = Class.forName(NMS + "EntityPlayer");
        NMS_PLAYER_CONN = Class.forName(NMS + "PlayerConnection");
        NMS_ICHAT_BASE = Class.forName(NMS + "IChatBaseComponent");
        NMS_PACKET = Class.forName(NMS + "Packet");
        NMS_PACKET_PLAY_TITLE = Class.forName(NMS + "PacketPlayOutTitle");
        NMS_TITLE_ACTION = (Class<? extends Enum>) Class.forName(NMS + "PacketPlayOutTitle$EnumTitleAction");
        NMS_CHAT_SERIALIZER = Class.forName(NMS + "IChatBaseComponent$ChatSerializer");

        // Methods being used
        GET_HANDLE = CB_CRAFTPLAYER.getMethod("getHandle");
        SEND_PACKET = NMS_PLAYER_CONN.getMethod("sendPacket", NMS_PACKET);
        ICHAT_A = NMS_CHAT_SERIALIZER.getMethod("a", String.class);

        // Fields being used
        PLAYER_CONN = NMS_ENTITY_PLAYER.getDeclaredField("playerConnection");

        // Title action array
        ACTION_ARRAY = NMS_TITLE_ACTION.getEnumConstants();
    }

    /**
     * Send a title message to all players.
     *
     * @param fadeInTicks  The ticks the message takes to fade in.
     * @param stayTicks    The ticks the message stays on screen (sans fades).
     * @param fadeOutTicks The ticks the message takes to fade out.
     * @param title        The title text.
     * @param subtitle     The subtitle text.
     */
    public void broadcastTitle(int fadeInTicks, int stayTicks, int fadeOutTicks, String title, String subtitle) {
        Bukkit.getOnlinePlayers().forEach(player -> sendTitle(player, fadeInTicks, stayTicks, fadeOutTicks, title, subtitle));
    }

    /**
     * Send a title message to all players in a session.
     *
     * @param session      The session being broadcast to.
     * @param fadeInTicks  The ticks the message takes to fade in.
     * @param stayTicks    The ticks the message stays on screen (sans fades).
     * @param fadeOutTicks The ticks the message takes to fade out.
     * @param title        The title text.
     * @param subtitle     The subtitle text.
     */
    public void broadcastTitle(Session session, int fadeInTicks, int stayTicks, int fadeOutTicks, String title, String subtitle) {
        session.getProfiles().stream().map(Profile::getPlayer).forEach(player -> {
            if (player.isPresent()) {
                sendTitle(player.get(), fadeInTicks, stayTicks, fadeOutTicks, title, subtitle);
            }
        });
    }

    /**
     * Send a title message to a player.
     *
     * @param player       The player receiving the message.
     * @param fadeInTicks  The ticks the message takes to fade in.
     * @param stayTicks    The ticks the message stays on screen (sans fades).
     * @param fadeOutTicks The ticks the message takes to fade out.
     * @param title        The title text.
     * @param subtitle     The subtitle text.
     */
    public void sendTitle(Player player, int fadeInTicks, int stayTicks, int fadeOutTicks, String title, String subtitle) {
        try {
            clearTitle(player, true);

            Object craftPlayer = CB_CRAFTPLAYER.cast(player);
            Object entityPlayer = GET_HANDLE.invoke(craftPlayer);
            Object connection = PLAYER_CONN.get(entityPlayer);

            Object packetPlayOutTimes = NMS_PACKET_PLAY_TITLE.getConstructor(NMS_TITLE_ACTION, NMS_ICHAT_BASE, Integer.TYPE, Integer.TYPE, Integer.TYPE).
                    newInstance(ACTION_ARRAY[2], null, fadeInTicks, stayTicks, fadeOutTicks);
            SEND_PACKET.invoke(connection, packetPlayOutTimes);

            if (subtitle != null) {
                subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
                subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
                Object titleSub = ICHAT_A.invoke(null, "{\"text\": \"" + subtitle + "\"}");
                Object packetPlayOutSubTitle = NMS_PACKET_PLAY_TITLE.getConstructor(NMS_TITLE_ACTION, NMS_ICHAT_BASE).
                        newInstance(ACTION_ARRAY[1], titleSub);
                SEND_PACKET.invoke(connection, packetPlayOutSubTitle);
            }

            if (title != null) {
                title = title.replaceAll("%player%", player.getDisplayName());
                title = ChatColor.translateAlternateColorCodes('&', title);
                Object titleMain = ICHAT_A.invoke(null, "{\"text\": \"" + title + "\"}");
                Object packetPlayOutTitle = NMS_PACKET_PLAY_TITLE.getConstructor(NMS_TITLE_ACTION, NMS_ICHAT_BASE).
                        newInstance(ACTION_ARRAY[0], titleMain);
                SEND_PACKET.invoke(connection, packetPlayOutTitle);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException oops) {
            oops.printStackTrace();
        }
    }

    /**
     * Clear or reset the title data for a specified player.
     *
     * @param player The player being cleared/reset.
     * @param reset  True if reset, false for clear.
     */
    public void clearTitle(final Player player, boolean reset) {
        try {
            Object craftPlayer = CB_CRAFTPLAYER.cast(player);
            Object entityPlayer = GET_HANDLE.invoke(craftPlayer);
            Object connection = PLAYER_CONN.get(entityPlayer);

            Object packetPlayOutClear = NMS_PACKET_PLAY_TITLE.getConstructor(NMS_TITLE_ACTION, NMS_ICHAT_BASE).
                    newInstance(ACTION_ARRAY[reset ? 4 : 3], null);
            SEND_PACKET.invoke(connection, packetPlayOutClear);
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }
}
