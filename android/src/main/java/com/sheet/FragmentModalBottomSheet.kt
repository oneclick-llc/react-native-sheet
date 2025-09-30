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

class FragmentModalBottomSheet(
  private val modalView: ViewGroup,
  private val dismissable: Boolean,
  private val isContentBackgroundLight: Boolean = true,
  private val onDismiss: (dismissAll: Boolean) -> Unit
) : BottomSheetDialogFragment() {

  var dismissAll = false

  companion object {
    var presentedWindow: WeakReference<Window>? = null
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View = modalView

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    this.isCancelable = dismissable

    val dialog = object : CustomBottomSheetDialog(requireContext(), R.style.AppBottomSheetDialog) {
      override fun onAttachedToWindow() {
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
    }
    dialog.setSheetBackgroundColor(Color.TRANSPARENT)

    return dialog
  }

  fun setNewNestedScrollView(view: View) {
    (dialog as CustomBottomSheetDialog).setNewNestedScrollView(view)
  }

  fun collapse() {
    (dialog as CustomBottomSheetDialog).collapse()
  }

  fun expand() {
    (dialog as CustomBottomSheetDialog).expand()
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    presentedWindow?.clear()
    presentedWindow = null
    onDismiss(dismissAll)
  }
}
