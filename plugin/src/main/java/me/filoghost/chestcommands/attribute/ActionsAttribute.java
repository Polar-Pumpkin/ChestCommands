/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.attribute;

import me.filoghost.chestcommands.action.Action;
import me.filoghost.chestcommands.action.DisabledAction;
import me.filoghost.chestcommands.icon.InternalConfigurableIcon;
import me.filoghost.chestcommands.logging.Errors;
import me.filoghost.chestcommands.parsing.ActionParser;
import me.filoghost.chestcommands.parsing.ParseException;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class ActionsAttribute extends ClickTypedIconAttribute {

    private final List<Action> actions;

    public ActionsAttribute(List<String> serializedActions, AttributeErrorHandler errorHandler) {
        actions = new ArrayList<>();

        for (String serializedAction : serializedActions) {
            if (serializedAction == null || serializedAction.isEmpty()) {
                continue; // Skip
            }

            try {
                actions.add(ActionParser.parse(serializedAction));
            } catch (ParseException e) {
                actions.add(new DisabledAction(Errors.User.configurationError(
                        "an action linked to clicking this icon was not executed because it was not valid")));
                errorHandler.onListElementError(serializedAction, e);
            }
        }
    }

    @Override
    public void apply(InternalConfigurableIcon icon, ClickType type) {
        icon.addActions(type, actions);
    }

    @Override
    public void apply(InternalConfigurableIcon icon) {
        icon.addActions(null, actions);
    }

}
