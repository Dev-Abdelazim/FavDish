package com.abdelazim.favdish.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.abdelazim.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDishDao {

    @Insert
    suspend fun insertFavDishDetails(dish: FavDish)

    @Query("SELECT * FROM fav_dishes_table ORDER BY id")
    fun getAllDishes(): Flow<List<FavDish>>

    @Update
    suspend fun updateDish(dish: FavDish)

    @Query("SELECT * FROM fav_dishes_table WHERE favorite_dish = 1")
    fun getAllFavoriteDishes(): Flow<List<FavDish>>

    @Delete
    suspend fun deleteDish(dish: FavDish)

    @Query("SELECT * FROM fav_dishes_table WHERE type = :filterByType")
    fun getFilteredDishesList(filterByType: String): Flow<List<FavDish>>

}