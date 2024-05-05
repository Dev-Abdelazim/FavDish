package com.abdelazim.favdish.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.abdelazim.favdish.databinding.ItemCustomListBinding
import com.abdelazim.favdish.view.activity.AddUpdateDishActivity
import com.abdelazim.favdish.view.fragment.AllDishesFragment

class CustomListItemAdapter(
    private val activity: Activity,
    private val items: ArrayList<String>,
    private val selection: String,
    private val fragment: Fragment? = null
): RecyclerView.Adapter<CustomListItemAdapter.viewHolder>() {

    class viewHolder(view: ItemCustomListBinding): RecyclerView.ViewHolder(view.root){
        val tvText = view.tvCustomItemText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = ItemCustomListBinding.inflate(
            LayoutInflater.from(
                activity,
            ),
            parent,
            false
        )
        return viewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val item = items[position]
        holder.tvText.text = item
        holder.itemView.setOnClickListener {
            if (activity is AddUpdateDishActivity){
                activity.selectedListItem(item, selection)
            }

            if (fragment is AllDishesFragment){
                fragment.filterSelection(item)
            }
        }
    }


}