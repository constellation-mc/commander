package me.melontini.commander.impl.expression.extensions.convert.attributes;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.types.NumberValue;
import com.ezylang.evalex.parser.Token;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = false)
public final class EntityAttributesStruct implements DataAccessorIfc {

  private final AttributeContainer container;

  public EntityAttributesStruct(AttributeContainer container) {
    this.container = container;
  }

  @Override
  public @Nullable EvaluationValue getData(String variable, Token token, EvaluationContext context)
      throws EvaluationException {
    var attr = Registries.ATTRIBUTE.getEntry(Identifier.of(variable));
    if (attr.isEmpty()) return null;
    return attr.filter(container::hasAttribute)
        .map(container::getValue)
        .map(BigDecimal::valueOf)
        .map(NumberValue::of)
        .orElse(null);
  }

  @Override
  public String toString() {
    return String.valueOf(container.toNbt());
  }
}
