package me.melontini.commander.impl.expression.library;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.Token;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import me.melontini.commander.api.expression.Expression;
import me.melontini.commander.api.expression.ExpressionLibrary;
import me.melontini.commander.impl.Commander;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import me.melontini.dark_matter.api.data.codecs.JsonCodecDataLoader;
import me.melontini.dark_matter.api.data.loading.ReloaderType;
import me.melontini.dark_matter.api.minecraft.util.TextUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public class ExpressionLibraryLoader extends JsonCodecDataLoader<ExpressionLibraryLoader.Shelf>
    implements ExpressionLibrary, DataAccessorIfc {

  public static final DynamicCommandExceptionType NO_EXPRESSION_EXCEPTION =
      new DynamicCommandExceptionType(
          object -> TextUtil.literal("No such expression in library %s!".formatted(object)));

  public static final ReloaderType<ExpressionLibraryLoader> RELOADER =
      ReloaderType.create(Commander.id("expression_library"));

  private Map<Identifier, Expression> library = new HashMap<>();

  public ExpressionLibraryLoader() {
    super(RELOADER.identifier(), Shelf.CODEC);
  }

  public Expression getExpression(Identifier id) {
    return this.library.get(id);
  }

  @Override
  public @UnmodifiableView Map<Identifier, Expression> allExpressions() {
    return Collections.unmodifiableMap(library);
  }

  @Override
  protected void apply(Map<Identifier, Shelf> parsed, ResourceManager manager) {
    Map<Identifier, Expression> base = new HashMap<>();
    Map<Identifier, Expression> replace = new HashMap<>();
    parsed.values().forEach(shelf -> {
      if (!shelf.replace()) {
        base.putAll(shelf.expressions());
      } else {
        replace.putAll(shelf.expressions());
      }
    });
    base.putAll(replace);
    this.library = base;
  }

  @Override
  public @Nullable EvaluationValue getData(String variable, Token token, EvaluationContext context)
      throws EvaluationException {
    var expression = ((com.ezylang.evalex.Expression) library.get(Identifier.of(variable)));
    if (expression == null) return null;
    return expression.evaluate(context);
  }

  @Override
  public String toString() {
    return "ExpressionLibrary()";
  }

  public record Shelf(boolean replace, Map<Identifier, Expression> expressions) {
    public static final Codec<Shelf> CODEC = RecordCodecBuilder.create(data -> data.group(
            ExtraCodecs.optional("replace", Codec.BOOL, false).forGetter(Shelf::replace),
            Codec.unboundedMap(Identifier.CODEC, Expression.CODEC)
                .fieldOf("expressions")
                .forGetter(Shelf::expressions))
        .apply(data, Shelf::new));
  }
}
