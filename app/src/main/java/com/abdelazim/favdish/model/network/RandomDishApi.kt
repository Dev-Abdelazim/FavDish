package com.abdelazim.favdish.model.network

import com.abdelazim.favdish.model.entities.RandomDish
import com.abdelazim.favdish.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomDishApi {

    @GET(Constants.API_ENDPOINT)
    fun getRandomDish(
        @Query(Constants.API_KEY) apiKey: String,
        @Query(Constants.LIMIT_LICENSE) limitLicense: Boolean,
        @Query(Constants.INCLUDE_TAGS) includeTags: String,
        @Query(Constants.NUMBER) number: Int,
    ): Single<RandomDish.Recipes>
}