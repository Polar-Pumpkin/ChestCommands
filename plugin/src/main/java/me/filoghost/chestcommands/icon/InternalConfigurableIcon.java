/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.icon;

import me.filoghost.chestcommands.action.Action;
import me.filoghost.chestcommands.action.OpenMenuAction;
import me.filoghost.chestcommands.api.MenuView;
import me.filoghost.chestcommands.config.Lang;
import me.filoghost.chestcommands.icon.requirement.Requirement;
import me.filoghost.fcommons.Preconditions;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InternalConfigurableIcon extends BaseConfigurableIcon implements RefreshableIcon {

    private final Map<ClickType, TypedClickHandler> typedHandlers = new HashMap<>();
    private final TypedClickHandler generalHandler = new TypedClickHandler();

    private IconPermission viewPermission;
    private IconPermission clickPermission;
    private String noClickPermissionMessage;
    private ClickResult clickResult;

    public InternalConfigurableIcon(Material material) {
        super(material);
        setPlaceholdersEnabled(true);
        this.clickResult = ClickResult.CLOSE;
    }

    public boolean canViewIcon(Player player) {
        return IconPermission.hasPermission(player, viewPermission);
    }

    public boolean hasViewPermission() {
        return viewPermission != null && !viewPermission.isEmpty();
    }

    public void setClickPermission(String permission) {
        this.clickPermission = new IconPermission(permission);
    }

    public void setNoClickPermissionMessage(String noClickPermissionMessage) {
        this.noClickPermissionMessage = noClickPermissionMessage;
    }

    public void setViewPermission(String viewPermission) {
        this.viewPermission = new IconPermission(viewPermission);
    }

    public void addRequirement(ClickType type, Requirement requirement) {
        if (type == null) {
            generalHandler.addRequirement(requirement);
            return;
        }
        typedHandlers.computeIfAbsent(type, key -> new TypedClickHandler()).addRequirement(requirement);
    }

    public void addActions(ClickType type, Collection<Action> actions) {
        if (type == null) {
            generalHandler.addActions(actions);
            return;
        }
        typedHandlers.computeIfAbsent(type, key -> new TypedClickHandler()).addActions(actions);
    }

    @Override
    public ItemStack render(@NotNull Player viewer) {
        if (canViewIcon(viewer)) {
            return super.render(viewer);
        } else {
            return null;
        }
    }

    @Override
    protected boolean shouldCacheRendering() {
        return super.shouldCacheRendering() && !hasViewPermission();
    }


    public void setClickResult(ClickResult clickResult) {
        Preconditions.notNull(clickResult, "clickResult");
        this.clickResult = clickResult;
    }

    @Override
    public void onClick(@NotNull MenuView menuView, @NotNull Player player, @NotNull InventoryClickEvent event) {
        ClickResult clickResult = onClickGetResult(menuView, player, event);
        if (clickResult == ClickResult.CLOSE) {
            menuView.close();
        }
    }

    private ClickResult onClickGetResult(@NotNull MenuView menuView, @NotNull Player player,
                                         @NotNull InventoryClickEvent event) {
        if (!IconPermission.hasPermission(player, viewPermission)) {
            return ClickResult.KEEP_OPEN;
        }

        if (!IconPermission.hasPermission(player, clickPermission)) {
            if (noClickPermissionMessage != null) {
                player.sendMessage(noClickPermissionMessage);
            } else {
                player.sendMessage(Lang.get().default_no_icon_permission);
            }
            return clickResult;
        }

        final TypedClickHandler handler =
                Optional.ofNullable(typedHandlers.get(event.getClick())).orElse(generalHandler);
        final ClickResult result = handler.handle(player, clickResult);

        // Update the menu after taking requirement costs and executing all actions
        menuView.refresh();

        // Force menu to stay open if actions open another menu
        return result;
    }

    @Override
    public @Nullable ItemStack updateRendering(Player viewer, @Nullable ItemStack currentRendering) {
        if (currentRendering != null && shouldCacheRendering()) {
            // Internal icons do not change, no need to update if the item is already rendered
            return currentRendering;
        }

        if (!canViewIcon(viewer)) {
            // Hide the current item
            return null;
        }

        if (currentRendering == null) {
            // Render item normally
            return render(viewer);
        } else {
            // Internal icons are loaded and then never change, we can safely update only name and lore (for
            // performance)
            ItemMeta meta = currentRendering.getItemMeta();
            meta.setDisplayName(renderName(viewer));
            meta.setLore(renderLore(viewer));
            currentRendering.setItemMeta(meta);
            return currentRendering;
        }
    }

    private static class TypedClickHandler {

        private final List<Requirement> requirements = new ArrayList<>();
        private final List<Action> actions = new ArrayList<>();

        public void addRequirement(Requirement requirement) {
            requirements.add(requirement);
        }

        public void addAction(Action action) {
            actions.add(action);
        }

        public void addActions(Collection<Action> actions) {
            this.actions.addAll(actions);
        }

        public ClickResult handle(Player player, ClickResult def) {
            final Requirement[] requires = requirements.toArray(new Requirement[0]);

            // Check all the requirements
            boolean hasAllRequirements = Requirement.hasAllCosts(player, requires);
            if (!hasAllRequirements) {
                return def;
            }

            // If all requirements are satisfied, take their cost
            boolean takenAllCosts = Requirement.takeAllCosts(player, requires);
            if (!takenAllCosts) {
                return def;
            }

            boolean hasOpenMenuAction = false;
            for (Action action : actions) {
                action.execute(player);
                if (action instanceof OpenMenuAction) {
                    hasOpenMenuAction = true;
                }
            }

            if (hasOpenMenuAction) {
                return ClickResult.KEEP_OPEN;
            } else {
                return def;
            }
        }

    }

}
