package de.crazypokemondev.pixelmongenerations.bingo.common.listeners;

import com.google.common.reflect.TypeToken;
import com.lypaka.lypakautils.ConfigurationLoaders.PlayerConfigManager;
import com.pixelmongenerations.api.events.CaptureEvent;
import com.pixelmongenerations.common.entity.pixelmon.EntityPixelmon;
import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import de.crazypokemondev.pixelmongenerations.bingo.common.config.BingoCardHelper;
import de.crazypokemondev.pixelmongenerations.bingo.common.items.BingoCard;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.BingoTask;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.CatchPokemonTask;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CaptureListener {
    @SubscribeEvent
    public void onPokemonCaught(CaptureEvent.SuccessfulCaptureEvent event) throws ObjectMappingException {
        if (event.getPlayer().world.isRemote) return;
        PlayerConfigManager bingoCardManager = PixelmonBingoMod.bingoCardManager;
        EntityPixelmon pokemon = event.getPokemon();
        EntityPlayerMP player = event.getPlayer();
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
                    if (card.get(i).isCompleted() || !(card.get(i) instanceof CatchPokemonTask)) continue;
                    CatchPokemonTask task = (CatchPokemonTask) card.get(i);
                    if (task.getSpecies() == pokemon.getSpecies()) {
                        task.completeTask(player);
                        nbtTagList.set(i, new NBTTagString(task.toString()));
                        nbt.setTag("card", nbtTagList);
                        stack.setTagCompound(nbt);
                        player.inventory.setInventorySlotContents(inventorySlot, stack);
                        BingoCardHelper.checkForBingo(i, player, card);
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
                if (card.get(i).isCompleted() || !(card.get(i) instanceof CatchPokemonTask)) continue;
                CatchPokemonTask task = (CatchPokemonTask) card.get(i);
                if (task.getSpecies() == pokemon.getSpecies()) {
                    task.completeTask(player);
                    rawCard.put(i, task.toString());
                    configNodeCard.setValue(rawCard);
                    bingoCardManager.savePlayer(playerUuid);
                    BingoCardHelper.checkForBingo(i, player, card);
                    return;
                }
            }
        }
    }
}
