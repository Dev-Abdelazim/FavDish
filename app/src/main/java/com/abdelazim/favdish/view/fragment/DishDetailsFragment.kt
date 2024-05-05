package com.abdelazim.favdish.view.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.abdelazim.favdish.R
import com.abdelazim.favdish.application.FavDishApplication
import com.abdelazim.favdish.databinding.FragmentDishDetailsBinding
import com.abdelazim.favdish.model.entities.FavDish
import com.abdelazim.favdish.viewmodel.FavDishViewModel
import com.abdelazim.favdish.viewmodel.FavDishViewModelFactory
import com.bumptech.glide.Glide
import java.io.IOException
import java.util.Locale

class DishDetailsFragment : Fragment() {

    private var _binding: FragmentDishDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }
    private var mFavDishDetails: FavDish? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpSharedMenu()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDishDetailsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: DishDetailsFragmentArgs by navArgs()

        args.let {
            try{
                mFavDishDetails = args.dishDetails
                Glide.with(requireActivity())
                    .load(it.dishDetails.imageUri)
                    .centerCrop()
                    .into(binding.ivDish)
            }catch (e: IOException){
                e.printStackTrace()
            }

            binding.tvTitle.text = it.dishDetails.title
            binding.tvType.text = it.dishDetails.type.capitalize(Locale.ROOT) // used to make first letter capital
            binding.tvCategory.text = it.dishDetails.category
            binding.tvIngredients.text = it.dishDetails.ingredients
            binding.tvCookingDirection.text = it.dishDetails.directionToCook
            binding.tvCookingTime.text = resources.getString(
                R.string.lbl_estimate_cooking_time, it.dishDetails.cookingTime
            )



            // reset the default icon
            if (args.dishDetails.isFavoriteDish){
                binding.ivFavorite.setImageDrawable(ContextCompat.getDrawable(requireActivity() ,R.drawable.ic_favorite_selected))
            }else{
                binding.ivFavorite.setImageDrawable(ContextCompat.getDrawable(requireActivity() ,R.drawable.ic_favorite_unselected))
            }


            // setup favorite button action
            binding.ivAddToFavorite.setOnClickListener {
                args.dishDetails.isFavoriteDish = !args.dishDetails.isFavoriteDish
                viewModel.updateDishDetails(args.dishDetails)

                /***change favorite icon to selected or unselected*/
                if (args.dishDetails.isFavoriteDish){
                    binding.ivFavorite.setImageDrawable(ContextCompat.getDrawable(requireActivity() ,R.drawable.ic_favorite_selected))
                    Toast.makeText(
                        requireActivity(),
                        resources.getString(R.string.msg_added_to_favorites),
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    binding.ivFavorite.setImageDrawable(ContextCompat.getDrawable(requireActivity() ,R.drawable.ic_favorite_unselected))
                    Toast.makeText(
                        requireActivity(),
                        resources.getString(R.string.msg_remove_from_favorites),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    /** this is new way to implement menu **/
    private fun setUpSharedMenu(){
        (requireActivity() as MenuHost).addMenuProvider(
            object: MenuProvider{
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_share, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when(menuItem.itemId){
                        R.id.shared_icon -> {
                            val type = "text/plain"
                            val subject = "Checkout this dish recipe"
                            var extraText = ""
                            val shareWith = "share with"

                            mFavDishDetails?.let {
                                var image = ""
                                if (it.isLoadingFromInternet){
                                    image = it.imageUri
                                }

                                var cookingInstruction = Html.fromHtml(
                                    it.directionToCook,
                                    Html.FROM_HTML_MODE_COMPACT
                                ).toString()

                                extraText = """
                                    $image
                                    Title: ${it.title}
                                    Type: ${it.type}
                                    Category: 
                                      ${it.category}
                                    Ingredients: 
                                      ${it.ingredients}
                                    Instructions To Cook:
                                      $cookingInstruction
                                    Time required to cook the dish approx ${it.cookingTime} minutes.
                                """.trimIndent()

                                val intent = Intent(Intent.ACTION_SEND)
                                intent.type = type
                                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                                intent.putExtra(Intent.EXTRA_TEXT, extraText)
                                startActivity(Intent.createChooser(intent, shareWith))
                            }
                        }
                    }
                    return true
                }

            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}