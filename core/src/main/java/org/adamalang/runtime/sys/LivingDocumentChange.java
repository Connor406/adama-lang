/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys;

import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.json.PrivateView;

import java.util.List;

/**
 * A living document is changing; this represents the side-effects of that change for both the data
 * store (i.e. durability) and the clients (i. the people). This change is bundled such that these
 * two concerns can be ordered such that clients never see artifacts of uncommitted changes.
 */
public class LivingDocumentChange {

  /** the data change */
  public final RemoteDocumentUpdate update;
  public final Object response;
  private final List<Broadcast> broadcasts;

  /** wrap both the update and broadcasts into a nice package */
  public LivingDocumentChange(RemoteDocumentUpdate update, List<Broadcast> broadcasts, Object response) {
    this.update = update;
    this.broadcasts = broadcasts;
    this.response = response;
  }

  /** complete the update. This is to be called once the change is made durable */
  public void complete() {
    if (broadcasts != null) {
      for (Broadcast bc : broadcasts) {
        bc.complete();
      }
    }
  }

  /** a closure to combine who to send too */
  public static class Broadcast {
    private final PrivateView view;
    private final String data;

    public Broadcast(PrivateView view, String data) {
      this.view = view;
      this.data = data;
    }

    public void complete() {
      view.deliver(data);
    }
  }
}
