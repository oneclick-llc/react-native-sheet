package com.modal

import android.content.Context
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.appcompat.app.AppCompatActivity
import com.BaseRNView
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.UiThreadUtil
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.facebook.react.views.view.ReactViewGroup

fun TopModalView.onModalDismiss() {
  (context as ReactContext).getJSModule(RCTEventEmitter::class.java)
    .receiveEvent(id, "onModalDismiss", Arguments.createMap())
}

class TopModalView(context: Context?) : ReactViewGroup(context) {
  private var dialog: FullScreenDialog? = null
  private var rnView = BaseRNView(context)
  var animated = true

  init {
    println("⚽️ init")
  }

  private fun getCurrentActivity(): AppCompatActivity {
    return (context as ReactContext).currentActivity as AppCompatActivity
  }

  override fun addView(child: View, index: Int) {
    rnView.addView(child)
    dialog = FullScreenDialog(rnView, animated, ::onModalDismiss)
    dialog?.safeShow(getCurrentActivity().supportFragmentManager, "TopModalView")
    dialog?.isCancelable = true

  }

  override fun addChildrenForAccessibility(outChildren: ArrayList<View?>?) {}
  override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent?) = false


  override fun removeView(child: View?) {
    println("⚽️ removeView")
    UiThreadUtil.assertOnUiThread()
    dismiss()
  }

  override fun removeViewAt(index: Int) {
    println("⚽️ removeViewAt: $index")
    UiThreadUtil.assertOnUiThread()
    dismiss()
  }

  fun dismiss() {
    dialog?.let {
      println("⚽️ dismiss")
      dialog?.dismissAllowingStateLoss()
      dialog = null
    }
  }
}
