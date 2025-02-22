/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.placeholder;

import me.filoghost.chestcommands.api.PlaceholderReplacer;
import me.filoghost.chestcommands.hook.PointEconomyHook;
import me.filoghost.chestcommands.hook.VaultEconomyHook;
import me.filoghost.chestcommands.util.Utils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;

public enum DefaultPlaceholder {

    PLAYER("player", (player, argument) -> player.getName()),

    ONLINE("online", (player, argument) -> String.valueOf(Bukkit.getOnlinePlayers().size())),

    MAX_PLAYERS("max_players", (player, argument) -> String.valueOf(Bukkit.getMaxPlayers())),

    WORLD("world", (player, argument) -> player.getWorld().getName()),

    MONEY("money", (player, argument) -> {
        if (VaultEconomyHook.INSTANCE.isEnabled()) {
            return VaultEconomyHook.formatMoney(VaultEconomyHook.getMoney(player));
        } else {
            return "[ECONOMY PLUGIN NOT FOUND]";
        }
    }),

    POINT("point", (player, argument) -> {
        if (PointEconomyHook.INSTANCE.isEnabled()) {
            return Integer.toString(PointEconomyHook.getPoint(player));
        } else {
            return "[PLAYERPOINTS NOT FOUND]";
        }
    }),

    SPACE_LEFT("space_left", (player, argument) -> {
        int multiply = 1;
        if (argument != null && argument.startsWith("x")) {
            multiply = NumberUtils.toInt(argument.substring(1), 1);
        }
        return String.valueOf(Utils.countEmptySlot(player) * multiply);
    });


    private final String identifier;
    private final PlaceholderReplacer replacer;

    DefaultPlaceholder(String identifier, PlaceholderReplacer replacer) {
        this.identifier = identifier;
        this.replacer = replacer;
    }

    public String getIdentifier() {
        return identifier;
    }

    public PlaceholderReplacer getReplacer() {
        return replacer;
    }

}
