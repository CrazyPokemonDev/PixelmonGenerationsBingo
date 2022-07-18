package de.crazypokemondev.pixelmongenerations.bingo.proxy;

import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.BingoTask;
import net.minecraftforge.fml.common.Mod;

import java.time.LocalDateTime;
import java.util.Map;

@Mod.EventBusSubscriber(modid = PixelmonBingoMod.MOD_ID)
public class CommonProxy {

    public void openBingoCardScreen(Map<Integer, BingoTask> tasks, LocalDateTime expirationTime) {

    }
}
