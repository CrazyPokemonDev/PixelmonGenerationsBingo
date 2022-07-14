package de.crazypokemondev.pixelmongenerations.bingo.common.items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModItems {
    public static Item BINGO_CARD = new BingoCard();

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                BINGO_CARD
        );
    }
}
