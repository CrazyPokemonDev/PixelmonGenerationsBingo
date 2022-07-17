package de.crazypokemondev.pixelmongenerations.bingo.common.items;

import com.google.common.reflect.TypeToken;
import com.lypaka.lypakautils.ConfigurationLoaders.PlayerConfigManager;
import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import de.crazypokemondev.pixelmongenerations.bingo.common.config.BingoCardHelper;
import de.crazypokemondev.pixelmongenerations.bingo.common.config.PixelmonBingoConfig;
import de.crazypokemondev.pixelmongenerations.bingo.network.BingoPacketHandler;
import de.crazypokemondev.pixelmongenerations.bingo.network.messages.OpenedBingoCardMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BingoCard extends BaseItem {
    public BingoCard() {
        super("bingo_card");
        setMaxStackSize(1);
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World worldIn, @NotNull EntityPlayer playerIn, @NotNull EnumHand handIn) {
        if (!worldIn.isRemote) {
            EntityPlayerMP player = (EntityPlayerMP) playerIn;
            PlayerConfigManager bingoCardManager = PixelmonBingoMod.bingoCardManager;
            UUID uuid = player.getUniqueID();
            bingoCardManager.loadPlayer(uuid);
            String dateTimeString = bingoCardManager.getPlayerConfigNode(uuid, "Expires").getString();
            Optional<LocalDateTime> expirationTime = getExpirationTime(dateTimeString);
            Map<Integer, String> card;
            if (expirationTime.isPresent() && expirationTime.get().isBefore(LocalDateTime.now())) {
                card = BingoCardHelper.generateNewBingoCard();
                bingoCardManager.getPlayerConfigNode(uuid, "Card").setValue(card);
                if (PixelmonBingoConfig.expirationTimer < 0) {
                    expirationTime = Optional.empty();
                    bingoCardManager.getPlayerConfigNode(uuid, "Expires").setValue("");
                } else {
                    expirationTime = Optional.of(
                            LocalDateTime.now().plusMinutes(PixelmonBingoConfig.expirationTimer));
                    bingoCardManager.getPlayerConfigNode(uuid, "Expires").setValue(
                            expirationTime.get().toString());
                }
                bingoCardManager.savePlayer(uuid);
            }
            else {
                try {
                    //noinspection UnstableApiUsage
                    card = bingoCardManager.getPlayerConfigNode(uuid, "Card")
                            .getValue(new TypeToken<Map<Integer, String>>() {});
                } catch (ObjectMappingException e) {
                    PixelmonBingoMod.LOGGER.error("Failed to read bingo card from config file!");
                    return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
                }
            }
            BingoPacketHandler.INSTANCE.sendTo(
                    new OpenedBingoCardMessage(card, expirationTime.orElse(null)), player);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    private Optional<LocalDateTime> getExpirationTime(String dateTimeString) {
        if (dateTimeString.isEmpty()) return Optional.empty();
        else return Optional.of(LocalDateTime.parse(dateTimeString));
    }
}
