package com.btk5h.skriptmirror.skript.custom.effect;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import com.btk5h.skriptmirror.Util;
import com.btk5h.skriptmirror.skript.custom.SyntaxParseEvent;
import org.bukkit.event.Event;

import java.util.Arrays;

public class CustomEffect extends Effect {
  private SyntaxInfo which;
  private Expression<?>[] exprs;
  private SkriptParser.ParseResult parseResult;

  @Override
  protected void execute(Event e) {
    // for effect commands
    invokeEffect(e);
  }

  @Override
  protected TriggerItem walk(Event e) {
    EffectTriggerEvent effectEvent = invokeEffect(e);

    if (effectEvent.isSync()) {
      return getNext();
    }

    return null;
  }

  private EffectTriggerEvent invokeEffect(Event e) {
    Trigger trigger = CustomEffectSection.effectHandlers.get(which);
    EffectTriggerEvent effectEvent = new EffectTriggerEvent(e, exprs, parseResult, which.getPattern(), getNext());
    if (trigger == null) {
      Skript.error(String.format("The custom effect '%s' no longer has a handler.", which));
    } else {
      trigger.execute(effectEvent);
    }
    return effectEvent;
  }

  @Override
  public String toString(Event e, boolean debug) {
    return which.getPattern();
  }

  @Override
  public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed,
                      SkriptParser.ParseResult parseResult) {
    which = CustomEffectSection.lookup(Util.getCurrentScript(), matchedPattern);

    if (which == null) {
      return false;
    }

    this.exprs = Arrays.stream(exprs)
        .map(Util::defendExpression)
        .toArray(Expression[]::new);
    this.parseResult = parseResult;

    if (!Util.canInitSafely(this.exprs)) {
      return false;
    }

    Trigger parseHandler = CustomEffectSection.parserHandlers.get(which);

    if (parseHandler != null) {
      SyntaxParseEvent event = new SyntaxParseEvent(this.exprs, parseResult, ScriptLoader.getCurrentEvents());
      parseHandler.execute(event);
      return event.isMarkedContinue();
    }

    return true;
  }
}
