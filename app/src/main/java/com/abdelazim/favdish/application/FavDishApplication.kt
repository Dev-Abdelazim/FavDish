package com.abdelazim.favdish.application

import android.app.Application
import com.abdelazim.favdish.model.database.FavDishDatabase
import com.abdelazim.favdish.model.database.repository.FavDishRepository

class FavDishApplication: Application() {
    // create database instance
    val database by lazy {
        FavDishDatabase.getDatabase(this@FavDishApplication)
    }

    // create repository instance
    val repository by lazy {
        FavDishRepository(database.favDishDao)
    }


}