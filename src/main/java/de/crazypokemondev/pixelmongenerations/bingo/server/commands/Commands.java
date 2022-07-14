package de.crazypokemondev.pixelmongenerations.bingo.server.commands;

import net.minecraft.command.CommandBase;

public class Commands {
    public static CommandBase[] getCommandList() {
        return new CommandBase[] {
                new Bingo(),
        };
    }
}
