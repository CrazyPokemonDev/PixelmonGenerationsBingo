package de.crazypokemondev.pixelmongenerations.bingo.common.tasks;

import com.pixelmongenerations.client.gui.GuiHelper;
import com.pixelmongenerations.core.enums.EnumSpecies;
import de.crazypokemondev.pixelmongenerations.bingo.common.config.PixelmonBingoConfig;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CatchPokemonTask extends BingoTask {
    private final EnumSpecies species;
    private static List<EnumSpecies> speciesPool;

    public CatchPokemonTask(EnumSpecies species) {
        this.species = species;
    }

    public CatchPokemonTask(String[] params) {
        if (params.length < 2) throw new IndexOutOfBoundsException("Missing parameter for task of type " + ID);
        int dexId = Integer.parseInt(params[1]);
        Optional<EnumSpecies> species = EnumSpecies.getFromDex(dexId);
        this.species = species
                .orElseThrow(() -> new IndexOutOfBoundsException("Couldn't find pokemon with national dex id " + dexId));
    }

    public static final String ID = "catch-pokemon";

    @Override
    public String toString() {
        return ID + BingoTask.PARAM_SEPARATOR + species.getNationalPokedexInteger();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawIcon(int x, int y, int w, int h, float zLevel) {
        GuiHelper.bindPokeSprite(species, false, -1, -1);
        GuiHelper.drawImageQuad(x, y, w, h, 0, 0, 1, 1, zLevel);
    }

    public static CatchPokemonTask getRandomTask() {
        List<EnumSpecies> speciesPool = getSpeciesPool();
        int index = ThreadLocalRandom.current().nextInt(speciesPool.size());
        return new CatchPokemonTask(speciesPool.get(index));
    }

    @NotNull
    private static List<EnumSpecies> getSpeciesPool() {
        if (speciesPool != null) {
            return speciesPool;
        } else if (PixelmonBingoConfig.Tasks.CatchPokemon.useWhitelist) {
            speciesPool = PixelmonBingoConfig.Tasks.CatchPokemon.whitelist;
        } else {
            speciesPool = new ArrayList<>(Arrays.asList(EnumSpecies.values()));
            if (!PixelmonBingoConfig.Tasks.CatchPokemon.allowLegendaries) {
                for (EnumSpecies s : EnumSpecies.LEGENDARY_ENUMS) {
                    speciesPool.remove(s);
                }
            }
            if (!PixelmonBingoConfig.Tasks.CatchPokemon.allowUltraBeasts) {
                for (EnumSpecies s : EnumSpecies.ULTRA_BEASTS_ENUMS) {
                    speciesPool.remove(s);
                }
            }
            for (EnumSpecies s : PixelmonBingoConfig.Tasks.CatchPokemon.blacklist) {
                speciesPool.remove(s);
            }
        }
        return speciesPool;
    }
}
