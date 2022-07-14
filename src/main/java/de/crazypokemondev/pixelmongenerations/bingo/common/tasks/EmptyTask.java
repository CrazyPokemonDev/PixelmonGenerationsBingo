package de.crazypokemondev.pixelmongenerations.bingo.common.tasks;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class EmptyTask extends BingoTask {
    public static final String ID = "empty";

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public String toString() {
        return ID;
    }

    @Override
    public void drawIcon(int x, int y, int w, int h, float zLevel) {

    }

    @Override
    @NotNull
    public Optional<List<String>> getToolTip() {
        return Optional.empty();
    }
}
