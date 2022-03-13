/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.attribute;

import me.filoghost.chestcommands.icon.InternalConfigurableIcon;
import me.filoghost.chestcommands.icon.requirement.item.RequiredItem;
import me.filoghost.chestcommands.icon.requirement.item.RequiredItems;
import me.filoghost.chestcommands.parsing.ItemStackParser;
import me.filoghost.chestcommands.parsing.ParseException;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class RequiredItemsAttribute extends ClickTypedIconAttribute {

    private final List<RequiredItem> requiredItems;

    public RequiredItemsAttribute(List<String> serializedRequiredItems, AttributeErrorHandler errorHandler) {
        requiredItems = new ArrayList<>();

        for (String serializedItem : serializedRequiredItems) {
            try {
                ItemStackParser itemReader = new ItemStackParser(serializedItem, true);
                itemReader.checkNotAir();
                RequiredItem requiredItem = new RequiredItem(itemReader.getMaterial(), itemReader.getAmount());
                if (itemReader.hasExplicitDurability()) {
                    requiredItem.setRestrictiveDurability(itemReader.getDurability());
                }
                requiredItems.add(requiredItem);
            } catch (ParseException e) {
                errorHandler.onListElementError(serializedItem, e);
            }
        }
    }

    @Override
    public void apply(InternalConfigurableIcon icon, ClickType type) {
        icon.addRequirement(type, new RequiredItems(requiredItems));
    }

    @Override
    public void apply(InternalConfigurableIcon icon) {
        icon.addRequirement(null, new RequiredItems(requiredItems));
    }

}
