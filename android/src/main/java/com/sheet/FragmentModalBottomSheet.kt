package com.sheet

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.lang.ref.WeakReference

private val debugLog = LogFactory("FragmentModalBottomSheet")

// Empty Constructor is required for Fragment Recreation,
// see https://oneclicklife.youtrack.cloud/issue/Looky-8019/Krash-iz-metriki-NoSuchMethodException-at-RNScreensFragmentFactory
class FragmentModalBottomSheet(
  private val modalView: ViewGroup? = null,
  private val dismissable: Boolean = true,
  private val isContentBackgroundLight: Boolean = true,
  private val onDismiss: (dismissAll: Boolean) -> Unit = {},
) : BottomSheetDialogFragment() {
  private fun log(message: String) = debugLog { "$message | id: $id" }

  init {
    log("init() | isModalViewDefined: ${modalView != null}, dismissable: $dismissable, isContentBackgroundLight: $isContentBackgroundLight, isOnDismissDefined: ${onDismiss != null}")
  }

  var dismissAll = false

  companion object {
    var presentedWindow: WeakReference<Window>? = null
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    log("onCreateView(...)")
    return modalView
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    log("onCreateDialog(...)")
    this.isCancelable = dismissable

    val dialog = object : CustomBottomSheetDialog(requireContext(), R.style.AppBottomSheetDialog) {
      override fun onAttachedToWindow() {
        log("CustomBottomSheetDialog.onAttachedToWindow()")
        super.onAttachedToWindow()

        window?.let {
          presentedWindow = WeakReference(it)

          WindowCompat.setDecorFitsSystemWindows(it, false)

          WindowInsetsControllerCompat(it, it.decorView).isAppearanceLightNavigationBars =
            isContentBackgroundLight

          it.navigationBarColor = Color.TRANSPARENT
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            it.isNavigationBarContrastEnforced = false
          }
        }

        findViewById<View>(com.google.android.material.R.id.container)?.apply {
          fitsSystemWindows = false
        }
      }

      override fun onDetachedFromWindow() {
        log("CustomBottomSheetDialog.onDetachedFromWindow()")

        super.onDetachedFromWindow()
      }
    }
    dialog.setSheetBackgroundColor(Color.TRANSPARENT)

    return dialog
  }

  fun setNewNestedScrollView(view: View) {
    log("setNewNestedScrollView(view.id: ${view.id})")
    (dialog as CustomBottomSheetDialog).setNewNestedScrollView(view)
  }

  fun collapse() {
    log("collapse()")
    (dialog as CustomBottomSheetDialog).collapse()
  }

  fun expand() {
    log("expand()")
    (dialog as CustomBottomSheetDialog).expand()
  }

  override fun onCancel(dialog: DialogInterface) {
    log("onCancel(dialog: $dialog)")
    super.onCancel(dialog)
  }

  override fun onDismiss(dialog: DialogInterface) {
    log("onDismiss(...)")
    super.onDismiss(dialog)
    presentedWindow?.clear()
    presentedWindow = null
    onDismiss(dismissAll)
  }
}
