package me.melontini.commander.impl.expression.extensions.convert.attributes;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.types.NumberValue;
import com.ezylang.evalex.parser.Token;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import me.melontini.commander.impl.expression.extensions.convert.RegistryAccessStruct;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = false)
public final class EntityAttributesStruct implements DataAccessorIfc {

  private final AttributeContainer container;
  private final RegistryAccessStruct registryAccess =
      RegistryAccessStruct.forRegistry(Registries.ATTRIBUTE);

  public EntityAttributesStruct(AttributeContainer container) {
    this.container = container;
  }

  @Override
  public @Nullable EvaluationValue getData(String variable, Token token, EvaluationContext context)
      throws EvaluationException {
    var attr = registryAccess.getCache().apply(variable);
    if (attr == null) return null;
    return NumberValue.of(BigDecimal.valueOf(container.getValue((EntityAttribute) attr)));
  }

  @Override
  public String toString() {
    return String.valueOf(container.toNbt());
  }
}
