/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package me.filoghost.chestcommands.attribute;

import me.filoghost.chestcommands.icon.InternalConfigurableIcon;
import org.bukkit.event.inventory.ClickType;

public abstract class ClickTypedIconAttribute implements IconAttribute {

    public abstract void apply(InternalConfigurableIcon icon, ClickType type);

}
