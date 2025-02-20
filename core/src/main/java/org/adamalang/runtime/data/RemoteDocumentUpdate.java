/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data;

import org.adamalang.runtime.json.JsonAlgebra;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;

import java.util.ArrayList;

/** the remote copy should change */
public class RemoteDocumentUpdate {
  /** the request that is changing the document */
  public final String request;

  /** the request as as redo patch */
  public final String redo;

  /** the undo patch to revert this change */
  public final String undo;

  /** the beginning sequencer of this change */
  public final int seqBegin;

  /** the end sequencer of this change */
  public final int seqEnd;


  /** who was responsible for the update */
  public final NtPrincipal who;

  /**
   * this update is incomplete with respect to time, and this will ensure we schedule an
   * invalidation in the future
   */
  public final boolean requiresFutureInvalidation;

  /**
   * if requiresFutureInvalidation, then how many milliseconds should the system wait to invoke
   * invalidation
   */
  public final int whenToInvalidateMilliseconds;

  /**
   * how many bytes were written with this update
   */
  public final long assetBytes;

  /** what is the type of the update */
  public final UpdateType updateType;

  public RemoteDocumentUpdate(final int seqBegin, final int seqEnd, NtPrincipal who, final String request, final String redo, final String undo, final boolean requiresFutureInvalidation, int whenToInvalidateMilliseconds, long assetBytes, UpdateType updateType) {
    this.seqBegin = seqBegin;
    this.seqEnd = seqEnd;
    this.who = who;
    this.request = request;
    this.redo = redo;
    this.undo = undo;
    this.requiresFutureInvalidation = requiresFutureInvalidation;
    this.whenToInvalidateMilliseconds = whenToInvalidateMilliseconds;
    this.assetBytes = assetBytes;
    this.updateType = updateType;
  }

  public static RemoteDocumentUpdate[] compact(RemoteDocumentUpdate[] updates) {
    if (updates.length == 1) {
      return updates;
    }
    ArrayList<RemoteDocumentUpdate> newUpdates = new ArrayList<>(updates.length);
    int k = 0;
    while (k < updates.length) {
      RemoteDocumentUpdate consider = updates[k];
      while (k + 1 < updates.length && updates[k + 1].updateType == UpdateType.Invalidate) {
        consider = merge(consider, updates[k + 1]);
        k++;
      }
      newUpdates.add(consider);
      k++;
    }
    return newUpdates.toArray(new RemoteDocumentUpdate[newUpdates.size()]);
  }

  private static RemoteDocumentUpdate merge(RemoteDocumentUpdate a, RemoteDocumentUpdate b) {
    return new RemoteDocumentUpdate(a.seqBegin, b.seqEnd, //
        a.who, a.request, //
        mergeJson(a.redo, b.redo), //
        mergeJson(b.undo, a.undo), b.requiresFutureInvalidation, b.whenToInvalidateMilliseconds, //
        a.assetBytes + b.assetBytes, a.updateType);
  }

  private static String mergeJson(String a, String b) {
    JsonStreamReader readerA = new JsonStreamReader(a);
    JsonStreamReader readerB = new JsonStreamReader(b);
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.writeTree(JsonAlgebra.merge(readerA.readJavaTree(), readerB.readJavaTree(), true));
    return writer.toString();
  }
}
