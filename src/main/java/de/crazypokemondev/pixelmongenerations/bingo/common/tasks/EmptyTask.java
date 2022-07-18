package de.crazypokemondev.pixelmongenerations.bingo.common.tasks;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class EmptyTask extends BingoTask {
    public static final String ID = "empty";

    public EmptyTask() {
        super();
    }

    public EmptyTask(Status status) {
        super(status);
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public String toString() {
        return getStatus().toString() + PARAM_SEPARATOR + ID;
    }

    @Override
    public void drawIcon(GuiScreen screen, int x, int y, int w, int h, float zLevel) {

    }

    @Override
    @NotNull
    public Optional<List<String>> getToolTip() {
        return Optional.empty();
    }
}
