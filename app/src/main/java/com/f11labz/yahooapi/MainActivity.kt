package com.f11labz.yahooapi

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.f11labz.yahooapi.databinding.ActivityMainBinding
import com.f11labz.yahooapi.stocklist.StockListFragment
import com.f11labz.yahooapi.stocklist.AddStockDialogFragment
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        fragment = supportFragmentManager.findFragmentById(R.id.stock_fragment)

        binding.fab.setOnClickListener { view ->
            if(isNetworkAvailable(this)) {
                AddStockDialogFragment().show(
                    supportFragmentManager,
                    "StockDialogFragment"
                )
            }
                //TODO add cache and retry
            else {
                Snackbar.make(view, R.string.internet_down_message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    fun addStock(symbol: String) {
        fragment?.let{
            StockListFragment::class.java.cast(fragment).searchAndStock(symbol)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }
}