package de.crazypokemondev.pixelmongenerations.bingo.common.items;

import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import net.minecraft.item.Item;

public class BaseItem extends Item {
    public BaseItem(String itemName) {
        super();
        setRegistryName(itemName);
        setUnlocalizedName(PixelmonBingoMod.MOD_ID + "." + itemName);
    }
}
