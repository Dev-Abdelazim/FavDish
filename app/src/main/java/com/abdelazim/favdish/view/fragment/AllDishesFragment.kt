package com.abdelazim.favdish.view.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdelazim.favdish.R
import com.abdelazim.favdish.application.FavDishApplication
import com.abdelazim.favdish.databinding.DialogCustomListBinding
import com.abdelazim.favdish.databinding.FragmentAllDishesBinding
import com.abdelazim.favdish.model.entities.FavDish
import com.abdelazim.favdish.utils.Constants
import com.abdelazim.favdish.view.activity.AddUpdateDishActivity
import com.abdelazim.favdish.view.activity.MainActivity
import com.abdelazim.favdish.view.adapters.CustomListItemAdapter
import com.abdelazim.favdish.view.adapters.FavDishesAdapter
import com.abdelazim.favdish.viewmodel.FavDishViewModel
import com.abdelazim.favdish.viewmodel.FavDishViewModelFactory

class AllDishesFragment : Fragment() {

    private var _binding: FragmentAllDishesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    private lateinit var FavDishAdapter: FavDishesAdapter
    private lateinit var customListDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        FavDishAdapter = FavDishesAdapter(this@AllDishesFragment)
        binding.rvDishesList.adapter = FavDishAdapter


        displayAllDishesInRecyclerView()
    }

    private fun displayAllDishesInRecyclerView() {
        viewModel.allFavDishesList.observe(viewLifecycleOwner) { allDishes ->
            allDishes?.let { dishes ->
                setUpRecyclerViewDataAndVisibility(dishes)
            }
        }
    }

    private fun setUpRecyclerViewDataAndVisibility(dishes: List<FavDish>) {
        if (dishes.isNotEmpty()) {
            binding.rvDishesList.visibility = View.VISIBLE
            binding.tvNoDishes.visibility = View.GONE
            FavDishAdapter.dishesList(dishes)
        } else {
            binding.rvDishesList.visibility = View.GONE
            binding.tvNoDishes.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllDishesBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_dish_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.add_dish -> {
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }
            R.id.filter_dish_icon -> {
                filterDishesListDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun navToDetailsFragment(dish: FavDish){
        findNavController()
            .navigate(
                AllDishesFragmentDirections.actionAllDishesToDishDetails(dish)
            )

        if (activity is MainActivity){
            (activity as MainActivity?)?.hideBottomNavigationView()
        }
    }

    // set alert dialog to popup when user want to delete dish
    fun deleteDishAlertDialog(
        dish: FavDish
     ){
        AlertDialog.Builder(requireActivity())
            .setTitle(requireActivity().resources.getString(R.string.delete_alert_dialog_title))
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(requireActivity().getString(R.string.delete_alert_dialog_msg, dish.title))
            .setNegativeButton(requireActivity().getString(R.string.delete_alert_dialog_cancel_button_text)){dialog,_ ->
                dialog.dismiss()
            }
            .setPositiveButton(requireActivity().getString(R.string.delete_alert_dialog_delete_button_text)){dialog,_ ->
                viewModel.deleteDish(dish)
                Toast.makeText(
                    requireActivity(),
                    "You delete ${dish.title} Successfully.",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun filterDishesListDialog(){
        customListDialog = Dialog(requireActivity())

        val cBinding = DialogCustomListBinding.inflate(layoutInflater)
        customListDialog.setContentView(cBinding.root)

        cBinding.tvDialogTitle.text = resources.getString(R.string.title_select_item_to_filter)

        cBinding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = CustomListItemAdapter(
            requireActivity(),
            Constants.filterItemsList,
            Constants.FILTER_SELECTION,
            this@AllDishesFragment
        )
        cBinding.rvList.adapter = adapter

        customListDialog.show()
    }


    fun filterSelection(filterItemSelection: String){
        customListDialog.dismiss()

        Log.d("filterItem", "filterSelection: $filterItemSelection")
        if (filterItemSelection == Constants.ALL_ITEMS){
            displayAllDishesInRecyclerView()
        }else{
            viewModel.getFilteredList(filterItemSelection).observe(viewLifecycleOwner){ dishes ->
                dishes?.let {
                    setUpRecyclerViewDataAndVisibility(it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity){
            (activity as MainActivity?)?.showBottomNavigationView()
        }
    }

}