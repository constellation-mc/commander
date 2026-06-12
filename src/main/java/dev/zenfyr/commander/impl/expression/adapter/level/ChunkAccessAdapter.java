package dev.zenfyr.commander.impl.expression.adapter.level;

import dev.zenfyr.commander.impl.expression.adapter.ReflectiveTypeAdapter;
import dev.zenfyr.commander.impl.expression.adapter.core.AttachmentTargetAdapter;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ChunkAccessAdapter extends ReflectiveTypeAdapter<ChunkAccess>
    implements AttachmentTargetAdapter, BlockGetterAdapter {}
