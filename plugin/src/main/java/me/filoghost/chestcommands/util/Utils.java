/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.util;

import me.filoghost.fcommons.Strings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    public static String formatEnum(@NotNull Enum<?> enumValue) {
        return Strings.capitalizeFully(enumValue.name().replace("_", " "));
    }

    public static String addYamlExtension(@NotNull String fileName) {
        if (fileName.toLowerCase().endsWith(".yml")) {
            return fileName;
        } else {
            return fileName + ".yml";
        }
    }

    public static long countEmptySlot(Player player) {
        final PlayerInventory inventory = player.getInventory();
        final List<ItemStack> items = new ArrayList<>(Arrays.asList(inventory.getContents()));
        items.removeAll(Arrays.asList(inventory.getArmorContents()));
        items.remove(inventory.getItemInOffHand());
        return items.stream()
                .filter(item -> item == null || item.getType() == Material.AIR)
                .count();
    }

}
