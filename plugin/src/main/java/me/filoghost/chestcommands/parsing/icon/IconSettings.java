/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.chestcommands.parsing.icon;

import com.google.common.base.Enums;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.filoghost.chestcommands.attribute.AttributeErrorHandler;
import me.filoghost.chestcommands.attribute.ClickTypedIconAttribute;
import me.filoghost.chestcommands.attribute.IconAttribute;
import me.filoghost.chestcommands.icon.InternalConfigurableIcon;
import me.filoghost.chestcommands.logging.Errors;
import me.filoghost.chestcommands.parsing.ParseException;
import me.filoghost.fcommons.config.ConfigPath;
import me.filoghost.fcommons.config.ConfigSection;
import me.filoghost.fcommons.config.ConfigType;
import me.filoghost.fcommons.config.ConfigValue;
import me.filoghost.fcommons.config.exception.ConfigValueException;
import me.filoghost.fcommons.config.exception.InvalidConfigValueException;
import me.filoghost.fcommons.config.exception.MissingConfigValueException;
import me.filoghost.fcommons.logging.ErrorCollector;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class IconSettings {

    private final Path menuFile;
    private final ConfigPath configPath;
    private final Map<AttributeType, IconAttribute> validAttributes;
    private final Multimap<ClickType, ClickTypedIconAttribute> typedAttributes;
    private final Set<AttributeType> invalidAttributes;

    public IconSettings(Path menuFile, ConfigPath configPath) {
        this.menuFile = menuFile;
        this.configPath = configPath;
        this.validAttributes = new EnumMap<>(AttributeType.class);
        this.typedAttributes = HashMultimap.create();
        this.invalidAttributes = new HashSet<>();
    }

    public InternalConfigurableIcon createIcon() {
        InternalConfigurableIcon icon = new InternalConfigurableIcon(Material.BEDROCK);

        for (IconAttribute attribute : validAttributes.values()) {
            attribute.apply(icon);
        }

        for (Entry<ClickType, ClickTypedIconAttribute> entry : typedAttributes.entries()) {
            final ClickType type = entry.getKey();
            final ClickTypedIconAttribute attribute = entry.getValue();
            attribute.apply(icon, type);
        }

        return icon;
    }

    public IconAttribute getAttributeValue(AttributeType attributeType) {
        return validAttributes.get(attributeType);
    }

    public boolean isMissingAttribute(AttributeType attributeType) {
        return !validAttributes.containsKey(attributeType) && !invalidAttributes.contains(attributeType);
    }

    @SuppressWarnings("UnstableApiUsage")
    public void loadFrom(ConfigSection config, ErrorCollector errorCollector) {
        for (Entry<ConfigPath, ConfigValue> root : config.toMap().entrySet()) {
            final ConfigPath key = root.getKey();
            final ConfigValue value = root.getValue();

            final ClickType click = Enums.getIfPresent(ClickType.class, key.asRawKey()).orNull();
            if (click != null) {
                try {
                    final ConfigSection section = value.asRequired(ConfigType.SECTION);
                    for (Entry<ConfigPath, ConfigValue> typed : section.toMap().entrySet()) {
                        parseIconAttribute(typed.getKey(), typed.getValue(), errorCollector, click);
                    }
                } catch (MissingConfigValueException | InvalidConfigValueException e) {
                    errorCollector.add(e, Errors.Menu.invalidAttribute(this, key));
                }
            } else {
                parseIconAttribute(key, value, errorCollector, null);
            }
        }
    }

    private void parseIconAttribute(ConfigPath key, ConfigValue value, ErrorCollector errorCollector, ClickType click) {
        AttributeType attributeType = null;
        try {
            attributeType = AttributeType.fromConfigKey(key);
            if (attributeType == null) {
                throw new ParseException(Errors.Parsing.unknownAttribute);
            }

            AttributeErrorHandler errorHandler = (String listElement, ParseException e) -> {
                errorCollector.add(e, Errors.Menu.invalidAttributeListElement(this, key, listElement));
            };

            IconAttribute iconAttribute = attributeType.getParser().parse(value, errorHandler);
            if (click != null && iconAttribute instanceof ClickTypedIconAttribute) {
                typedAttributes.put(click, (ClickTypedIconAttribute) iconAttribute);
            } else {
                validAttributes.put(attributeType, iconAttribute);
            }
        } catch (ParseException | ConfigValueException e) {
            errorCollector.add(e, Errors.Menu.invalidAttribute(this, key));
            if (attributeType != null) {
                invalidAttributes.add(attributeType);
            }
        }
    }

    public Path getMenuFile() {
        return menuFile;
    }

    public ConfigPath getConfigPath() {
        return configPath;
    }

}
