package de.crazypokemondev.pixelmongenerations.bingo.common.config;

import com.google.common.reflect.TypeToken;
import com.lypaka.lypakautils.ConfigurationLoaders.PlayerConfigManager;
import com.pixelmongenerations.common.entity.pixelmon.drops.DropItemHelper;
import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import de.crazypokemondev.pixelmongenerations.bingo.common.items.BingoCard;
import de.crazypokemondev.pixelmongenerations.bingo.common.loot.LootTables;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.BingoTask;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.CatchPokemonTask;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.CraftItemTask;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.EmptyTask;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.util.Constants;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BingoCardHelper {

    public static Map<Integer, String> generateNewBingoCard(World world) {
        Map<Integer, String> card = new HashMap<>();
        for (int i = 0; i < 25; i++) {
            card.put(i, getRandomTask(world).toString());
        }
        return card;
    }

    private static BingoTask getRandomTask(World world) {
        Map<String, Integer> types = new HashMap<>();
        //TaskTypeSwitch
        if (PixelmonBingoConfig.Tasks.CatchPokemon.enabled)
            types.put(CatchPokemonTask.ID, PixelmonBingoConfig.Tasks.CatchPokemon.weight);
        if (PixelmonBingoConfig.Tasks.CraftItem.enabled)
            types.put(CraftItemTask.ID, PixelmonBingoConfig.Tasks.CraftItem.weight);
        if (types.size() < 1) {
            return new EmptyTask();
        }

        int index = world.rand.nextInt(getTotalWeight(types));
        for (Map.Entry<String, Integer> entry : types.entrySet()) {
            if (index < entry.getValue()) {
                //TaskTypeSwitch
                switch(entry.getKey()) {
                    case CatchPokemonTask.ID:
                        return CatchPokemonTask.getRandomTask(world);
                    case CraftItemTask.ID:
                        return CraftItemTask.getRandomTask(world);
                    default:
                        return new EmptyTask();
                }
            }
            else
                index -= entry.getValue();
        }
        return new EmptyTask();
    }

    private static int getTotalWeight(Map<String, Integer> map) {
        int sum = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            sum += entry.getValue();
        }
        return sum;
    }

    public static Map<Integer, BingoTask> deserializeTasks(Map<Integer, String> card) {
        Map<Integer, BingoTask> result = new HashMap<>();
        for (Map.Entry<Integer, String> entry : card.entrySet()) {
            result.put(entry.getKey(), BingoTask.fromString(entry.getValue()));
        }
        return result;
    }

    public static Map<Integer, BingoTask> deserializeTasks(NBTTagList card) {
        return deserializeTasks(nbtToMap(card));
    }

    public static void checkForBingo(int slot, EntityPlayerMP player, Map<Integer, BingoTask> card) {
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

    public static boolean isCardCompleted(Map<Integer, String> card) {
        return deserializeTasks(card).entrySet().stream().allMatch(entry -> entry.getValue().isCompleted());
    }

    public static NBTTagList generateNewBingoCardNbt(World world) {
        NBTTagList list = new NBTTagList();
        Map<Integer, String> card = generateNewBingoCard(world);
        for (int i = 0; i < 25; i++) {
            NBTTagString nbt = new NBTTagString(card.get(i));
            list.appendTag(nbt);
        }
        return list;
    }

    public static Map<Integer, String> nbtToMap(NBTTagList card) {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < 25; i++) {
            map.put(i, card.getStringTagAt(i));
        }
        return map;
    }

    public static <T extends BingoTask> void handleFirstIncompleteTask(Class<T> type, EntityPlayerMP player, Predicate<T> predicate, Consumer<T> handler) throws ObjectMappingException {
        PlayerConfigManager bingoCardManager = PixelmonBingoMod.bingoCardManager;
        boolean handleMagic = false;
        for (int inventorySlot = 0; inventorySlot < player.inventory.getSizeInventory(); inventorySlot++) {
            ItemStack stack = player.inventory.getStackInSlot(inventorySlot);
            if (!(stack.getItem() instanceof BingoCard)) continue;
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) continue;
            if (nbt.getBoolean("isMagic")) {
                handleMagic = true;
            } else if (nbt.hasKey("card")) {
                NBTTagList nbtTagList = nbt.getTagList("card", Constants.NBT.TAG_STRING);
                Map<Integer, BingoTask> card = BingoCardHelper.deserializeTasks(nbtTagList);
                for (int i = 0; i < 25; i++) {
                    if (card.get(i).isCompleted() || !(type.isInstance(card.get(i)))) continue;
                    T task = type.cast(card.get(i));
                    if (predicate.test(task)) {
                        handler.accept(task);
                        nbtTagList.set(i, new NBTTagString(task.toString()));
                        nbt.setTag("card", nbtTagList);
                        stack.setTagCompound(nbt);
                        player.inventory.setInventorySlotContents(inventorySlot, stack);
                        checkForBingo(i, player, card);
                        return;
                    }
                }
            }
        }
        if (handleMagic) {
            UUID playerUuid = player.getUniqueID();

            CommentedConfigurationNode configNodeCard = bingoCardManager.getPlayerConfigNode(playerUuid, "Card");
            if (configNodeCard == null) return;
            @SuppressWarnings("UnstableApiUsage")
            Map<Integer, String> rawCard = configNodeCard.getValue(new TypeToken<Map<Integer, String>>() {
            });
            Map<Integer, BingoTask> card = BingoCardHelper.deserializeTasks(rawCard);
            for (int i = 0; i < 25; i++) {
                if (card.get(i).isCompleted() || !(type.isInstance(card.get(i)))) continue;
                T task = type.cast(card.get(i));
                if (predicate.test(task)) {
                    handler.accept(task);
                    rawCard.put(i, task.toString());
                    configNodeCard.setValue(rawCard);
                    bingoCardManager.savePlayer(playerUuid);
                    checkForBingo(i, player, card);
                    return;
                }
            }
        }
    }
}
