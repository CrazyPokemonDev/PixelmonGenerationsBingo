package de.crazypokemondev.pixelmongenerations.bingo.server.commands;

import de.crazypokemondev.pixelmongenerations.bingo.common.items.ModItems;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public class Bingo extends CommandBase {
    @Override
    public @NotNull String getName() {
        return "bingo";
    }

    @Override
    public @NotNull String getUsage(@NotNull ICommandSender sender) {
        return "commands.pixelmongenerationsbingo.bingo.usage";
    }

    @Override
    public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, String @NotNull [] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            ItemStack bingoCard = new ItemStack(ModItems.BINGO_CARD, 1);
            player.inventory.addItemStackToInventory(bingoCard);
        }
    }
}
