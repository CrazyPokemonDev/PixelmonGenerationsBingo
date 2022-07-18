package de.crazypokemondev.pixelmongenerations.bingo.proxy;

import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.SERVER, modid = PixelmonBingoMod.MOD_ID)
public class ServerProxy extends CommonProxy {

}
