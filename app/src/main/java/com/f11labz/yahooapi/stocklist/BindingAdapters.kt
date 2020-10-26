package com.f11labz.yahooapi.stocklist

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.f11labz.yahooapi.R
import com.f11labz.yahooapi.data.domain.AppStock
import com.f11labz.yahooapi.formatToCurrency
import com.f11labz.yahooapi.formatToCurrencyWithPlus
import com.f11labz.yahooapi.formatToPercentage


@BindingAdapter("priceFormatted")
fun TextView.setPriceFormatted(appStock : AppStock){
    text = formatToCurrency(appStock.price)
}


@BindingAdapter("changeFormatted")
fun TextView.setchangeFormatted(appStock : AppStock){
    text = formatToCurrencyWithPlus(appStock.absolutechange)
}

@BindingAdapter("percentageFormatted", "showPercentage")
fun TextView.setPercentageFormatted(appStock : AppStock, showPercentage: Boolean){
    val change = formatToCurrencyWithPlus(appStock.absolutechange)
    val percentage = formatToPercentage(appStock.percentchange/100)
    text = when(showPercentage){
        true -> percentage
        else -> change
    }

}
@BindingAdapter("setPill")
fun TextView.setPill(appStock : AppStock){
    setBackgroundResource(when(appStock.absolutechange >= 0.0){
        true -> R.drawable.percent_change_pill_green
        else -> R.drawable.percent_change_pill_red
    })
}


