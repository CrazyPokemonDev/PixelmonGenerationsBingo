package de.crazypokemondev.pixelmongenerations.bingo.common.listeners;

import com.google.common.reflect.TypeToken;
import com.lypaka.lypakautils.ConfigurationLoaders.PlayerConfigManager;
import com.pixelmongenerations.api.events.CaptureEvent;
import com.pixelmongenerations.common.entity.pixelmon.EntityPixelmon;
import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import de.crazypokemondev.pixelmongenerations.bingo.common.config.BingoCardHelper;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.BingoTask;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.CatchPokemonTask;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.Map;
import java.util.UUID;

public class CaptureListener {
    @SubscribeEvent
    public void onPokemonCaught(CaptureEvent.SuccessfulCaptureEvent event) throws ObjectMappingException {
        if (event.getPlayer().world.isRemote) return;
        PlayerConfigManager bingoCardManager = PixelmonBingoMod.bingoCardManager;
        EntityPixelmon pokemon = event.getPokemon();
        EntityPlayerMP player = event.getPlayer();
        UUID playerUuid = player.getUniqueID();
        @SuppressWarnings("UnstableApiUsage")
        Map<Integer, String> rawCard = bingoCardManager.getPlayerConfigNode(playerUuid, "Card")
                .getValue(new TypeToken<Map<Integer, String>>() {});
        Map<Integer, BingoTask> card = BingoCardHelper.deserializeTasks(rawCard);
        for (int i = 0; i < 25; i++) {
            if (card.get(i).isCompleted() || !(card.get(i) instanceof CatchPokemonTask)) continue;
            CatchPokemonTask task = (CatchPokemonTask) card.get(i);
            if (task.getSpecies() == pokemon.getSpecies()) {
                task.completeTask(player);
                rawCard.put(i, task.toString());
                bingoCardManager.getPlayerConfigNode(playerUuid, "Card").setValue(rawCard);
                bingoCardManager.savePlayer(playerUuid);
                BingoCardHelper.checkForBingo(i, player);
                break;
            }
        }
    }
}
