package com.sheet

import android.util.Log

internal const val LOG_TAG = ".Sheet"
private const val DEBUG = true

@Suppress("FunctionName")
internal fun LogFactory(
  tag: String,
  messagePrefixGetter: () -> String = { "" },
  isEnabled: Boolean = true,
): (messageGetter: () -> String) -> Unit {
  return fun(messageGetter: () -> String) {
    if (!DEBUG || !isEnabled) return
    // long Tags cause Formatting Problems in Studio's Logcat
    Log.d(LOG_TAG, "$tag.${messagePrefixGetter()}${messageGetter()}")
  }
}
