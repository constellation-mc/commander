package me.melontini.commander.impl.builtin.commands.action;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.melontini.commander.api.command.Command;
import me.melontini.commander.api.command.CommandType;
import me.melontini.commander.api.command.Selector;
import me.melontini.commander.api.event.EventContext;
import me.melontini.commander.api.expression.BrigadierMacro;
import me.melontini.commander.impl.Commander;
import me.melontini.commander.impl.builtin.BuiltInCommands;
import me.melontini.commander.impl.util.ServerHelper;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public record CommandCommand(Selector.Conditioned selector, Either<List<BrigadierMacro>, Identifier> commands) implements Command {

    public static final Codec<CommandCommand> CODEC = RecordCodecBuilder.create(data -> data.group(
            Selector.CODEC.fieldOf("selector").forGetter(CommandCommand::selector),
            ExtraCodecs.either(BrigadierMacro.CODEC.listOf(), Identifier.CODEC).fieldOf("commands").forGetter(CommandCommand::commands)
    ).apply(data, CommandCommand::new));

    @Override
    public boolean execute(EventContext context) {
        var opt = selector().select(context).map(ServerCommandSource::withSilent);
        if (opt.isEmpty()) return false;
        var server = context.lootContext().getWorld().getServer();

        if (commands().left().isPresent()) {
            for (BrigadierMacro command : commands().left().get()) {
                try {
                    server.getCommandManager().executeWithPrefix(opt.get(), command.build(context.lootContext()));
                } catch (Throwable e) {
                    ServerHelper.broadcastToOps(server, Text.literal(command.original()).append(Text.literal(" failed execution! Please check latest.log for more info!")).formatted(Formatting.RED));
                    Commander.LOGGER.error(e);
                    return false;
                }
            }
        }
        if (commands().right().isPresent()) {
            var func = server.getCommandFunctionManager().getFunction(commands().right().get());
            func.ifPresentOrElse(commandFunction -> server.getCommandFunctionManager().execute(commandFunction, opt.get()), () -> {
                throw new IllegalStateException("Unknown function %s!".formatted(commands().right().orElseThrow()));
            });
        }
        return true;
    }

    @Override
    public CommandType type() {
        return BuiltInCommands.COMMANDS;
    }
}
