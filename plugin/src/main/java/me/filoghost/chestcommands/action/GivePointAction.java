/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.action;

import me.filoghost.chestcommands.hook.PointEconomyHook;
import me.filoghost.chestcommands.logging.Errors;
import me.filoghost.chestcommands.parsing.NumberParser;
import me.filoghost.chestcommands.parsing.ParseException;
import org.bukkit.entity.Player;

public class GivePointAction implements Action {

    private final int pointToGive;

    public GivePointAction(String serializedAction) throws ParseException {
        pointToGive = NumberParser.getInteger(serializedAction);
    }

    @Override
    public void execute(Player player) {
        if (PointEconomyHook.INSTANCE.isEnabled()) {
            PointEconomyHook.givePoint(player, pointToGive);
        } else {
            player.sendMessage(Errors.User.configurationError("PlayerPoints not found"));
        }
    }

}
