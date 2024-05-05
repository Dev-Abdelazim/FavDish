package com.abdelazim.favdish.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abdelazim.favdish.model.entities.RandomDish
import com.abdelazim.favdish.model.network.RandomDishApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class RandomDishViewModel : ViewModel() {
    private val randomDishApiService: RandomDishApiService = RandomDishApiService()
    private val compositeDisposable = CompositeDisposable()

    val loadingData: MutableLiveData<Boolean> = MutableLiveData()
    val randomDishResponse: MutableLiveData<RandomDish.Recipes> = MutableLiveData()
    val loadingDataError: MutableLiveData<Boolean> = MutableLiveData()


    // get a random dish from api and handel the background work using RxJava
    fun getRandomRecipeFromAPI(){
        loadingData.value = true

        compositeDisposable.add(
            randomDishApiService.getRandomDish()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(
                    object: DisposableSingleObserver<RandomDish.Recipes>(){
                        override fun onSuccess(value: RandomDish.Recipes) {
                            loadingData.value = false
                            randomDishResponse.value = value
                            loadingDataError.value = false
                        }

                        override fun onError(e: Throwable) {
                            loadingData.value = false
                            loadingDataError.value = true
                            e.printStackTrace()
                        }
                    }
                )
        )
    }

}