package com.devtides.dogs.view


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

import com.devtides.dogs.R
import com.devtides.dogs.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_list.*

/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment() {

    private lateinit var viewModel: ListViewModel
    private val dogsListAdapter = DogsListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //get view model
        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.refresh()
        //set UI
        dogs_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogsListAdapter
        }

        refresh_layout.setOnRefreshListener {
            dogs_list.visibility = View.GONE
            list_error.visibility = View.GONE
            loading_view.visibility = View.VISIBLE
            viewModel.refreshFromDatabaseCache()
            refresh_layout.isRefreshing = false
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dogs.observe(this, Observer { dogs ->
           dogs?.let {
               dogs_list.visibility = View.VISIBLE
               dogsListAdapter.updateDogList(dogs)
           }
        })

        viewModel.dogsLoadError.observe(this, Observer { isError ->
            isError?.let {
                list_error.visibility = if(it) View.VISIBLE else View.GONE
            }
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                loading_view.visibility = if(it) View.VISIBLE else View.GONE
                if(it) {
                    list_error.visibility = View.GONE
                    dogs_list.visibility = View.GONE
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_settings -> {
                view?.let{
                    Navigation.findNavController(it)
                        .navigate(ListFragmentDirections
                            .actionListFragmentToSettingsFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
