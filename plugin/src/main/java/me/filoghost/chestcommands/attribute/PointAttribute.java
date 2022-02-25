/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.attribute;

import me.filoghost.chestcommands.icon.InternalConfigurableIcon;
import me.filoghost.chestcommands.logging.Errors;
import me.filoghost.chestcommands.parsing.ParseException;

public class PointAttribute implements IconAttribute {

    private final int price;

    public PointAttribute(int price, AttributeErrorHandler errorHandler) throws ParseException {
        if (price < 0) {
            throw new ParseException(Errors.Parsing.zeroOrPositive);
        }
        this.price = price;
    }

    @Override
    public void apply(InternalConfigurableIcon icon) {
        icon.setRequiredPoint(price);
    }

}
