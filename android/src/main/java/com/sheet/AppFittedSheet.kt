package com.sheet

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.view.ViewStructure
import android.view.accessibility.AccessibilityEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.UiThreadUtil
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.events.EventDispatcher

fun Fragment.safeShow(
  manager: FragmentManager,
  tag: String?
) {
  val ft = manager.beginTransaction()
  ft.add(this, tag)
  ft.commitNowAllowingStateLoss()
}

private val debugLog = LogFactory("AppFittedSheet")
private fun AppFittedSheet.log(message: String) = debugLog { "$message | id: $id" }

internal fun AppFittedSheet.onSheetDismiss() {
  log("onSheetDismiss()")
  val reactEventDispatcher =
    UIManagerHelper.getEventDispatcherForReactTag(context as ReactContext, id)
  val surfaceId = UIManagerHelper.getSurfaceId(context)
  reactEventDispatcher?.dispatchEvent(SheetDismissEvent(surfaceId, id))
}

private var presentedSheets: MutableList<String> = mutableListOf()

open class AppFittedSheet(context: Context) : ViewGroup(context), LifecycleEventListener {
  private var stacked = true
  private val fragmentTag = "CCBottomSheet-${System.currentTimeMillis()}"
  var mHostView = DialogRootViewGroup(context)


  var dismissable = true
  var topLeftRightCornerRadius: Float = 0F
  var _backgroundColor: Int = Color.TRANSPARENT
  var isSheetContentBackgroundLight = false

  var maxWidth: Float = 0F
    set(value) {
      log("maxWidth.set(value: $value) | prev: $field")
      field = value
      mHostView.sheetMaxWidthSize = value
      if (this.sheet == null) return
      mHostView.updateMaxWidth(mHostView.sheetMaxWidthSize)
    }

  var eventDispatcher: EventDispatcher?
    get() = mHostView.eventDispatcher
    set(eventDispatcher) {
      mHostView.eventDispatcher = eventDispatcher
    }

  private fun getCurrentActivity(): AppCompatActivity? {
    return (context as? ReactContext)?.currentActivity as? AppCompatActivity
  }

  private fun findSheet(name: String): FragmentModalBottomSheet? {
    return getCurrentActivity()?.supportFragmentManager?.findFragmentByTag(name) as? FragmentModalBottomSheet
  }

  private val sheet: FragmentModalBottomSheet?
    get() = findSheet(fragmentTag)

  override fun setId(id: Int) {
    log("setId($id)")
    super.setId(id)
    // Forward the ID to our content view, so event dispatching behaves correctly
    mHostView.id = id
  }

  fun showOrUpdate() {
    log("showOrUpdate()")
    UiThreadUtil.assertOnUiThread()

    val sheet = this.sheet
    mHostView.setCornerRadius(topLeftRightCornerRadius)
    mHostView.setBackgroundColor(_backgroundColor)
    if (sheet == null) {
      if (stacked) {
        presentedSheets.lastOrNull()?.let {
          findSheet(it)?.collapse()
        }
      }

      val fragment = FragmentModalBottomSheet(
        modalView = mHostView,
        dismissable = dismissable,
        isContentBackgroundLight = isSheetContentBackgroundLight
      ) { dismissAll ->
        val logTag = "FragmentModalBottomSheet.onDismiss(dismissAll: $dismissAll)"
        log(logTag)
        val parent = mHostView.parent as? ViewGroup
        parent?.removeViewAt(0)
        onSheetDismiss()
        if (dismissAll) {
          log("$logTag | dismissingSilently | presentedSheets.size: ${presentedSheets.size}")
          if (stacked) {
            var lastName = presentedSheets.removeLastOrNull()
            if (lastName == fragmentTag) lastName = presentedSheets.lastOrNull()
            if (lastName != null) {
              findSheet(lastName)?.let {
                it.dismissAll = true
                it.dismissAllowingStateLoss()
              }
            }
          }
          return@FragmentModalBottomSheet
        }
        if (stacked) {
          var lastName = presentedSheets.removeLastOrNull()
          if (lastName == fragmentTag) {
            lastName = presentedSheets.lastOrNull()
          }
          lastName?.let { findSheet(it)?.expand() }
          log("$logTag | dismiss | presentedSheets.size: ${presentedSheets.size}")
        }
      }

      getCurrentActivity()?.supportFragmentManager?.let {
        fragment.safeShow(it, fragmentTag)
        if (stacked) {
          log("show | presentedSheets.size: ${presentedSheets.size}, fragmentTag: $fragmentTag")
          if (presentedSheets.contains(fragmentTag)) return
          presentedSheets.add(fragmentTag)
        }
      }
    }
  }

  fun setNewNestedScrollView(view: View) {
    log("setNewNestedScrollView(view.id: ${view.id})")
    sheet?.setNewNestedScrollView(view)
  }

  override fun dispatchProvideStructure(structure: ViewStructure) {
    mHostView.dispatchProvideStructure(structure)
  }

  override fun addView(child: View, index: Int) {
    log("addView(child.id: ${child.id}, index: $index)")
    UiThreadUtil.assertOnUiThread()
    mHostView.addView(child, index)
//    val ctx = context as ReactContext? ?: return
//    val module = UIManagerHelper.getUIManager(ctx, UIManagerType.DEFAULT) as? UIManagerModule ?: return
//    ctx.runOnNativeModulesQueueThread {
//      val resolveShadowNode = module?.uiImplementation?.resolveShadowNode(child.id) ?: return@runOnNativeModulesQueueThread
//      val height = resolveShadowNode.layoutHeight
//      if (height > 0) mHostView.setVirtualHeight(height)
//    }
  }

  override fun getChildCount(): Int = mHostView.childCount

  override fun getChildAt(index: Int): View? = mHostView.getChildAt(index)

  override fun removeView(child: View) {
    log("removeView(childId: ${child.id})")
    UiThreadUtil.assertOnUiThread()
    dismiss()
  }

  override fun onDetachedFromWindow() {
    log("onDetachedFromWindow()")
    super.onDetachedFromWindow()
  }

  override fun removeViewAt(index: Int) {
    log("removeViewAt(index: $index) | viewToRemoveId: ${mHostView.getChildAt(index).id}")
    UiThreadUtil.assertOnUiThread()
    dismiss()
  }

  private fun onDropInstance() {
    log("onDropInstance()")
    (context as ReactContext).removeLifecycleEventListener(this)
    dismiss()
  }

  fun dismiss() {
    log("dismiss()")
    UiThreadUtil.assertOnUiThread()
    this.sheet?.dismissAllowingStateLoss()
  }

  override fun addChildrenForAccessibility(outChildren: ArrayList<View?>?) {}

  override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent?) = false
  override fun onHostResume() {
    log("onHostResume()")
    showOrUpdate()
  }

  override fun onHostPause() {
    log("onHostPause()")
  }

  override fun onHostDestroy() {
    log("onHostDestroy")
    onDropInstance()
  }

  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    log("onLayout(changed: $changed, l: $l, t: $t, r: $r, b: $b)")
  }

  companion object {
    fun dismissAll(activity: AppCompatActivity) {
      debugLog { "Companion.dismissAll(...)" }
      val fragment = activity.supportFragmentManager.fragments.lastOrNull()
      if (fragment is FragmentModalBottomSheet) {
        fragment.dismissAll = true
        fragment.dismissAllowingStateLoss()
      }
    }

    fun dismissPresented(activity: AppCompatActivity) {
      debugLog { "Companion.dismissPresented(...)" }
      val fragment = activity.supportFragmentManager.fragments.lastOrNull()
      if (fragment is FragmentModalBottomSheet) {
        fragment.dismissAllowingStateLoss()
      }
    }
  }
}
