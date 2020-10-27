package com.f11labz.yahooapi.stocklist

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.f11labz.yahooapi.R
import com.f11labz.yahooapi.data.domain.AppStock
import com.f11labz.yahooapi.formatToNumber
import com.f11labz.yahooapi.formatToNumberWithPlus
import com.f11labz.yahooapi.formatToPercentage


@BindingAdapter("priceFormatted")
fun TextView.setPriceFormatted(appStock : AppStock){
    text = formatToNumber(appStock.price)
}



@BindingAdapter("percentageFormatted", "showPercentage")
fun TextView.setPercentageFormatted(appStock : AppStock, showPercentage: Boolean){
    val change = formatToNumberWithPlus(appStock.absolutechange)
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

@BindingAdapter("formatColor", "showPercentage")
fun TextView.formatColor(appStock : AppStock,showPercentage: Boolean){
    if(appStock.postmarketabsolutechange == Float.MIN_VALUE){
        visibility = View.INVISIBLE
    }
    else {
        visibility = View.VISIBLE
        val change = formatToNumber(appStock.postmarketabsolutechange)
        val percentage = formatToPercentage(appStock.postmarketpercentchange)
        text = when (showPercentage) {
            true -> percentage
            else -> change
        }
        if (showPercentage) {
            setTextColor(
                when (appStock.postmarketpercentchange >= 0.0) {
                    true -> ContextCompat.getColor(this.context, R.color.material_green_700)
                    false -> ContextCompat.getColor(this.context, R.color.material_red_700)
                }
            )
        } else
            setTextColor(Color.DKGRAY)
    }
}

@BindingAdapter("showHidePostText")
fun TextView.showHidePostText(appStock : AppStock){
    visibility = when(appStock.postmarketabsolutechange == Float.MIN_VALUE){
        true ->View.INVISIBLE
        false -> View.VISIBLE
    }

}

