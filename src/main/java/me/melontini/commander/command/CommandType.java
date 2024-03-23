package me.melontini.commander.command;

import com.mojang.serialization.Codec;

public record CommandType(Codec<? extends Command> codec) {

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }
}
