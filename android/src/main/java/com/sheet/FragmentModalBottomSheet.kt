package com.sheet

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FragmentModalBottomSheet(
  private val modalView: DialogRootViewGroup,
  private val dismissable: Boolean,
  private val handleRadius: Float,
  private val sheetBackgroundColor: Int,
  private val isDark: Boolean,
  private val isStatusBarBgLight: Boolean,
  private val onDismiss: () -> Unit,
) : BottomSheetDialogFragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View = modalView

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    this.isCancelable = dismissable

    val dialog = CustomBottomSheetDialog(requireContext(), if (isDark) R.style.AppBottomSheetDialog_Dark else R.style.AppBottomSheetDialog)
    dialog.window.let {
      if (it != null) {
        it.navigationBarColor = sheetBackgroundColor

        if (isStatusBarBgLight) {
          it.decorView.systemUiVisibility =
            it.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
          it.decorView.systemUiVisibility =
            it.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
      }
    }

    dialog.setCornerRadius(handleRadius)
    dialog.setSheetBackgroundColor(sheetBackgroundColor)
    return dialog
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    onDismiss()
  }
}
