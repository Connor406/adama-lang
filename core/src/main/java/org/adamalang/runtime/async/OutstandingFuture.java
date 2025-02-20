/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.async;

import org.adamalang.runtime.natives.NtPrincipal;

/**
 * This represents a future which has been vended to the runtime.
 *
 * <p>A future which has been vended, and it is assigned a unique id. Given how the async element
 * works within Adama, it is vital that order of futures vended be stable.
 */
public class OutstandingFuture {
  public final String channel;
  public final int id;
  public final NtPrincipal who;
  public String json;
  private boolean claimed;
  private boolean taken;

  /**
   * @param id the unique id of the future (for client's reference)
   * @param channel the channel for the future to wait on
   * @param who the client we are waiting on
   */
  public OutstandingFuture(final int id, final String channel, final NtPrincipal who) {
    this.id = id;
    this.channel = channel;
    this.who = who;
    claimed = true; // creation is an act of claiming
    taken = false;
  }

  /** has this future been claimed and not taken */
  public boolean outstanding() {
    return claimed && !taken;
  }

  /** release the claim and free it up */
  public void reset() {
    claimed = false;
    taken = false;
  }

  /** take the future */
  public void take() {
    taken = true;
  }

  /** does this future match the given channel and person; that is, can this future pair up */
  public boolean test(final String testChannel, final NtPrincipal testClientId) {
    if (channel.equals(testChannel) && who.equals(testClientId) && !claimed) {
      claimed = true;
      return true;
    }
    return false;
  }
}
