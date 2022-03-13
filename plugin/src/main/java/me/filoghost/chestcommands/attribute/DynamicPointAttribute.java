/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package me.filoghost.chestcommands.attribute;

import me.filoghost.chestcommands.icon.InternalConfigurableIcon;
import me.filoghost.chestcommands.icon.requirement.RequiredDynamicPoint;
import org.bukkit.event.inventory.ClickType;

public class DynamicPointAttribute extends ClickTypedIconAttribute {

    private final String price;

    public DynamicPointAttribute(String price, AttributeErrorHandler errorHandler) {
        this.price = price;
    }

    @Override
    public void apply(InternalConfigurableIcon icon, ClickType type) {
        icon.addRequirement(type, new RequiredDynamicPoint(price));
    }

    @Override
    public void apply(InternalConfigurableIcon icon) {
        icon.addRequirement(null, new RequiredDynamicPoint(price));
    }

}
