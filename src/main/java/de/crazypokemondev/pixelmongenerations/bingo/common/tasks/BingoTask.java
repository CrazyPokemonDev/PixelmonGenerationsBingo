package de.crazypokemondev.pixelmongenerations.bingo.common.tasks;

import com.pixelmongenerations.common.entity.pixelmon.drops.DropItemHelper;
import de.crazypokemondev.pixelmongenerations.bingo.client.gui.GuiResources;
import de.crazypokemondev.pixelmongenerations.bingo.common.loot.LootTables;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class BingoTask {
    private Status status;
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public static final String PARAM_SEPARATOR = ":";
    public static final String TASK_SEPARATOR = ";";

    public BingoTask() {
        this(Status.Open);
    }

    public BingoTask(Status status) {
        this.status = status;
    }

    public abstract String getIdentifier();

    /**
     * Returns a string representation of the task. This should be the status of the task (string representation of
     * {@link Status}), an identifier for the task type, followed by any parameters. The status, identifier and the
     * parameters should each be separated by {@value PARAM_SEPARATOR}.
     * Cannot contain {@value TASK_SEPARATOR}.
     */
    @Override
    public abstract String toString();

    public static BingoTask fromString(String str) {
        String[] parts = str.split(PARAM_SEPARATOR);
        if (parts.length < 2) throw new IndexOutOfBoundsException("Failed to deserialize BingoTask: found no PARAM_SEPARATOR.");

        Status status = Status.valueOf(parts[0]);

        //TaskTypeSwitch
        switch (parts[1]) {
            case CatchPokemonTask.ID:
                return new CatchPokemonTask(status, parts);
            case CraftItemTask.ID:
                return new CraftItemTask(status, parts);
            case EmptyTask.ID:
            default:
                return new EmptyTask(status);
        }
    }

    @SideOnly(Side.CLIENT)
    public abstract void drawIcon(GuiScreen screen, int x, int y, int w, int h, float zLevel);

    protected static void drawCheckMarkIcon(GuiScreen screen, int x, int y, int w, int h) {
        screen.drawTexturedModalRect(
                x + w - GuiResources.CHECK_ICON_WIDTH, y + h - GuiResources.CHECK_ICON_HEIGHT,
                GuiResources.CHECK_ICON_X, GuiResources.CHECK_ICON_Y,
                GuiResources.CHECK_ICON_WIDTH, GuiResources.CHECK_ICON_HEIGHT);
    }

    @NotNull
    public Optional<List<String>> getToolTip() {
        String translateKey = getTranslateKey();
        return Optional.of(Collections.singletonList(I18n.format(translateKey)));
    }

    @NotNull
    public String getTranslateKey() {
        return "gui.pixelmongenerationsbingo.tasks." + getIdentifier() + ".tooltip";
    }

    public void completeTask(EntityPlayerMP player) {
        setStatus(Status.Completed);
        if (player.world.isRemote) return;
        LootTable table = player.world.getLootTableManager().getLootTableFromLocation(LootTables.singleTaskLootTable);
        LootContext ctx = new LootContext.Builder((WorldServer) player.world)
                .withPlayer(player)
                .withLuck(player.getLuck()).build();
        List<ItemStack> stacks = table.generateLootForPools(player.world.rand, ctx);
        for (ItemStack stack : stacks) {
            if (!player.addItemStackToInventory(stack)) {
                DropItemHelper.dropItemOnGround(player, stack, false, false);
            }
        }
    }

    public boolean isCompleted() {
        return status == Status.Completed;
    }

    public enum Status {
        Open,
        Completed
    }
}
