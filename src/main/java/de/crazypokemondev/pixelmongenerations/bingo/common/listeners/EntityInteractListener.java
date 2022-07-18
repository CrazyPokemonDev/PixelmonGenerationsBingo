package de.crazypokemondev.pixelmongenerations.bingo.common.listeners;

import com.pixelmongenerations.common.entity.npcs.NPCShopkeeper;
import com.pixelmongenerations.common.entity.npcs.registry.BaseShopItem;
import com.pixelmongenerations.common.entity.npcs.registry.ShopItem;
import com.pixelmongenerations.common.entity.npcs.registry.ShopItemWithVariation;
import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import de.crazypokemondev.pixelmongenerations.bingo.common.config.PixelmonBingoConfig;
import de.crazypokemondev.pixelmongenerations.bingo.common.items.BingoCard;
import de.crazypokemondev.pixelmongenerations.bingo.common.items.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityInteractListener {
    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!PixelmonBingoConfig.BingoCard.soldByShopkeepers) return;
        if (event.getTarget() instanceof NPCShopkeeper) {
            NPCShopkeeper shopkeeper = (NPCShopkeeper) event.getTarget();
            if (shopkeeper.getItemList() == null) shopkeeper.loadItems();
            if (shopkeeper.getItemList().stream().anyMatch(item -> item.getItem().getItem() instanceof BingoCard))
                return;
            ItemStack stack = new ItemStack(ModItems.BINGO_CARD, 1);
            BaseShopItem base = new BaseShopItem(PixelmonBingoMod.MOD_ID + ":bingo_card", stack,
                    PixelmonBingoConfig.BingoCard.buyPrice, PixelmonBingoConfig.BingoCard.sellPrice);
            ShopItem shopItem = new ShopItem(base, 1, 100, false);
            ShopItemWithVariation shopItemWithVariation = new ShopItemWithVariation(shopItem, 1);
            shopkeeper.getItemList().add(shopItemWithVariation);
        }
    }
}
