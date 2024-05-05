package com.abdelazim.favdish.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.abdelazim.favdish.model.entities.FavDish


@Database(
    entities = [FavDish::class],
    version = 1,
    exportSchema = false
)
abstract class FavDishDatabase: RoomDatabase(){
    abstract val favDishDao: FavDishDao

    companion object{
        @Volatile
        private var INSTANCE: FavDishDatabase? = null

        fun getDatabase(context: Context): FavDishDatabase{
            return INSTANCE ?: synchronized(this){
                val instance: FavDishDatabase = Room.databaseBuilder(
                    context.applicationContext,
                    FavDishDatabase::class.java,
                    "fav_dish_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}