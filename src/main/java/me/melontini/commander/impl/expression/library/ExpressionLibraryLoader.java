package me.melontini.commander.impl.expression.library;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.Token;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import me.melontini.commander.api.expression.Expression;
import me.melontini.commander.api.expression.ExpressionLibrary;
import me.melontini.commander.impl.Commander;
import me.melontini.dark_matter.api.data.codecs.JsonCodecDataLoader;
import me.melontini.dark_matter.api.data.loading.ReloaderType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ExpressionLibraryLoader extends JsonCodecDataLoader<ExpressionLibraryLoader.Shelf>
    implements ExpressionLibrary, DataAccessorIfc {

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
    var expression = ((com.ezylang.evalex.Expression) library.get(new Identifier(variable)));
    if (expression == null) return null;
    return expression.evaluate(context);
  }

  public record Shelf(boolean replace, Map<Identifier, Expression> expressions) {
    public static final Codec<Shelf> CODEC = RecordCodecBuilder.create(data -> data.group(
            Codec.BOOL.fieldOf("replace").forGetter(Shelf::replace),
            Codec.unboundedMap(Identifier.CODEC, Expression.CODEC)
                .fieldOf("expressions")
                .forGetter(Shelf::expressions))
        .apply(data, Shelf::new));
  }
}
