package de.crazypokemondev.pixelmongenerations.bingo.common.items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class ModItems {
    public static BingoCard BINGO_CARD = new BingoCard();

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                BINGO_CARD
        );
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        BINGO_CARD.initModel();
    }
}
