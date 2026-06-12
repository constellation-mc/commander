package dev.zenfyr.commander.impl.expression.library;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.Token;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.zenfyr.commander.api.expression.Expression;
import dev.zenfyr.commander.api.expression.ExpressionLibrary;
import dev.zenfyr.commander.impl.Commander;
import dev.zenfyr.pulsar.codec.JsonCodecDataLoader;
import dev.zenfyr.pulsar.resources.ReloaderType;
import dev.zenfyr.pulsar.util.TextUtil;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public class ExpressionLibraryLoader extends JsonCodecDataLoader<ExpressionLibraryLoader.Shelf>
    implements ExpressionLibrary, DataAccessorIfc {

  public static final DynamicCommandExceptionType NO_EXPRESSION_EXCEPTION =
      new DynamicCommandExceptionType(
          object -> TextUtil.literal("No such expression in library %s!".formatted(object)));

  public static final ReloaderType<ExpressionLibraryLoader> RELOADER =
      ReloaderType.create(Commander.id("expression_library"));

  private Map<ResourceLocation, Expression> library = new Object2ReferenceOpenHashMap<>();
  private Map<String, Supplier<Expression>> cache = new Object2ReferenceOpenHashMap<>();

  public ExpressionLibraryLoader() {
    super(RELOADER.location(), Shelf.CODEC);
  }

  public Expression getExpression(ResourceLocation id) {
    return this.library.get(id);
  }

  @Override
  public @Nullable Expression getExpression(String id) {
    var cache = this.cache.get(id);
    if (cache != null) return cache.get();

    if (!ResourceLocation.isValidResourceLocation(id)) {
      this.cache.put(id, (cache = () -> null));
      return cache.get();
    }

    var location = ResourceLocation.tryParse(id);
    this.cache.put(id, (cache = () -> this.library.get(location)));
    return cache.get();
  }

  @Override
  public @UnmodifiableView Map<ResourceLocation, Expression> allExpressions() {
    return Collections.unmodifiableMap(library);
  }

  @Override
  protected void apply(Map<ResourceLocation, Shelf> parsed, ResourceManager manager) {
    this.cache = new Object2ReferenceOpenHashMap<>();
    Map<ResourceLocation, Expression> base = new Object2ReferenceOpenHashMap<>();
    parsed.values().forEach(shelf -> base.putAll(shelf.expressions()));
    this.library = base;
  }

  @Override
  public @Nullable EvaluationValue getData(String variable, Token token, EvaluationContext context)
      throws EvaluationException {
    var expression = ((com.ezylang.evalex.Expression) library.get(new ResourceLocation(variable)));
    if (expression == null) return null;
    return expression.solve(context);
  }

  @Override
  public String toString() {
    return "ExpressionLibrary()";
  }

  public record Shelf(Map<ResourceLocation, Expression> expressions) {
    public static final Codec<Shelf> CODEC = RecordCodecBuilder.create(
        data -> data.group(Codec.unboundedMap(ResourceLocation.CODEC, Expression.CODEC)
                .fieldOf("expressions")
                .forGetter(Shelf::expressions))
            .apply(data, Shelf::new));
  }
}
