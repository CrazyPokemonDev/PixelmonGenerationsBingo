package de.crazypokemondev.pixelmongenerations.bingo.common.tasks;

public class EmptyTask extends BingoTask {
    public static final String ID = "empty";
    @Override
    public String toString() {
        return ID;
    }

    @Override
    public void drawIcon(int x, int y, int w, int h, float zLevel) {

    }
}
