package de.crazypokemondev.pixelmongenerations.bingo.network.messages;

import de.crazypokemondev.pixelmongenerations.bingo.client.gui.BingoCardScreen;
import de.crazypokemondev.pixelmongenerations.bingo.common.config.BingoCardHelper;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.BingoTask;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class OpenedBingoCardMessage implements IMessage {
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    private Map<Integer, String> card = new HashMap<>();
    public OpenedBingoCardMessage(){}

    public OpenedBingoCardMessage(Map<Integer, String> card) {
        this.card = card;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        for (int i = 0; i < 25; i++) {
            buf.writeCharSequence(card.get(i), CHARSET);
            buf.writeCharSequence(BingoTask.TASK_SEPARATOR, CHARSET);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int i = 0;
        StringBuilder s = new StringBuilder();
        while (buf.readerIndex() < buf.writerIndex()) {
            CharSequence c = buf.readCharSequence(1, CHARSET);
            if (c == BingoTask.TASK_SEPARATOR) {
                card.put(i, s.toString());
                s = new StringBuilder();
                i++;
            }
            else {
                s.append(c);
            }
        }
    }

    public static class Handler implements IMessageHandler<OpenedBingoCardMessage, IMessage> {

        @Override
        public IMessage onMessage(OpenedBingoCardMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().displayGuiScreen(new BingoCardScreen(BingoCardHelper.deserializeTasks(message.card)));
            return null;
        }
    }
}
