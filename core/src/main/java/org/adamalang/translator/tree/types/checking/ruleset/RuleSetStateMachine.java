/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.definitions.DefineStateTransition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeStateMachineRef;
import org.adamalang.translator.tree.types.reactive.TyReactiveStateMachineRef;

public class RuleSetStateMachine {
  public static DefineStateTransition FindStateMachineStep(final Environment environment, final String name, final DocumentPosition position, final boolean silent) {
    final var defineStateTransition = environment.document.transitions.get(name);
    if (defineStateTransition != null) {
      return defineStateTransition;
    } else if (!silent) {
      environment.document.createError(position, String.format("State machine transition not found: a state machine label '%s' was not found.", name));
    }
    return null;
  }

  public static boolean IsStateMachineRef(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    final var tyType = RuleSetCommon.Resolve(environment, tyTypeOriginal, silent);
    if (tyType != null) {
      if (tyType instanceof TyNativeStateMachineRef || tyType instanceof TyReactiveStateMachineRef) {
        return true;
      }
      RuleSetCommon.SignalTypeFailure(environment, new TyNativeStateMachineRef(TypeBehavior.ReadOnlyNativeValue, null, null), tyTypeOriginal, silent);
    }
    return false;
  }
}
