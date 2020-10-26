package com.f11labz.yahooapi.stocklist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.f11labz.yahooapi.MainActivity
import com.f11labz.yahooapi.R

/**
 * Dialog to Add Stock
 * TODO make autocomplete search
 */
class AddStockDialogFragment : DialogFragment() {
    var mStockText: EditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //Not Using data binding here
        val builder =
            AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)

        val custom: View = inflater.inflate(R.layout.add_stock_dialog, null)

        mStockText = custom.findViewById(R.id.dialog_stock)
        mStockText?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            addStock()
            true
        })

        builder.setView(custom)
        builder.setMessage(getString(R.string.dialog_title))
        builder.setPositiveButton(
            getString(R.string.dialog_add)
        ) { dialog, id -> addStock() }

        builder.setNegativeButton(getString(R.string.dialog_cancel), null)
        val dialog: Dialog = builder.create()
        val window = dialog.window
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        return dialog
    }

    private fun addStock() {
        val symbol = mStockText!!.text.toString().toUpperCase()
        if(!TextUtils.isEmpty(symbol)) {
            val mainActivity = this.activity as MainActivity
            mainActivity.addStock(symbol)
        }
        else{
            Toast.makeText(activity,getString(R.string.enter_symbol_message),Toast.LENGTH_LONG).show()
        }
        dismissAllowingStateLoss()
    }
}