/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;

import java.util.List;

/** list assets for an object that are stored; this is for garbage collection of assets */
public interface ColdAssetSystem {

  /** list all the asset ids for a given key */
  void listAssetsOf(Key key, Callback<List<String>> callback);

  /** delete the asset for a given document */
  void deleteAsset(Key key, String assetId, Callback<Void> callback);
}
