package com.abdelazim.favdish.view.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.abdelazim.favdish.R
import com.abdelazim.favdish.application.FavDishApplication
import com.abdelazim.favdish.databinding.FragmentRandomDishBinding
import com.abdelazim.favdish.model.entities.FavDish
import com.abdelazim.favdish.model.entities.RandomDish
import com.abdelazim.favdish.viewmodel.FavDishViewModel
import com.abdelazim.favdish.viewmodel.FavDishViewModelFactory
import com.abdelazim.favdish.viewmodel.RandomDishViewModel
import com.bumptech.glide.Glide

class RandomDishFragment : Fragment() {

    private var _binding: FragmentRandomDishBinding? = null
    private lateinit var randomDishViewModel: RandomDishViewModel
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var isSaved: Boolean = false
    private var mProgressDialog: Dialog? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRandomDishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        randomDishViewModel = ViewModelProvider(this).get(RandomDishViewModel::class.java)

        randomDishViewModel.getRandomRecipeFromAPI()

        randomDishViewModelObserver()

        // swipe to refresh and git new random dish
        binding.srlRandomDish.setOnRefreshListener {
            randomDishViewModel.getRandomRecipeFromAPI()
        }
    }

    private fun randomDishViewModelObserver() {
        randomDishViewModel.randomDishResponse.observe(viewLifecycleOwner){ randomDishResponse ->
            randomDishResponse?.let {
                if (binding.srlRandomDish.isRefreshing){
                    binding.srlRandomDish.isRefreshing = false
                    isSaved = false
                }
                setUpRandomDishUi(it.recipes[0])
            }
        }

        randomDishViewModel.loadingData.observe(viewLifecycleOwner){ isLoading ->
            isLoading?.let {
                if (isLoading && !binding.srlRandomDish.isRefreshing){
                    showCustomProgressDialog()
                }else{
                    hideCustomProgressDialog()
                }
            }
        }

        randomDishViewModel.loadingDataError.observe(viewLifecycleOwner){ loadingDataError ->
            loadingDataError?.let {
                if (binding.srlRandomDish.isRefreshing){
                    binding.srlRandomDish.isRefreshing = false
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpRandomDishUi(recipe: RandomDish.Recipe) {
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(binding.ivRandomDish)

        binding.tvTitle.text = recipe.title

        var dishType: String = "other"
        if (recipe.dishTypes.isNotEmpty()){
            dishType = recipe.dishTypes[0]
            binding.tvRandomType.text = dishType
        }

        binding.tvRandomCategory.text = "Other"

        var ingredients: String = ""
        recipe.extendedIngredients.forEach {
            if (ingredients.isEmpty()){
                ingredients = it.original
            }else{
                ingredients = ingredients + ", \n" + it.original
            }
        }
        binding.tvIngredients.text = ingredients

        val directionToCook = Html.fromHtml(
            recipe.instructions,
            Html.FROM_HTML_MODE_COMPACT
        ).toString()
        binding.tvRandomCookingDirection.text = directionToCook

        binding.tvRandomCookingTime.text = resources.getString(
            R.string.lbl_estimate_cooking_time,
            recipe.readyInMinutes.toString()
        )

        binding.ivRandomSave.setImageDrawable(
            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_unsaved)
        )


        // save Random Dish into database
        binding.ivRandomAddSave.setOnClickListener {
            saveRandomDishToMyDishes(
                recipe.image,
                recipe.title,
                dishType,
                ingredients,
                recipe.readyInMinutes.toString(),
                directionToCook,
            )

        }
    }

    private fun saveRandomDishToMyDishes(
        imageUri: String,
        title: String,
        type: String ,
        ingredients: String ,
        cookingTime: String ,
        directionToCook: String ,
        category: String = "other",
        isLoadingFromInternet: Boolean = true ,
        isFavoriteDish: Boolean = false,
    ){
        val vm: FavDishViewModel by viewModels {
            FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
        }

        if (!isSaved){
            val randomDish = FavDish(
                imageUri = imageUri,
                isLoadingFromInternet = isLoadingFromInternet,
                title = title,
                type = type,
                category = category,
                ingredients = ingredients,
                cookingTime = cookingTime,
                directionToCook = directionToCook,
                isFavoriteDish = isFavoriteDish,
            )

            vm.insert(randomDish)
            isSaved = true
            Toast.makeText(requireActivity(),
                getString(R.string.add_to_your_dishes_successfully),
                Toast.LENGTH_SHORT
            ).show()
            binding.ivRandomSave.setImageDrawable(
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_saved)
            )
        }else{
            Toast.makeText(requireActivity(),
                getString(R.string.msg_already_saved),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showCustomProgressDialog(){
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog?.let {dialog ->
            dialog.setContentView(R.layout.dialog_custom_progress)
            dialog.show()
        }
    }

    private fun hideCustomProgressDialog(){
        mProgressDialog?.let {dialog ->
            dialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}