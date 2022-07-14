package de.crazypokemondev.pixelmongenerations.bingo.client.gui;

import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.BingoTask;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BingoCardScreen extends GuiScreen {
    private final Map<Integer, BingoTask> card;

    public static final int WIDTH = 256;
    public static final int HEIGHT = 256;
    public static final int OFFSET_X = 48;
    public static final int OFFSET_Y = 64;
    public static final int COL_WIDTH = 32;
    public static final int ROW_HEIGHT = 32;

    private static final ResourceLocation background =
            new ResourceLocation(PixelmonBingoMod.MOD_ID, "textures/gui/bingoCard.png");

    public BingoCardScreen(Map<Integer, BingoTask> card) {
        this.card = card;
        zLevel = -0.5f;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWorldBackground(0);
        mc.getTextureManager().bindTexture(background);
        int x = (width - WIDTH) / 2;
        int y = (height - HEIGHT) / 2;
        drawTexturedModalRect(x, y, 0, 0, WIDTH, HEIGHT);
        Optional<List<String>> toolTip = Optional.empty();
        for (int i = 0; i < 25; i++) {
            int row = i / 5;
            int col = i % 5;
            BingoTask task = card.get(i);
            if (task == null) continue;
            int iconX = x + OFFSET_X + col * COL_WIDTH;
            int iconY = y + OFFSET_Y + row * ROW_HEIGHT;
            task.drawIcon(iconX, iconY,
                    COL_WIDTH, ROW_HEIGHT, zLevel);
            if (mouseX >= iconX && mouseX < iconX + COL_WIDTH
                    && mouseY >= iconY && mouseY < iconY + ROW_HEIGHT) {
                toolTip = task.getToolTip();
            }
        }
        toolTip.ifPresent(strings -> drawHoveringText(strings, mouseX, mouseY, fontRenderer));

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
