package com.example.notesapp.util

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.notesapp.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import java.util.Calendar
import java.util.Date


fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.disable() {
    isEnabled = false
}

fun View.enabled() {
    isEnabled = true
}

fun Fragment.toast(msg: String?) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
}

fun ChipGroup.addChip(
    text: String,
    isTouchTargeSize: Boolean = false,
    closeIconListener: View.OnClickListener? = null
) {
    val chip: Chip = LayoutInflater.from(context).inflate(R.layout.item_chip,null,false) as Chip
    chip.text = if (text.length > 9) text.substring(0,9) + "..." else text
    chip.isClickable = false
    chip.setEnsureMinTouchTargetSize(isTouchTargeSize)
    if (closeIconListener != null){
        chip.closeIcon = ContextCompat.getDrawable(context, com.google.android.material.R.drawable.ic_mtrl_chip_close_circle)
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener(closeIconListener)
    }
    addView(chip)
}

fun Context.createDialog(layout: Int, cancelable: Boolean): Dialog {
    val dialog = Dialog(this, android.R.style.Theme_Dialog)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(layout)
    dialog.window?.setGravity(Gravity.CENTER)
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setCancelable(cancelable)
    return dialog
}

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun String.isValidEmail() =
    isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()


inline fun TabLayout.onTabSelectionListener(
    crossinline onTabSelected: (TabLayout.Tab?) -> Unit = {},
    crossinline onTabUnselected: (TabLayout.Tab?) -> Unit? = {},
) {
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.onSelection(true)
            onTabSelected.invoke(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            tab?.onSelection(false)
            onTabUnselected.invoke(tab)
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {}
    })
}

fun TabLayout.Tab.onSelection(isSelected: Boolean = true) {
    val textViewTitle: TextView? = customView?.findViewById<TextView>(R.id.tabTitle)
    textViewTitle?.let { textView ->
        textView.setTextColor(
            ContextCompat.getColor(
                textView.context,
                if (isSelected) R.color.tab_selected else R.color.tab_unselected
            )
        )
    }
}

fun Date.startOfDay(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun Date.endOfDay(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.time
}
