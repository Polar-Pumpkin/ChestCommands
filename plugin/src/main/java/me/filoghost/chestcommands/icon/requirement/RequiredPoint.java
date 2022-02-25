/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.icon.requirement;

import me.filoghost.chestcommands.config.Lang;
import me.filoghost.chestcommands.hook.PointEconomyHook;
import me.filoghost.chestcommands.logging.Errors;
import me.filoghost.fcommons.Preconditions;
import org.bukkit.entity.Player;

public class RequiredPoint implements Requirement {

    private final int pointAmount;

    public RequiredPoint(int pointAmount) {
        Preconditions.checkArgument(pointAmount > 0, "point amount must be positive");
        this.pointAmount = pointAmount;
    }

    @Override
    public boolean hasCost(Player player) {
        if (!PointEconomyHook.INSTANCE.isEnabled()) {
            player.sendMessage(Errors.User.configurationError(
                    "the item has a price, but PlayerPoints was not found. "
                            + "For security, the action has been blocked"));
            return false;
        }

        if (!PointEconomyHook.hasPoint(player, pointAmount)) {
            player.sendMessage(Lang.get().no_point.replace("{point}", Integer.toString(pointAmount)));
            return false;
        }

        return true;
    }

    @Override
    public boolean takeCost(Player player) {
        boolean success = PointEconomyHook.takePoint(player, pointAmount);

        if (!success) {
            player.sendMessage(Errors.User.configurationError("a point transaction couldn't be executed"));
        }

        return success;
    }
}
