package de.crazypokemondev.pixelmongenerations.bingo.common.listeners;

import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class LoginListener {
    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PixelmonBingoMod.bingoCardManager.loadPlayer(event.player.getUniqueID());
    }
}
