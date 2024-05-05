package com.abdelazim.favdish.model.database.repository

import androidx.annotation.WorkerThread
import com.abdelazim.favdish.model.database.FavDishDao
import com.abdelazim.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val favDishDao: FavDishDao) {


    @WorkerThread
    suspend fun insertFavDishDetails(dish: FavDish){
        favDishDao.insertFavDishDetails(dish)
    }

    fun getAllDishes(): Flow<List<FavDish>> = favDishDao.getAllDishes()

    @WorkerThread
    suspend fun updateDishDetails(dish: FavDish) {
        favDishDao.updateDish(dish)
    }

    fun getAllFavoriteDishes(): Flow<List<FavDish>> = favDishDao.getAllFavoriteDishes()

    @WorkerThread
    suspend fun deleteDish(dish: FavDish) = favDishDao.deleteDish(dish)

    fun getFilteredDishesList(filterByType: String): Flow<List<FavDish>> = favDishDao.getFilteredDishesList(filterByType)
}

