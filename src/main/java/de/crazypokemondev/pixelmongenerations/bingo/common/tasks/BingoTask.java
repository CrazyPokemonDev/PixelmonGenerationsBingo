package de.crazypokemondev.pixelmongenerations.bingo.common.tasks;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BingoTask {

    public static final String PARAM_SEPARATOR = ":";
    public static final String TASK_SEPARATOR = ";";

    /**
     * Returns a string representation of the task. This should be an identifier for the task type,
     * followed by any parameters. The identifier and the parameters should each be separated by {@value PARAM_SEPARATOR}.
     * Cannot contain {@value TASK_SEPARATOR}.
     */
    @Override
    public abstract String toString();

    public static BingoTask fromString(String str) {
        String[] parts = str.split(PARAM_SEPARATOR);
        //TaskTypeSwitch
        switch (parts[0]) {
            case CatchPokemonTask.ID:
                return new CatchPokemonTask(parts);
            case EmptyTask.ID:
            default:
                return new EmptyTask();
        }
    }

    @SideOnly(Side.CLIENT)
    public abstract void drawIcon(int x, int y, int w, int h, float zLevel);
}
