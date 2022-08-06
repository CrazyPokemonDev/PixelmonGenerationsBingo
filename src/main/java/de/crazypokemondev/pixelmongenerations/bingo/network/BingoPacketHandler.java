package de.crazypokemondev.pixelmongenerations.bingo.network;

import de.crazypokemondev.pixelmongenerations.bingo.network.messages.OpenedBingoCardMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class BingoPacketHandler {
    public static final String NETWORK_CHANNEL_ID = "pg-bingo";
    public static SimpleNetworkWrapper INSTANCE;
    public static int id = 0;

    public static void registerMessages() {
        INSTANCE.registerMessage(OpenedBingoCardMessage.Handler.class, OpenedBingoCardMessage.class, id++, Side.CLIENT);
    }

    public static void registerChannel() {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(NETWORK_CHANNEL_ID);
    }
}
