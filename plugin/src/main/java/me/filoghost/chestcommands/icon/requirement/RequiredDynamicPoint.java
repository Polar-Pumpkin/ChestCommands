/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package me.filoghost.chestcommands.icon.requirement;

import me.filoghost.chestcommands.placeholder.PlaceholderString;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RequiredDynamicPoint implements Requirement {

    @NotNull
    private final String price;

    public RequiredDynamicPoint(@NotNull String price) {
        this.price = price;
    }

    @Override
    public boolean hasCost(Player player) {
        return calculate(player).hasCost(player);
    }

    @Override
    public boolean takeCost(Player player) {
        return calculate(player).takeCost(player);
    }

    private RequiredPoint calculate(Player player) {
        return new RequiredPoint(Integer.parseInt(PlaceholderString.of(price).getValue(player)));
    }

}
