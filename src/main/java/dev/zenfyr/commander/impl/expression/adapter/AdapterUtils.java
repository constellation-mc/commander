package dev.zenfyr.commander.impl.expression.adapter;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.parser.solvables.Solvable;
import net.minecraft.core.BlockPos;

public final class AdapterUtils {

  public static BlockPos blockPos(EvaluationContext context, Solvable... args)
      throws EvaluationException {
    return new BlockPos(
        args[0].solve(context).getNumberValue().intValue(),
        args[1].solve(context).getNumberValue().intValue(),
        args[2].solve(context).getNumberValue().intValue());
  }
}
