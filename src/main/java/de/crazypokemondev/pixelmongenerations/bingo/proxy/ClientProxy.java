package de.crazypokemondev.pixelmongenerations.bingo.proxy;

import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import de.crazypokemondev.pixelmongenerations.bingo.client.gui.BingoCardScreen;
import de.crazypokemondev.pixelmongenerations.bingo.common.items.ModItems;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.BingoTask;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.time.LocalDateTime;
import java.util.Map;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = PixelmonBingoMod.MOD_ID)
public class ClientProxy extends CommonProxy {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModItems.initModels();
    }

    @Override
    public void openBingoCardScreen(Map<Integer, BingoTask> tasks, LocalDateTime expirationTime) {
        Minecraft.getMinecraft().displayGuiScreen(new BingoCardScreen(tasks, expirationTime));
    }
}
