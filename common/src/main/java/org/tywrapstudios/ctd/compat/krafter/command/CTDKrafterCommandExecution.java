package org.tywrapstudios.ctd.compat.krafter.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.tywrapstudios.krafter.extensions.minecraft.MinecraftExtensionKt;

import java.util.function.Supplier;

public class CTDKrafterCommandExecution {
    public static int link(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int code = IntegerArgumentType.getInteger(context, "code");
        var result = 0;

        try {
            result = MinecraftExtensionKt.verify(context.getSource().getPlayer().getUUID(), code).get();
            if (result == 1) {
                context.getSource().sendSuccess(() -> Component.literal("Successfully linked your account!").withStyle(ChatFormatting.GREEN), false);
            } else {
                context.getSource().sendFailure(Component.literal("Failed to link your account. Please check the code and try again.").withStyle(ChatFormatting.RED));
            }
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Failed to link: " + e.getMessage()).withStyle(ChatFormatting.RED));
        }
        return result;
    }
}
