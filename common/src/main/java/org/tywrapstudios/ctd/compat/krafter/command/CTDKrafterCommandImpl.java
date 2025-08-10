package org.tywrapstudios.ctd.compat.krafter.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CTDKrafterCommandImpl {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var root = Commands
                .literal("ctd-krafter")
                .build();

        var link = Commands
                .literal("link")
                .build();

        var linkCodeArg = Commands
                .argument("code", IntegerArgumentType.integer(10000, 99999))
                .executes(CTDKrafterCommandExecution::link)
                .build();

        /* Root command */
        dispatcher.getRoot().addChild(root);
        /* Link command */
        root.addChild(link);
        link.addChild(linkCodeArg);
    }
}
