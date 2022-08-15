package de.crazypokemondev.pixelmongenerations.bingo.common.listeners;

import de.crazypokemondev.pixelmongenerations.bingo.common.config.BingoCardHelper;
import de.crazypokemondev.pixelmongenerations.bingo.common.tasks.CraftItemTask;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class CraftingListener {
    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) throws ObjectMappingException {
        if (event.player.world.isRemote) return;
        Item item = event.crafting.getItem();
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        Predicate<CraftItemTask> predicate = task -> task.getItem().equals(item);
        Consumer<CraftItemTask> handler = task -> task.completeTask(player);
        BingoCardHelper.handleFirstIncompleteTask(CraftItemTask.class, player, predicate, handler);
    }
}
