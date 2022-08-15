package de.crazypokemondev.pixelmongenerations.bingo.common.config;

import com.google.common.reflect.TypeToken;
import com.lypaka.lypakautils.ConfigurationLoaders.BasicConfigManager;
import com.pixelmongenerations.core.enums.EnumSpecies;
import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import net.minecraft.item.Item;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PixelmonBingoConfig {

    private static final int CONFIG_FILE_INDEX = 0;

    public static class Tasks {
        public static class CatchPokemon {
            public static boolean enabled;
            public static int weight;
            public static boolean allowLegendaries;
            public static boolean allowUltraBeasts;
            public static boolean allowMissingNo;
            public static List<EnumSpecies> blacklist;
            public static boolean useWhitelist;
            public static List<EnumSpecies> whitelist;
        }

        public static class CraftItem {
            public static boolean enabled;
            public static int weight;
            public static List<Item> items;
        }
    }

    public static long expirationTimer;

    public static class Formatting {
        public static String dateTimeFormat;
    }

    public static class BingoCard {
        public static boolean useMagicCard;
        public static boolean soldByShopkeepers;
        public static int buyPrice;
        public static int sellPrice;
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void load(BasicConfigManager configManager) throws ObjectMappingException {
        int version = configManager.getConfigNode(CONFIG_FILE_INDEX, "Version").getInt(1);
        applyMigrations(configManager, version);

        Tasks.CatchPokemon.enabled =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CatchPokemon", "enabled")
                .getBoolean(true);
        Tasks.CatchPokemon.weight =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CatchPokemon", "weight")
                .getInt(4);
        Tasks.CatchPokemon.allowLegendaries =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CatchPokemon", "allowLegendaries")
                .getBoolean(false);
        Tasks.CatchPokemon.allowUltraBeasts =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CatchPokemon", "allowUltraBeasts")
                .getBoolean(false);
        Tasks.CatchPokemon.allowMissingNo =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CatchPokemon", "allowMissingNo")
                .getBoolean(false);
        Tasks.CatchPokemon.blacklist = makeEnumSpeciesList(
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CatchPokemon", "blacklist")
                .getList(new TypeToken<String>() {}, new ArrayList<>())
        );
        Tasks.CatchPokemon.useWhitelist =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CatchPokemon", "useWhitelist")
                .getBoolean(false);
        Tasks.CatchPokemon.whitelist = makeEnumSpeciesList(
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CatchPokemon", "whitelist")
                .getList(new TypeToken<String>() {}, new ArrayList<>())
        );

        Tasks.CraftItem.enabled = configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CraftItem", "enabled")
                .getBoolean(true);
        Tasks.CraftItem.weight = configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CraftItem", "weight")
                .getInt(1);
        // items are loaded postInit

        expirationTimer = configManager.getConfigNode(CONFIG_FILE_INDEX, "ExpirationTimer")
                .getLong(1440);

        Formatting.dateTimeFormat =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Formatting", "DateTimeFormat")
                        .getString("yyyy-MM-dd HH:mm");

        BingoCard.useMagicCard =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "BingoCard", "MagicCard")
                        .getBoolean(false);
        BingoCard.soldByShopkeepers =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "BingoCard", "SoldByShopkeepers")
                        .getBoolean(true);
        BingoCard.buyPrice =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "BingoCard", "BuyPrice")
                        .getInt(100);
        BingoCard.sellPrice =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "BingoCard", "SellPrice")
                        .getInt(50);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void loadPostInit(BasicConfigManager configManager) throws ObjectMappingException {
        Tasks.CraftItem.items = makeItemList(
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CraftItem", "items")
                        .getList(new TypeToken<String>() {}, new ArrayList<>())
        );
    }

    private static List<EnumSpecies> makeEnumSpeciesList(List<String> strings) {
        List<EnumSpecies> list = new ArrayList<>();
        for (String s : strings) {
            Optional<EnumSpecies> species = EnumSpecies.getFromName(s);
            if (species.isPresent()) {
                list.add(species.get());
            } else {
                try {
                    int id = Integer.parseInt(s);
                    species = EnumSpecies.getFromDex(id);
                    if (species.isPresent()) {
                        list.add(species.get());
                    }
                    else {
                        PixelmonBingoMod.LOGGER.warn("Unrecognized pokemon: {}", s);
                    }
                } catch (NumberFormatException e) {
                    PixelmonBingoMod.LOGGER.warn("Unrecognized pokemon: {}", s);
                }
            }
        }
        return list;
    }

    private static List<Item> makeItemList(List<String> strings) {
        List<Item> list = new ArrayList<>();
        for (String s : strings) {
            Item i = Item.getByNameOrId(s);
            if (i != null) {
                list.add(i);
            }
            else {
                PixelmonBingoMod.LOGGER.warn("Unrecognized item: {}", s);
            }
        }
        return list;
    }

    private static void applyMigrations(BasicConfigManager configManager, int currentVersion) {
        if (migrations.containsKey(currentVersion)) {
            migrations.get(currentVersion).accept(configManager);
        }
        configManager.save();
    }

    private static final Map<Integer, Consumer<BasicConfigManager>> migrations = Stream.of(
            new AbstractMap.SimpleEntry<Integer, Consumer<BasicConfigManager>>(1, configManager -> {
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CraftItem", "enabled").setValue(true);
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CraftItem", "weight").setValue(1);
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CraftItem", "items").setValue(
                        Arrays.asList("pixelmon:dive_ball", "pixelmon:dusk_ball", "pixelmon:fast_ball",
                                "pixelmon:friend_ball", "pixelmon:great_ball", "pixelmon:heal_ball",
                                "pixelmon:heavy_ball", "pixelmon:level_ball", "pixelmon:love_ball", "pixelmon:lure_ball",
                                "pixelmon:luxury_ball", "pixelmon:moon_ball", "pixelmon:nest_ball", "pixelmon:net_ball",
                                "pixelmon:poke_ball", "pixelmon:premier_ball", "pixelmon:quick_ball",
                                "pixelmon:repeat_ball", "pixelmon:safari_ball", "pixelmon:sport_ball",
                                "pixelmon:timer_ball", "pixelmon:ultra_ball"));
            })
    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

}
