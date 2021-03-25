package com.f11labz.yahooapi.stocklist

import android.os.Bundle
import android.view.*
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.f11labz.yahooapi.R
import com.f11labz.yahooapi.data.database.getDatabase
import com.f11labz.yahooapi.data.repository.StockRepository
import com.f11labz.yahooapi.databinding.StockListFragmentBinding
import com.f11labz.yahooapi.data.repository.StockRepository.SearchStockStatus

/**
 * A simple [Fragment] subclass to show stockList
 */
class StockListFragment : Fragment() {

    private lateinit var binding: StockListFragmentBinding

    private val viewModel: StockViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        val repository = StockRepository(getDatabase(activity.application).stockDao)
        ViewModelProvider(this, StockViewModel.Factory(repository))
            .get(StockViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.stock_list_fragment,
        container,false)

        val stockViewModel = viewModel
        val adapter = StockAdapter()
        binding.stockList.adapter = adapter
        lifecycle.addObserver(viewModel) //Start stop sync
        stockViewModel.stocks.observe(viewLifecycleOwner, Observer {
            it?.let{
                adapter.submitList(it)
            }
        })

        stockViewModel.status.observe(viewLifecycleOwner, Observer{
          when(it){
              SearchStockStatus.LOADING -> {
                  binding.statusImage.visibility = View.VISIBLE
                  binding.statusImage.setImageResource(R.drawable.loading_animation)
              }
              SearchStockStatus.ERROR -> {
                  binding.statusImage.visibility = View.GONE
                  Toast.makeText(activity,R.string.fetch_error,Toast.LENGTH_LONG).show()
              }
              SearchStockStatus.NOTFOUND->{
                  binding.statusImage.visibility = View.GONE
                  Toast.makeText(activity,R.string.stock_not_found,Toast.LENGTH_LONG).show()
              }
              SearchStockStatus.DONE -> {
                  binding.statusImage.visibility = View.GONE
                  Toast.makeText(activity,R.string.stock_found,Toast.LENGTH_LONG).show()
              }
          }
        })
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        val toggleButton : AppCompatToggleButton = menu.findItem(R.id.switchId)
            .actionView.findViewById<AppCompatToggleButton>(R.id.displaymodeswitch)
        toggleButton.setOnCheckedChangeListener { compoundButton: CompoundButton,
                                                  isChecked: Boolean ->

            val adapter = binding.stockList.adapter as StockAdapter
            adapter.showPercentage = isChecked
        }

        return super.onCreateOptionsMenu(menu, inflater)
    }

    fun searchAndStock(symbol: String){
        viewModel.addStock(symbol)
    }
}