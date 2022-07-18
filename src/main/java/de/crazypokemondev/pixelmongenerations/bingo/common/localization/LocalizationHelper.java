package de.crazypokemondev.pixelmongenerations.bingo.common.localization;

import com.pixelmongenerations.core.enums.EnumSpecies;

public class LocalizationHelper {
    public static String getSpeciesNameKey(EnumSpecies species) {
        return "pixelmon." + species.getPokemonName().toLowerCase() + ".name";
    }
}
