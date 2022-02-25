/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.hook;

import me.filoghost.fcommons.Preconditions;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public enum PointEconomyHook implements PluginHook {

    INSTANCE;

    private PlayerPointsAPI economy;

    @Override
    public void setup() {
        economy = null;

        if (Bukkit.getPluginManager().getPlugin("PlayerPoints") == null) {
            return;
        }
        economy = JavaPlugin.getPlugin(PlayerPoints.class).getAPI();
    }

    @Override
    public boolean isEnabled() { return economy != null; }

    public static int getPoint(Player player) {
        INSTANCE.checkEnabledState();
        return INSTANCE.economy.look(player.getUniqueId());
    }

    public static boolean hasPoint(Player player, int minimum) {
        INSTANCE.checkEnabledState();
        checkPositiveAmount(minimum);

        int balance = INSTANCE.economy.look(player.getUniqueId());
        return balance >= minimum;
    }

    public static boolean takePoint(Player player, int amount) {
        INSTANCE.checkEnabledState();
        checkPositiveAmount(amount);

        return INSTANCE.economy.take(player.getUniqueId(), amount);
    }

    public static boolean givePoint(Player player, int amount) {
        INSTANCE.checkEnabledState();
        checkPositiveAmount(amount);

        return INSTANCE.economy.give(player.getUniqueId(), amount);
    }

    private static void checkPositiveAmount(int amount) {
        Preconditions.checkArgument(amount >= 0, "amount cannot be negative");
    }

}
