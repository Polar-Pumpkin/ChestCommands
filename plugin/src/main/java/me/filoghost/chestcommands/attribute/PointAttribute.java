/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.attribute;

import me.filoghost.chestcommands.icon.InternalConfigurableIcon;
import me.filoghost.chestcommands.icon.requirement.RequiredPoint;
import me.filoghost.chestcommands.logging.Errors;
import me.filoghost.chestcommands.parsing.ParseException;
import org.bukkit.event.inventory.ClickType;

public class PointAttribute extends ClickTypedIconAttribute {

    private final int price;

    public PointAttribute(int price, AttributeErrorHandler errorHandler) throws ParseException {
        if (price < 0) {
            throw new ParseException(Errors.Parsing.zeroOrPositive);
        }
        this.price = price;
    }

    @Override
    public void apply(InternalConfigurableIcon icon, ClickType type) {
        icon.addRequirement(type, new RequiredPoint(price));
    }

    @Override
    public void apply(InternalConfigurableIcon icon) {
        icon.addRequirement(null, new RequiredPoint(price));
    }

}
