package com.abdelazim.favdish.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.abdelazim.favdish.application.FavDishApplication
import com.abdelazim.favdish.databinding.FragmentFavoriteDishesBinding
import com.abdelazim.favdish.model.entities.FavDish
import com.abdelazim.favdish.view.activity.MainActivity
import com.abdelazim.favdish.view.adapters.FavDishesAdapter
import com.abdelazim.favdish.viewmodel.FavDishViewModel
import com.abdelazim.favdish.viewmodel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {

    private var _binding: FragmentFavoriteDishesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val vm: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.favoriteDishes.observe(viewLifecycleOwner){ dishes ->
            dishes?.let { favDishes ->
                binding.rvFavDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
                val favDishAdapter = FavDishesAdapter(this@FavoriteDishesFragment)
                binding.rvFavDishesList.adapter = favDishAdapter

                if (dishes.isNotEmpty()){
                    binding.rvFavDishesList.visibility = View.VISIBLE
                    binding.tvNoFavDishes.visibility = View.GONE
                    favDishAdapter.dishesList(favDishes)
                }else{
                    binding.rvFavDishesList.visibility = View.GONE
                    binding.tvNoFavDishes.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun navToDetailsFragment(dish: FavDish){
        findNavController()
            .navigate(
                FavoriteDishesFragmentDirections
                    .actionFavoriteDishesToNavigationDishDetails(dish)
            )
        if (activity is MainActivity){
            (activity as MainActivity?)?.hideBottomNavigationView()
        }
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity){
            (activity as MainActivity?)?.showBottomNavigationView()
        }
    }
}

