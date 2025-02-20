/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.events;

import org.adamalang.caravan.contracts.KeyToIdService;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.data.Key;

import java.util.concurrent.ConcurrentHashMap;

/** since the finder service is the canonical way to convert keys into ID, we wrap it here and then cache it for performance reasons */
public class FinderServiceToKeyToIdService implements KeyToIdService {
  private final FinderService finder;
  private final ConcurrentHashMap<Key, Long> cache;

  public FinderServiceToKeyToIdService(FinderService finder) {
    this.finder = finder;
    this.cache = new ConcurrentHashMap();
  }

  @Override
  public void translate(Key key, Callback<Long> callback) {
    Long result = cache.get(key);
    if (result != null) {
      callback.success(result);
      return;
    }
    finder.find(key, new Callback<FinderService.Result>() {
      @Override
      public void success(FinderService.Result value) {
        cache.put(key, value.id);
        callback.success(value.id);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void forget(Key key) {
    cache.remove(key);
  }
}
