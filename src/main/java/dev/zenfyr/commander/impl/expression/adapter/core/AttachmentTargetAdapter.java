package dev.zenfyr.commander.impl.expression.adapter.core;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.adapter.context.PropertyAdapterContext;
import dev.zenfyr.commander.impl.Commander;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;

public interface AttachmentTargetAdapter {

  default EvaluationValue prop_data(PropertyAdapterContext<AttachmentTarget> ctx) {
    return Expression.convertValue(
        ctx.getObject().getAttachedOrCreate(Commander.DATA_ATTACHMENT), ctx.getConfig());
  }
}
