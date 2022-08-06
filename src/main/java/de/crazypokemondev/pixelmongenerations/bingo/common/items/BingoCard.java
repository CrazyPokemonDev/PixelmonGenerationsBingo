package de.crazypokemondev.pixelmongenerations.bingo.common.items;

import com.google.common.reflect.TypeToken;
import com.lypaka.lypakautils.ConfigurationLoaders.PlayerConfigManager;
import com.lypaka.lypakautils.FancyText;
import de.crazypokemondev.pixelmongenerations.bingo.PixelmonBingoMod;
import de.crazypokemondev.pixelmongenerations.bingo.common.config.BingoCardHelper;
import de.crazypokemondev.pixelmongenerations.bingo.common.config.PixelmonBingoConfig;
import de.crazypokemondev.pixelmongenerations.bingo.network.BingoPacketHandler;
import de.crazypokemondev.pixelmongenerations.bingo.network.messages.OpenedBingoCardMessage;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BingoCard extends BaseItem {
    public BingoCard() {
        super("bingo_card");
        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip,
                               @NotNull ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.getBoolean("isMagic")) {
            tooltip.add(FancyText.getFormattedString(I18n.format("item.pixelmongenerationsbingo.bingo_card.magic")));
        }
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World worldIn, @NotNull EntityPlayer playerIn, @NotNull EnumHand handIn) {
        ItemStack heldItem = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            NBTTagCompound nbt = getOrSetTagCompound(heldItem);
            EntityPlayerMP player = (EntityPlayerMP) playerIn;
            if (nbt.getBoolean("isMagic")) {
                PlayerConfigManager bingoCardManager = PixelmonBingoMod.bingoCardManager;
                UUID uuid = player.getUniqueID();
                String dateTimeString = bingoCardManager.getPlayerConfigNode(uuid, "Expires").getString();
                Optional<LocalDateTime> expirationTime = getExpirationTime(dateTimeString);
                Map<Integer, String> card;
                if (expirationTime.isPresent() && expirationTime.get().isBefore(LocalDateTime.now())
                        || !bingoCardManager.getPlayerConfigNode(uuid, "Card").hasListChildren()) {
                    card = BingoCardHelper.generateNewBingoCard(worldIn);
                    bingoCardManager.getPlayerConfigNode(uuid, "Card").setValue(card);
                    expirationTime = setExpirationTimer(bingoCardManager, uuid);
                    bingoCardManager.savePlayer(uuid);
                } else {
                    try {
                        //noinspection UnstableApiUsage
                        card = bingoCardManager.getPlayerConfigNode(uuid, "Card")
                                .getValue(new TypeToken<Map<Integer, String>>() {
                                });
                        if (!expirationTime.isPresent() && BingoCardHelper.isCardCompleted(card)) {
                            card = BingoCardHelper.generateNewBingoCard(worldIn);
                            bingoCardManager.getPlayerConfigNode(uuid, "Card").setValue(card);
                            expirationTime = setExpirationTimer(bingoCardManager, uuid);
                            bingoCardManager.savePlayer(uuid);
                        }
                    } catch (ObjectMappingException e) {
                        PixelmonBingoMod.LOGGER.error("Failed to read bingo card from config file!");
                        return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
                    }
                }
                BingoPacketHandler.INSTANCE.sendTo(
                        new OpenedBingoCardMessage(card, expirationTime.orElse(null)), player);
            } else {
                NBTTagList card;
                // card isn't magic
                Optional<LocalDateTime> expirationTime = getExpirationTime(nbt);
                if (!nbt.hasKey("card") || nbt.getTagList("card", Constants.NBT.TAG_STRING).tagCount() != 25
                        || (expirationTime.isPresent() && expirationTime.get().isBefore(LocalDateTime.now()))) {
                    card = BingoCardHelper.generateNewBingoCardNbt(worldIn);
                    nbt.setTag("card", card);
                    setExpirationTimer(nbt);
                } else {
                    card = nbt.getTagList("card", Constants.NBT.TAG_STRING);
                }
                BingoPacketHandler.INSTANCE.sendTo(
                        new OpenedBingoCardMessage(card, expirationTime.orElse(null)), player);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
    }

    private void setExpirationTimer(NBTTagCompound nbt) {
        if (PixelmonBingoConfig.expirationTimer >= 0)
            nbt.setLong("expirationTimestamp", LocalDateTime.now()
                    .plusMinutes(PixelmonBingoConfig.expirationTimer).toEpochSecond(ZoneOffset.UTC));
        else
            nbt.removeTag("expirationTimestamp");
    }

    @NotNull
    private NBTTagCompound getOrSetTagCompound(ItemStack item) {
        if (item.getTagCompound() == null) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setBoolean("isMagic", PixelmonBingoConfig.BingoCard.useMagicCard);
            item.setTagCompound(nbt);
            return nbt;
        } else {
            return item.getTagCompound();
        }
    }

    @NotNull
    private Optional<LocalDateTime> setExpirationTimer(PlayerConfigManager bingoCardManager, UUID uuid) {
        Optional<LocalDateTime> expirationTime;
        if (PixelmonBingoConfig.expirationTimer < 0) {
            expirationTime = Optional.empty();
            bingoCardManager.getPlayerConfigNode(uuid, "Expires").setValue("");
        } else {
            expirationTime = Optional.of(
                    LocalDateTime.now().plusMinutes(PixelmonBingoConfig.expirationTimer));
            bingoCardManager.getPlayerConfigNode(uuid, "Expires").setValue(
                    expirationTime.get().toString());
        }
        return expirationTime;
    }

    private Optional<LocalDateTime> getExpirationTime(String dateTimeString) {
        if (dateTimeString.isEmpty()) return Optional.empty();
        else return Optional.of(LocalDateTime.parse(dateTimeString));
    }

    private Optional<LocalDateTime> getExpirationTime(NBTTagCompound nbt) {
        if (!nbt.hasKey("expirationTimestamp")) return Optional.empty();
        else return Optional.of(LocalDateTime.ofEpochSecond(
                nbt.getLong("expirationTimestamp"), 0, ZoneOffset.UTC));
    }
}
