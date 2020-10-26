package com.f11labz.yahooapi.stocklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.f11labz.yahooapi.data.domain.AppStock
import com.f11labz.yahooapi.databinding.StockListItemBinding

class StockAdapter():ListAdapter<AppStock,StockAdapter.AppStockViewHolder>(DiffCallback) {
    var showPercentage : Boolean = false  //needs to be outside of stock
        set(value) {
            field = value
            notifyDataSetChanged()// We have to force list refresh
        }
    class AppStockViewHolder private constructor(private var binding:StockListItemBinding):
            RecyclerView.ViewHolder(binding.root){
        fun bind(appStock : AppStock, showPercentage : Boolean){
            binding.stock = appStock;
            binding.percentage = showPercentage
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): AppStockViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    StockListItemBinding.inflate(layoutInflater, parent, false)
                return AppStockViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
    viewType:Int):AppStockViewHolder{
        return AppStockViewHolder.from(parent)
    }



    override fun onBindViewHolder(holder:AppStockViewHolder, position:Int){
        val appStock = getItem(position)
        holder.bind(appStock,showPercentage)
    }


    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [AppStock]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<AppStock>() {
        override fun areItemsTheSame(oldItem: AppStock, newItem: AppStock): Boolean {
            return oldItem.symbol == newItem.symbol
        }


        override fun areContentsTheSame(oldItem: AppStock, newItem: AppStock): Boolean {
            return oldItem == newItem  //Auto generated equality check from data classes
        }
    }


}