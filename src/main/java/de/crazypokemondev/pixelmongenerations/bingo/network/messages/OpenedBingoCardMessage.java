package de.crazypokemondev.pixelmongenerations.bingo.network.messages;

import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import de.crazypokemondev.pixelmongenerations.bingo.client.gui.BingoCardScreen;
import de.crazypokemondev.pixelmongenerations.bingo.common.config.BingoCardHelper;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.BingoTask;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class OpenedBingoCardMessage implements IMessage {
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    private Map<Integer, String> card;
    @Nullable
    private LocalDateTime expirationTime;

    public OpenedBingoCardMessage(){}

    public OpenedBingoCardMessage(Map<Integer, String> card, @Nullable LocalDateTime expirationTime) {
        this.card = card;
        this.expirationTime = expirationTime;
    }

    public OpenedBingoCardMessage(NBTTagList card, @Nullable LocalDateTime expirationTime) {
        this(BingoCardHelper.nbtToMap(card), expirationTime);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (expirationTime != null) {
            buf.writeLong(expirationTime.toEpochSecond(ZoneOffset.UTC));
        }
        else {
            buf.writeLong(-1);
        }
        for (int i = 0; i < 25; i++) {
            buf.writeCharSequence(card.get(i), CHARSET);
            buf.writeCharSequence(BingoTask.TASK_SEPARATOR, CHARSET);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        long epochSecond = buf.readLong();
        if (epochSecond >= 0) {
            expirationTime = LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC);
        } else {
            expirationTime = null;
        }
        card = new HashMap<>();
        int i = 0;
        StringBuilder s = new StringBuilder();
        while (buf.readerIndex() < buf.writerIndex()) {
            CharSequence c = buf.readCharSequence(1, CHARSET);
            if (BingoTask.TASK_SEPARATOR.contentEquals(c)) {
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
            // use proxy to avoid classloading client side only class GuiScreen
            Minecraft.getMinecraft().addScheduledTask(() -> PixelmonBingoMod.proxy
                    .openBingoCardScreen(BingoCardHelper.deserializeTasks(message.card), message.expirationTime));
            return null;
        }
    }
}
