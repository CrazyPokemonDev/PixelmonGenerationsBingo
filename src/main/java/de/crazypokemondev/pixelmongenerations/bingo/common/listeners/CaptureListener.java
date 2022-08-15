package de.crazypokemondev.pixelmongenerations.bingo.common.listeners;

import com.pixelmongenerations.api.events.CaptureEvent;
import com.pixelmongenerations.common.entity.pixelmon.EntityPixelmon;
import de.crazypokemondev.pixelmongenerations.bingo.common.config.BingoCardHelper;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.CatchPokemonTask;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class CaptureListener {
    @SubscribeEvent
    public void onPokemonCaught(CaptureEvent.SuccessfulCaptureEvent event) throws ObjectMappingException {
        if (event.getPlayer().world.isRemote) return;
        EntityPixelmon pokemon = event.getPokemon();
        EntityPlayerMP player = event.getPlayer();
        Predicate<CatchPokemonTask> predicate = (CatchPokemonTask task) -> task.getSpecies() == pokemon.getSpecies();
        Consumer<CatchPokemonTask> handler = (CatchPokemonTask task) -> task.completeTask(player);
        BingoCardHelper.handleFirstIncompleteTask(CatchPokemonTask.class, player, predicate, handler);
    }
}
