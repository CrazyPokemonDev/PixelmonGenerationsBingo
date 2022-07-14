package de.crazypokemondev.pixelmongenerations.bingo.client.gui;

import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.BingoTask;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class BingoCardScreen extends GuiScreen {
    private Map<Integer, BingoTask> card;

    public static final int WIDTH = 256;
    public static final int HEIGHT = 256;

    private static final ResourceLocation background =
            new ResourceLocation(PixelmonBingoMod.MOD_ID, "textures/gui/bingoCard.png");

    public BingoCardScreen(Map<Integer, BingoTask> card) {
        this.card = card;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        mc.getTextureManager().bindTexture(background);
        int x = (width - WIDTH) / 2;
        int y = (height - HEIGHT) / 2;
        drawTexturedModalRect(x, y, 0, 0, WIDTH, HEIGHT);
    }
}
