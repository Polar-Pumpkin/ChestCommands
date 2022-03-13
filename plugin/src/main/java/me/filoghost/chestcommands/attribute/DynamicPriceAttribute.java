/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package me.filoghost.chestcommands.attribute;

import me.filoghost.chestcommands.icon.InternalConfigurableIcon;
import me.filoghost.chestcommands.icon.requirement.RequiredDynamicMoney;
import org.bukkit.event.inventory.ClickType;

public class DynamicPriceAttribute extends ClickTypedIconAttribute {

    private final String price;

    public DynamicPriceAttribute(String price, AttributeErrorHandler errorHandler) {
        this.price = price;
    }

    @Override
    public void apply(InternalConfigurableIcon icon, ClickType type) {
        icon.addRequirement(type, new RequiredDynamicMoney(price));
    }

    @Override
    public void apply(InternalConfigurableIcon icon) {
        icon.addRequirement(null, new RequiredDynamicMoney(price));
    }

}
