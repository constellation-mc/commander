package me.melontini.commander.test;

import static me.melontini.commander.test.ExpressionTest.*;

import lombok.extern.log4j.Log4j2;
import me.melontini.commander.api.event.EventType;
import me.melontini.commander.api.util.EventExecutors;
import me.melontini.commander.impl.Commander;
import me.melontini.dark_matter.api.data.codecs.ExtraCodecs;
import me.melontini.handytests.server.ServerTestContext;
import me.melontini.handytests.server.ServerTestEntrypoint;
import me.melontini.handytests.util.runner.HandyTest;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.ActionResult;
import org.assertj.core.api.Assertions;

@Log4j2
public class CustomEventsTest implements ModInitializer, ServerTestEntrypoint {

  public static final EventType CUSTOM = EventType.builder()
      .cancelTerm(ExtraCodecs.enumCodec(ActionResult.class))
      .build(Commander.id("test"));

  @HandyTest // custom_events_test.json
  void testCustomEventType(ServerTestContext context) {
    Assertions.assertThat(EventExecutors.runActionResult(
            CUSTOM, context.context().getOverworld(), () -> emptyContext(context)))
        .isEqualTo(ActionResult.FAIL); // Default is pass, the JSON cancels the event with fail.
  }

  @Override
  public void onInitialize() {
    // NOOP
  }
}
