package com.btk5h.skriptmirror.skript;

import com.btk5h.skriptmirror.Util;

import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprTry extends SimpleExpression<Object> {
  static {
    Skript.registerExpression(ExprTry.class, Object.class, ExpressionType.COMBINED, "try %object%");
  }

  private Expression<Object> expr;

  @Override
  protected Object[] get(Event e) {
    return expr.getArray(e);
  }

  @Override
  public boolean isSingle() {
    return true;
  }

  @Override
  public Class<?> getReturnType() {
    return expr.getReturnType();
  }

  @Override
  public String toString(Event e, boolean debug) {
    return expr.toString(e, debug);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed,
                      SkriptParser.ParseResult parseResult) {
    expr = Util.defendExpression(exprs[0]);

    if (!Util.canInitSafely(expr)) {
      return false;
    }

    if (!(expr instanceof ExprJavaCall)) {
      Skript.error("Try may only be used with Java calls");
      return false;
    }

    ((ExprJavaCall) expr).setSuppressErrors(true);

    return true;
  }
}
