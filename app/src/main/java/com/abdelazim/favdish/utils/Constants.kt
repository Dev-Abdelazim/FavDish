package com.abdelazim.favdish.utils

object Constants {

    const val ALL_ITEMS: String = "All"
    const val FILTER_SELECTION: String = "filterSelection"
    const val DISH_TYPE: String = "DishType"
    const val DISH_CATEGORY: String = "DishCategory"
    const val EDIT_DISH_KEY: String = "edit dish key"


    const val API_ENDPOINT: String = "recipes/random"

    const val API_KEY:String = "apiKey"
    const val LIMIT_LICENSE: String = "limitLicense"
    const val INCLUDE_TAGS: String = "include-tags"
    const val NUMBER: String = "number"

    const val BASE_URL: String = "https://api.spoonacular.com/"
    const val API_KEY_VALUE:String = "15d70407d2ed4c2395f3b345ab48c7b5"
    const val LIMIT_LICENSE_VALUE: Boolean = true
    const val INCLUDE_TAGS_VALUE: String = "vegetarian, dessert"
    const val NUMBER_VALUE: Int = 1

    // constants from notification part
    const val NOTIFICATION_ID = "FavDish_notification_id"
    const val NOTIFICATION_NAME = "FavDish"
    const val NOTIFICATION_CHANNEL = "FavDish_channel_01"



    private val _dishTypesList: ArrayList<String> = arrayListOf(
        "breakfast",
        "lunch",
        "snacks",
        "dinner",
        "salad",
        "side dish",
        "dessert",
        "other"
    )
    val dishTypesList = _dishTypesList


    private val _dishCategory: ArrayList<String> = arrayListOf(
        "Pizza",
        "BBQ",
        "Bakery",
        "Burger",
        "Cafe",
        "Chicken",
        "Dessert",
        "Drinks",
        "Hot Dogs",
        "Juices",
        "Sandwich",
        "Tea & Coffee",
        "Wraps",
        "other"
    )
    val dishCategoryList = _dishCategory

    val filterItemsList: ArrayList<String> = arrayListOf<String>().apply {
        add(ALL_ITEMS)
        addAll(_dishTypesList)
    }


}