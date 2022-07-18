package de.crazypokemondev.pixelmongenerations.bingo.common.config;

import com.google.common.reflect.TypeToken;
import com.lypaka.lypakautils.ConfigurationLoaders.PlayerConfigManager;
import com.pixelmongenerations.common.entity.pixelmon.drops.DropItemHelper;
import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import de.crazypokemondev.pixelmongenerations.bingo.common.loot.LootTables;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.BingoTask;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.CatchPokemonTask;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.EmptyTask;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BingoCardHelper {

    public static Map<Integer, String> generateNewBingoCard(World world) {
        Map<Integer, String> card = new HashMap<>();
        for (int i = 0; i < 25; i++) {
            card.put(i, getRandomTask(world).toString());
        }
        return card;
    }

    private static BingoTask getRandomTask(World world) {
        List<String> types = new ArrayList<>();
        //TaskTypeSwitch
        if (PixelmonBingoConfig.Tasks.CatchPokemon.enabled)
            types.add(CatchPokemonTask.ID);
        if (types.size() < 1) {
            return new EmptyTask();
        }

        int index = world.rand.nextInt(types.size());
        //TaskTypeSwitch
        switch (types.get(index)) {
            case CatchPokemonTask.ID:
                return CatchPokemonTask.getRandomTask(world);
            default:
                return new EmptyTask();
        }
    }

    public static Map<Integer, BingoTask> deserializeTasks(Map<Integer, String> card) {
        Map<Integer, BingoTask> result = new HashMap<>();
        for (Map.Entry<Integer, String> entry : card.entrySet()) {
            result.put(entry.getKey(), BingoTask.fromString(entry.getValue()));
        }
        return result;
    }

    public static void checkForBingo(int slot, EntityPlayerMP player) throws ObjectMappingException {
        PlayerConfigManager bingoCardManager = PixelmonBingoMod.bingoCardManager;
        @SuppressWarnings("UnstableApiUsage")
        Map<Integer, BingoTask> card = BingoCardHelper.deserializeTasks(bingoCardManager.getPlayerConfigNode(
                player.getUniqueID(), "Card").getValue(new TypeToken<Map<Integer, String>>() {}));
        boolean cardCompleted = true;
        boolean rowCompleted = true;
        boolean colCompleted = true;
        boolean topLeftBottomRightDiagonalCompleted = slot / 5 == slot % 5;
        boolean topRightBottomLeftDiagonalCompleted = slot / 5 == 4 - (slot % 5);
        for (int i = 0; i < 25; i++) {
            if (i / 5 == slot / 5 && !card.get(i).isCompleted()) rowCompleted = false;
            if (i % 5 == slot % 5 && !card.get(i).isCompleted()) colCompleted = false;
            if (topLeftBottomRightDiagonalCompleted && i / 5 == i % 5 && !card.get(i).isCompleted())
                topLeftBottomRightDiagonalCompleted = false;
            if (topRightBottomLeftDiagonalCompleted && i / 5 == 4 - (i % 5) && !card.get(i).isCompleted())
                topRightBottomLeftDiagonalCompleted = false;
            if (!card.get(i).isCompleted()) cardCompleted = false;
        }
        int bingoCount = 0;
        List<ItemStack> loot = new ArrayList<>();
        LootTable bingoTable = player.world.getLootTableManager().getLootTableFromLocation(LootTables.bingoLootTable);
        LootContext ctx = new LootContext.Builder((WorldServer) player.world)
                .withPlayer(player)
                .withLuck(player.getLuck()).build();
        if (rowCompleted) {
            bingoCount++;
            loot.addAll(bingoTable.generateLootForPools(player.world.rand, ctx));
        }
        if (colCompleted) {
            bingoCount++;
            loot.addAll(bingoTable.generateLootForPools(player.world.rand, ctx));
        }
        if (topLeftBottomRightDiagonalCompleted) {
            bingoCount++;
            loot.addAll(bingoTable.generateLootForPools(player.world.rand, ctx));
        }
        if (topRightBottomLeftDiagonalCompleted) {
            bingoCount++;
            loot.addAll(bingoTable.generateLootForPools(player.world.rand, ctx));
        }
        if (cardCompleted) {
            LootTable completedTable =
                    player.world.getLootTableManager().getLootTableFromLocation(LootTables.bingoCardLootTable);
            loot.addAll(completedTable.generateLootForPools(player.world.rand, ctx));
        }
        if (bingoCount > 0) {
            for (ItemStack stack : loot) {
                if (!player.addItemStackToInventory(stack)) {
                    DropItemHelper.dropItemOnGround(player, stack, false, false);
                }
            }
            player.sendMessage(new TextComponentTranslation(
                    "messages.pixelmongenerationsbingo.bingo_" + bingoCount));
        }
        if (cardCompleted) player.sendMessage(new TextComponentTranslation(
                "messages.pixelmongenerationsbingo.bingo_card_completed").setStyle(
                        new Style().setColor(TextFormatting.GREEN)));
    }
}
