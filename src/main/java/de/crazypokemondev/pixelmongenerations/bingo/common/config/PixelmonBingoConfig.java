package de.crazypokemondev.pixelmongenerations.bingo.common.config;

import com.google.common.reflect.TypeToken;
import com.lypaka.lypakautils.ConfigurationLoaders.BasicConfigManager;
import com.pixelmongenerations.core.enums.EnumSpecies;
import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PixelmonBingoConfig {

    private static final int CONFIG_FILE_INDEX = 0;

    public static class Tasks {
        public static class CatchPokemon {
            public static boolean enabled;
            public static boolean allowLegendaries;
            public static boolean allowUltraBeasts;
            public static boolean allowMissingNo;
            public static List<EnumSpecies> blacklist;
            public static boolean useWhitelist;
            public static List<EnumSpecies> whitelist;
        }
    }

    public static long expirationTimer;

    public static class Formatting {
        public static String dateTimeFormat;
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void load(BasicConfigManager configManager) throws ObjectMappingException {
        Tasks.CatchPokemon.enabled =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CatchPokemon", "enabled")
                .getBoolean();
        Tasks.CatchPokemon.allowLegendaries =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CatchPokemon", "allowLegendaries")
                .getBoolean();
        Tasks.CatchPokemon.allowUltraBeasts =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CatchPokemon", "allowUltraBeasts")
                .getBoolean();
        Tasks.CatchPokemon.allowMissingNo =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CatchPokemon", "allowMissingNo")
                .getBoolean();
        Tasks.CatchPokemon.blacklist = makeEnumSpeciesList(
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CatchPokemon", "blacklist")
                .getList(new TypeToken<String>() {})
        );
        Tasks.CatchPokemon.useWhitelist =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CatchPokemon", "useWhitelist")
                .getBoolean();
        Tasks.CatchPokemon.whitelist = makeEnumSpeciesList(
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Tasks", "CatchPokemon", "whitelist")
                .getList(new TypeToken<String>() {})
        );

        expirationTimer = configManager.getConfigNode(CONFIG_FILE_INDEX, "ExpirationTimer").getLong();

        Formatting.dateTimeFormat =
                configManager.getConfigNode(CONFIG_FILE_INDEX, "Formatting", "DateTimeFormat").getString();
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

}
