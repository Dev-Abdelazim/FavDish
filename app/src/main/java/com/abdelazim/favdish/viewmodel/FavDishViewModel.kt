package com.abdelazim.favdish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.abdelazim.favdish.model.database.repository.FavDishRepository
import com.abdelazim.favdish.model.entities.FavDish
import kotlinx.coroutines.launch

class FavDishViewModel(private val repo: FavDishRepository): ViewModel() {

    val allFavDishesList: LiveData<List<FavDish>> = repo.getAllDishes().asLiveData()
    val favoriteDishes: LiveData<List<FavDish>> = repo.getAllFavoriteDishes().asLiveData()

    fun insert(dish: FavDish) = viewModelScope.launch {
        repo.insertFavDishDetails(dish)
    }

    fun updateDishDetails(dish: FavDish) = viewModelScope.launch {
        repo.updateDishDetails(dish)
    }

    fun deleteDish(dish: FavDish) = viewModelScope.launch {
        repo.deleteDish(dish)
    }

    fun getFilteredList(filterType: String): LiveData<List<FavDish>> =
        repo.getFilteredDishesList(filterType).asLiveData()

}

class FavDishViewModelFactory(private val repo: FavDishRepository)
    : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavDishViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return FavDishViewModel(repo) as T
        }
        throw IllegalAccessException("Unknown ViewModel Class")
    }
}