package com.sheet

import android.graphics.Color
import com.behavior.BottomSheetBehavior
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.PixelUtil.dpToPx
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.viewmanagers.SheetViewManagerDelegate
import com.facebook.react.viewmanagers.SheetViewManagerInterface

private val debugLog = LogFactory("SheetViewManager")

@ReactModule(name = SheetViewManager.NAME)
class SheetViewManager(reactContext: ReactApplicationContext) :
  ViewGroupManager<SheetView>(reactContext),
  SheetViewManagerInterface<SheetView> {
  private val mDelegate: ViewManagerDelegate<SheetView>

  init {
    mDelegate = SheetViewManagerDelegate(this)
  }

  override fun getDelegate(): ViewManagerDelegate<SheetView> {
    return mDelegate
  }

  override fun getName(): String {
    return NAME
  }

  public override fun createViewInstance(context: ThemedReactContext): SheetView {
    debugLog { "createViewInstance(...)" }
    return SheetView(context)
  }

  override fun dismissSheet(view: SheetView) {
    debugLog { "dismissSheet(view.id: ${view.id})" }
    view.dismiss()
  }

  override fun setUniqueId(view: SheetView, value: String?) {

  }

  override fun setDismissable(view: SheetView, value: Boolean) {
    debugLog { "setDismissable(view.id: ${view.id}, value: $value)" }
    view.dismissable = value
  }

  override fun setMaxWidth(view: SheetView, value: Double) {
    debugLog { "setMaxWidth(view.id: ${view.id}, value: $value)" }
    view.maxWidth = value.dpToPx()
  }

  override fun setMaxHeight(view: SheetView, value: Double) {
    debugLog { "setMaxHeight(view.id: ${view.id}, value: $value)" }
    view.mHostView.sheetMaxHeightSize = value.dpToPx()
  }

  override fun setMinHeight(view: SheetView, value: Double) {
    debugLog { "setMinHeight(view.id: ${view.id}, value: $value)" }
    view.mHostView.sheetMinHeightSize = value.dpToPx()
  }

  override fun setTopLeftRightCornerRadius(view: SheetView, value: Double) {
    debugLog { "setTopLeftRightCornerRadius(view.id: ${view.id}, value: $value)" }
    view.topLeftRightCornerRadius = value.dpToPx()
  }

  override fun setIsSystemUILight(view: SheetView, value: Boolean) {
    debugLog { "setIsSystemUILight(view.id: ${view.id}, value: $value)" }
    view.isSheetContentBackgroundLight = value
  }

  override fun setPassScrollViewReactTag(view: SheetView, value: String?) {
    debugLog { "setPassScrollViewReactTag(view.id: ${view.id}, value: $value)" }
    value ?: return
    val v = BottomSheetBehavior.findView(view) ?: return
    view.setNewNestedScrollView(v)
  }

  override fun setSheetBackgroundColor(view: SheetView, value: Int?) {
    debugLog { "setSheetBackgroundColor(view.id: ${view.id}, value: $value)" }
    view._backgroundColor = value ?: Color.TRANSPARENT
  }

  override fun setCalculatedHeight(view: SheetView, value: Double) {
    debugLog { "setCalculatedHeight(view.id: ${view.id}, value: $value)" }
    view.mHostView.setVirtualHeight(value.dpToPx())
  }

  override fun onAfterUpdateTransaction(view: SheetView) {
    debugLog { "onAfterUpdateTransaction(view.id: ${view.id})" }
    super.onAfterUpdateTransaction(view)
    view.showOrUpdate()
  }

  protected override fun addEventEmitters(
    reactContext: ThemedReactContext,
    view: SheetView
  ) {
    debugLog { "addEventEmitters(..., view.id: ${view.id})" }
    val dispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, view.id)
    if (dispatcher != null) {
      view.eventDispatcher = dispatcher
    }
  }

  companion object {
    const val NAME = "SheetView"
  }
}
