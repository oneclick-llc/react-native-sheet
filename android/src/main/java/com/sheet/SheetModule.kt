package com.sheet

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap

private var debugLog = LogFactory("SheetModule")

class SheetModule(reactContext: ReactApplicationContext) : NativeSheetSpec(reactContext) {
  override fun getTypedExportedConstants(): Map<String, Any> {
    val res = getInitialWindowMetrics()
    debugLog { "getTypedExportedConstants() | res: $res" }
    return res
  }

  private fun getInitialWindowMetrics(): Map<String, Any> {
    debugLog { "getInitialWindowMetrics()" }
    val decorView =
      reactApplicationContext.currentActivity?.window?.decorView as ViewGroup? ?: return emptyMap()
    val insets = getSafeAreaInsets(decorView)
    return if (insets == null) {
      emptyMap()
    } else mapOf("insets" to edgeInsetsToJavaMap(insets))
  }

  override fun viewportSize(): WritableMap {
    return Arguments.createMap()
  }

  override fun dismissAll() {
    debugLog { "dismissAll()" }
    reactApplicationContext.runOnUiQueueThread {
      reactApplicationContext.currentActivity?.let { AppFittedSheet.dismissAll(it as AppCompatActivity) }
    }
  }

  override fun dismissPresented() {
    debugLog { "dismissPresented()" }
    reactApplicationContext.runOnUiQueueThread {
      reactApplicationContext.currentActivity?.let { AppFittedSheet.dismissPresented(it as AppCompatActivity) }
    }
  }
}
