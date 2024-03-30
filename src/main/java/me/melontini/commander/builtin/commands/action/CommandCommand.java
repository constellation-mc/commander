package me.melontini.commander.builtin.commands.action;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.melontini.commander.builtin.BuiltInCommands;
import me.melontini.commander.command.Command;
import me.melontini.commander.command.CommandType;
import me.melontini.commander.command.selector.ConditionedSelector;
import me.melontini.commander.event.EventContext;
import me.melontini.commander.util.macro.BrigadierMacro;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.List;

public record CommandCommand(ConditionedSelector selector, Either<List<BrigadierMacro>, Identifier> commands) implements Command {

    public static final Codec<CommandCommand> CODEC = RecordCodecBuilder.create(data -> data.group(
            ConditionedSelector.CODEC.fieldOf("selector").forGetter(CommandCommand::selector),
            ExtraCodecs.either(BrigadierMacro.CODEC.listOf(), Identifier.CODEC).fieldOf("commands").forGetter(CommandCommand::commands)
    ).apply(data, CommandCommand::new));

    @Override
    public boolean execute(EventContext context) {
        var opt = selector().select(context).map(ServerCommandSource::withSilent);
        if (opt.isEmpty()) return false;
        var server = context.lootContext().getWorld().getServer();

        if (commands().left().isPresent()) {
            for (BrigadierMacro command : commands().left().get()) {
                server.getCommandManager().executeWithPrefix(opt.get(), command.build(context));
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
