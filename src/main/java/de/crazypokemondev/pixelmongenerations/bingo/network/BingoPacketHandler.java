package de.crazypokemondev.pixelmongenerations.bingo.network;

import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import de.crazypokemondev.pixelmongenerations.bingo.network.messages.OpenedBingoCardMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class BingoPacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(PixelmonBingoMod.MOD_ID);
    public static int id = 0;

    public static void registerMessages() {
        INSTANCE.registerMessage(OpenedBingoCardMessage.Handler.class, OpenedBingoCardMessage.class, id++, Side.CLIENT);
    }
}
