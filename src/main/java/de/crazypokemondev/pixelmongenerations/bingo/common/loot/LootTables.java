package de.crazypokemondev.pixelmongenerations.bingo.common.loot;

import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

public class LootTables {
    public static final ResourceLocation singleTaskLootTable =
            new ResourceLocation(PixelmonBingoMod.MOD_ID, "task_rewards");
    public static final ResourceLocation bingoLootTable =
            new ResourceLocation(PixelmonBingoMod.MOD_ID, "bingo_rewards");
    public static final ResourceLocation bingoCardLootTable =
            new ResourceLocation(PixelmonBingoMod.MOD_ID, "bingo_card_rewards");

    public static void registerAll() {
        LootTableList.register(singleTaskLootTable);
        LootTableList.register(bingoLootTable);
        LootTableList.register(bingoCardLootTable);
    }
}
