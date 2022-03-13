/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.attribute;

import me.filoghost.chestcommands.icon.InternalConfigurableIcon;
import me.filoghost.chestcommands.icon.requirement.RequiredMoney;
import me.filoghost.chestcommands.logging.Errors;
import me.filoghost.chestcommands.parsing.ParseException;
import org.bukkit.event.inventory.ClickType;

public class PriceAttribute extends ClickTypedIconAttribute {

    private final double price;

    public PriceAttribute(double price, AttributeErrorHandler errorHandler) throws ParseException {
        if (price < 0) {
            throw new ParseException(Errors.Parsing.zeroOrPositive);
        }
        this.price = price;
    }

    @Override
    public void apply(InternalConfigurableIcon icon, ClickType type) {
        icon.addRequirement(type, new RequiredMoney(price));
    }

    @Override
    public void apply(InternalConfigurableIcon icon) {
        icon.addRequirement(null, new RequiredMoney(price));
    }

}
