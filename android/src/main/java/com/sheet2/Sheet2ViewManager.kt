package com.sheet2

import android.graphics.Color
import com.behavior.BottomSheetBehavior
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.PixelUtil.dpToPx
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ReactStylesDiffMap
import com.facebook.react.uimanager.StateWrapper
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.viewmanagers.SheetViewManagerDelegate
import com.facebook.react.viewmanagers.SheetViewManagerInterface

private val debugLog = LogFactory("Sheet2ViewManager")

@ReactModule(name = Sheet2ViewManager.NAME)
class Sheet2ViewManager(reactContext: ReactApplicationContext) : ViewGroupManager<Sheet2View>(reactContext),
  SheetViewManagerInterface<Sheet2View> {
  private val mDelegate: ViewManagerDelegate<Sheet2View>

  init {
    mDelegate = SheetViewManagerDelegate(this)
  }

  override fun getDelegate(): ViewManagerDelegate<Sheet2View> {
    return mDelegate
  }

  override fun getName(): String {
    return NAME
  }

  public override fun createViewInstance(context: ThemedReactContext): Sheet2View {
    debugLog { "createViewInstance(...)" }
    return Sheet2View(context)
  }

  override fun dismissSheet(view: Sheet2View) {
    debugLog { "dismissSheet(view.id: ${view.id})" }
    view.dismiss()
  }

  override fun setUniqueId(view: Sheet2View, value: String?) {

  }

  override fun setDismissable(view: Sheet2View, value: Boolean) {
    debugLog { "setDismissable(view.id: ${view.id}, value: $value)" }
    view.dismissable = value
  }

  override fun setMaxWidth(view: Sheet2View, value: Double) {
    debugLog { "setMaxWidth(view.id: ${view.id}, value: $value)" }
    view.maxWidth = value.dpToPx()
  }

  override fun setMaxHeight(view: Sheet2View, value: Double) {
    debugLog { "setMaxHeight(view.id: ${view.id}, value: $value)" }
    view.mHostView.sheetMaxHeightSize = value.dpToPx()
  }

  override fun setMinHeight(view: Sheet2View, value: Double) {
    debugLog { "setMinHeight(view.id: ${view.id}, value: $value)" }
    view.mHostView.sheetMinHeightSize = value.dpToPx()
  }

  override fun setTopLeftRightCornerRadius(view: Sheet2View, value: Double) {
    debugLog { "setTopLeftRightCornerRadius(view.id: ${view.id}, value: $value)" }
    view.topLeftRightCornerRadius = value.dpToPx()
  }

  override fun setIsContentBackgroundLight(view: Sheet2View, value: Boolean) {
    debugLog { "setIsContentBackgroundLight(view.id: ${view.id}, value: $value)" }
    view.isContentBackgroundLight = value
  }

  override fun setPassScrollViewReactTag(view: Sheet2View, value: String?) {
    debugLog { "setPassScrollViewReactTag(view.id: ${view.id}, value: $value)" }
    value ?: return
    val v = BottomSheetBehavior.findView(view) ?: return
    view.setNewNestedScrollView(v)
  }

  override fun setSheetBackgroundColor(view: Sheet2View, value: Int?) {
    debugLog { "setSheetBackgroundColor(view.id: ${view.id}, value: $value)" }
    view._backgroundColor = value ?: Color.TRANSPARENT
  }

  override fun setWindowLevel(view: Sheet2View, value: String?) {
    // iOS only
  }

  override fun setUseInlinePresentation(view: Sheet2View, value: Boolean) {
    view.useInlinePresentation = value
  }

  override fun setCalculatedHeight(view: Sheet2View, value: Double) {
    debugLog { "setCalculatedHeight(view.id: ${view.id}, value: $value)" }
    view.mHostView.setVirtualHeight(value.dpToPx())
  }

  override fun onAfterUpdateTransaction(view: Sheet2View) {
    debugLog { "onAfterUpdateTransaction(view.id: ${view.id})" }
    super.onAfterUpdateTransaction(view)
    view.showOrUpdate()
  }

  override fun updateState(
    view: Sheet2View,
    props: ReactStylesDiffMap?,
    stateWrapper: StateWrapper?
  ): Any? {
    view.fabricStateWrapper = stateWrapper
    return super.updateState(view, props, stateWrapper)
  }

  protected override fun addEventEmitters(
    reactContext: ThemedReactContext,
    view: Sheet2View
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
