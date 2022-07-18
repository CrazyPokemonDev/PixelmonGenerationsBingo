package de.crazypokemondev.pixelmongenerations.bingo.common.tasks;

import com.pixelmongenerations.client.gui.GuiHelper;
import com.pixelmongenerations.core.enums.EnumSpecies;
import de.crazypokemondev.pixelmongenerations.bingo.client.gui.GuiResources;
import de.crazypokemondev.pixelmongenerations.bingo.common.config.PixelmonBingoConfig;
import de.crazypokemondev.pixelmongenerations.bingo.common.localization.LocalizationHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentSelector;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CatchPokemonTask extends BingoTask {
    private final EnumSpecies species;
    public EnumSpecies getSpecies() {
        return species;
    }

    private static List<EnumSpecies> speciesPool;

    public CatchPokemonTask(EnumSpecies species) {
        this.species = species;
    }

    public CatchPokemonTask(Status status, String[] params) {
        super(status);
        if (params.length < 3) throw new IndexOutOfBoundsException("Missing parameter for task of type " + ID);
        int dexId = Integer.parseInt(params[2]);
        Optional<EnumSpecies> species = EnumSpecies.getFromDex(dexId);
        this.species = species
                .orElseThrow(() -> new IndexOutOfBoundsException("Couldn't find pokemon with national dex id " + dexId));
    }

    public static final String ID = "catch_pokemon";
    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public String toString() {
        return getStatus().toString() + PARAM_SEPARATOR + ID + PARAM_SEPARATOR + species.getNationalPokedexInteger();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawIcon(GuiScreen screen, int x, int y, int w, int h, float zLevel) {
        GuiHelper.bindPokeSprite(species, false, -1, -1);
        GuiHelper.drawImageQuad(x, y, w, h, 0, 0, 1, 1, zLevel);
        screen.mc.getTextureManager().bindTexture(GuiResources.guiIcons);
        switch (getStatus()) {
            case Completed:
                drawCheckMarkIcon(screen, x, y, w, h);
                break;
            case Open:
            default:
                screen.drawTexturedModalRect(
                        x + w - GuiResources.CATCH_POKEMON_ICON_WIDTH, y + h - GuiResources.CATCH_POKEMON_ICON_HEIGHT,
                        GuiResources.CATCH_POKEMON_ICON_X, GuiResources.CATCH_POKEMON_ICON_Y,
                        GuiResources.CATCH_POKEMON_ICON_WIDTH, GuiResources.CATCH_POKEMON_ICON_HEIGHT);
                break;
        }
    }

    public static CatchPokemonTask getRandomTask(World world) {
        List<EnumSpecies> speciesPool = getSpeciesPool();
        int index = world.rand.nextInt(speciesPool.size());
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
            if (!PixelmonBingoConfig.Tasks.CatchPokemon.allowMissingNo) {
                speciesPool.remove(EnumSpecies.MissingNo);
            }
            for (EnumSpecies s : PixelmonBingoConfig.Tasks.CatchPokemon.blacklist) {
                speciesPool.remove(s);
            }
        }
        return speciesPool;
    }

    @NotNull
    @Override
    public Optional<List<String>> getToolTip() {
        String translateKey = getTranslateKey();
        String formattedString = I18n.format(translateKey, species.getPokemonName());
        return Optional.of(Collections.singletonList(formattedString));
    }

    @Override
    public void completeTask(EntityPlayerMP player) {
        super.completeTask(player);
        player.sendMessage(new TextComponentTranslation(
                "messages.pixelmongenerationsbingo.tasks." + ID + ".completed",
                new TextComponentTranslation(LocalizationHelper.getSpeciesNameKey(species))));
    }
}
