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
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.traits.IsStructure;

import java.util.Map;

public class RuleSetStructures {
  // can type A project into type B, that is B is the new type, and A may have
  // more stuff but everything from B must be found in A
  public static boolean CanStructureAProjectIntoStructureB(final Environment environment, final TyType typeA, final TyType typeB, final boolean silent) {
    var result = false;
    if (typeA != null && typeB != null && typeA instanceof IsStructure && typeB instanceof IsStructure) {
      result = true;
      final var storA = ((IsStructure) typeA).storage();
      final var storB = ((IsStructure) typeB).storage();
      for (final Map.Entry<String, FieldDefinition> elementB : storB.fields.entrySet()) {
        final var other = storA.fields.get(elementB.getKey());
        if (other != null) {
          RuleSetAssignment.CanTypeAStoreTypeB(environment, elementB.getValue().type, other.type, StorageTweak.None, silent);
        } else {
          if (!silent) {
            environment.document.createError(typeA, String.format("The type '%s' contains field '%s' which is not found within '%s'.", typeB.getAdamaType(), elementB.getKey(), typeA.getAdamaType()));
          }
          result = false;
        }
      }
    }
    return result;
  }

  public static boolean IsStructure(final Environment environment, final TyType tyTypeOriginal, final boolean silent) {
    var tyType = tyTypeOriginal;
    if (tyType != null) {
      tyType = RuleSetCommon.Resolve(environment, tyType, silent);
      if (tyType != null && (tyType instanceof IsStructure)) {
        return true;
      } else if (!silent) {
        environment.document.createError(tyTypeOriginal, String.format("Type check failure: must have a type of 'record' or 'message', but got a type of '%s'.", tyTypeOriginal.getAdamaType()));
      }
    }
    return false;
  }
}
