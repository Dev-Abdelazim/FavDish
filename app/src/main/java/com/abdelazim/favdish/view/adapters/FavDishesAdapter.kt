package com.abdelazim.favdish.view.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.abdelazim.favdish.R
import com.abdelazim.favdish.databinding.ItemDishLayoutBinding
import com.abdelazim.favdish.model.entities.FavDish
import com.abdelazim.favdish.utils.Constants
import com.abdelazim.favdish.view.activity.AddUpdateDishActivity
import com.abdelazim.favdish.view.fragment.AllDishesFragment
import com.abdelazim.favdish.view.fragment.FavoriteDishesFragment
import com.bumptech.glide.Glide

class FavDishesAdapter(
    private val fragment: Fragment
): RecyclerView.Adapter<FavDishesAdapter.ViewHolder>(){

    private var dishes: List<FavDish> = listOf()

    class ViewHolder(val view: ItemDishLayoutBinding): RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDishLayoutBinding.inflate(
            LayoutInflater.from(fragment.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        Glide.with(fragment)
            .load(dish.imageUri)
            .into(holder.view.ivDishImage)
        holder.view.tvDishTitle.text = dish.title

        holder.itemView.setOnClickListener {
            when(fragment){
                is AllDishesFragment -> {
                    fragment.navToDetailsFragment(dish)
                }
                is FavoriteDishesFragment -> {
                    fragment.navToDetailsFragment(dish)
                }
            }
        }

        // popup menu setup and action
        holder.view.ibMore.setOnClickListener {
            val popupMenu = PopupMenu(fragment.context, holder.view.ibMore)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.edit_dish -> {
                        val intent = Intent(fragment.requireActivity(), AddUpdateDishActivity::class.java)
                        intent.putExtra(Constants.EDIT_DISH_KEY, dish)
                        fragment.requireActivity().startActivity(intent)
                    }
                    R.id.delete_dish -> {
                        if (fragment is AllDishesFragment){
                            fragment.deleteDishAlertDialog(dish)
                        }
                    }
                }
                true
            }
            popupMenu.show()
        }

        if (fragment is AllDishesFragment){
            holder.view.ibMore.visibility = View.VISIBLE
        }else{
            holder.view.ibMore.visibility = View.GONE
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun dishesList(listOfDishes: List<FavDish>){
        dishes = listOfDishes
        notifyDataSetChanged()
    }

}