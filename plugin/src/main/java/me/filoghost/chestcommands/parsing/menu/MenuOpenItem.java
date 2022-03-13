/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.parsing.menu;

import me.filoghost.fcommons.Preconditions;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class MenuOpenItem {

    private final Material material;
    private final OpenerType openerType;
    private short durability;
    private boolean isRestrictiveDurability;

    public MenuOpenItem(Material material, OpenerType openerType) {
        Preconditions.checkArgumentNotAir(material, "material");
        Preconditions.notNull(openerType, "clickType");
        
        this.material = material;
        this.openerType = openerType;
    }

    public void setRestrictiveDurability(short durability) {
        this.durability = durability;
        this.isRestrictiveDurability = true;
    }

    public boolean matches(ItemStack item, Action action) {
        if (item == null) {
            return false;
        }

        if (this.material != item.getType()) {
            return false;
        }
        
        if (isRestrictiveDurability && this.durability != item.getDurability()) {
            return false;
        }

        return openerType.isValidInteract(action);
    }
}
