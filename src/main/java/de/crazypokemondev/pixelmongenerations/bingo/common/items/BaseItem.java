package de.crazypokemondev.pixelmongenerations.bingo.common.items;

import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

public class BaseItem extends Item {
    public BaseItem(String itemName) {
        super();
        setRegistryName(PixelmonBingoMod.MOD_ID, itemName);
        setTranslationKey(PixelmonBingoMod.MOD_ID + "." + itemName);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0,
                new ModelResourceLocation(Objects.requireNonNull(getRegistryName()), "inventory"));
    }
}
