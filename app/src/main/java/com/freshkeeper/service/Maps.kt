package com.freshkeeper.service

import com.freshkeeper.R

val storageLocationMap =
    mapOf(
        "fridge" to R.string.fridge,
        "cupboard" to R.string.cupboard,
        "freezer" to R.string.freezer,
        "counter_top" to R.string.counter_top,
        "cellar" to R.string.cellar,
        "bread_box" to R.string.bread_box,
        "spice_rack" to R.string.spice_rack,
        "pantry" to R.string.pantry,
        "fruit_basket" to R.string.fruit_basket,
        "other" to R.string.other,
    )

val categoryMap =
    mapOf(
        "dairy_goods" to R.string.dairy_goods,
        "vegetables" to R.string.vegetables,
        "fruits" to R.string.fruits,
        "meat" to R.string.meat,
        "fish" to R.string.fish,
        "frozen_goods" to R.string.frozen_goods,
        "spices" to R.string.spices,
        "bread" to R.string.bread,
        "confectionery" to R.string.confectionery,
        "drinks" to R.string.drinks,
        "pasta" to R.string.pasta,
        "canned_goods" to R.string.canned_goods,
        "candy" to R.string.candy,
        "groats" to R.string.groats,
        "sauces" to R.string.sauces,
        "pet_food" to R.string.pet_food,
        "child_food" to R.string.child_food,
        "other" to R.string.other,
    )

val categoryTips =
    mapOf(
        "spices" to R.string.spice_tip,
        "pasta" to R.string.pasta_tip,
        "canned_goods" to R.string.canned_goods_tip,
        "groats" to R.string.groats_tip,
        "sauces" to R.string.sauces_tip,
        "drinks" to R.string.drinks_tip,
        "dairy_goods" to R.string.fresh_food_warning,
        "meat" to R.string.fresh_food_warning,
        "fish" to R.string.fresh_food_warning,
        "vegetables" to R.string.fresh_food_warning,
        "fruits" to R.string.fresh_food_warning,
        "bread" to R.string.fresh_food_warning,
        "candy" to R.string.sweets_tip,
        "confectionery" to R.string.sweets_tip,
    )

val storageLocationReverseMap =
    mapOf(
        R.string.fridge to "fridge",
        R.string.cupboard to "cupboard",
        R.string.freezer to "freezer",
        R.string.counter_top to "counter_top",
        R.string.cellar to "cellar",
        R.string.bread_box to "bread_box",
        R.string.spice_rack to "spice_rack",
        R.string.pantry to "pantry",
        R.string.fruit_basket to "fruit_basket",
        R.string.other to "other",
    )

val categoryReverseMap =
    mapOf(
        R.string.dairy_goods to "dairy_goods",
        R.string.vegetables to "vegetables",
        R.string.fruits to "fruits",
        R.string.meat to "meat",
        R.string.fish to "fish",
        R.string.frozen_goods to "frozen_goods",
        R.string.spices to "spices",
        R.string.bread to "bread",
        R.string.confectionery to "confectionery",
        R.string.drinks to "drinks",
        R.string.pasta to "pasta",
        R.string.canned_goods to "canned_goods",
        R.string.candy to "candy",
        R.string.groats to "groats",
        R.string.sauces to "sauces",
        R.string.pet_food to "pet_food",
        R.string.child_food to "child_food",
        R.string.other to "other",
    )

val reverseHouseholdTypeMap =
    mapOf(
        "Family" to R.string.family,
        "Shared apartment" to R.string.shared_apartment,
        "Single household" to R.string.single_household,
        "Pair" to R.string.pair,
    )

val drawableMap =
    mapOf(
        "user_joined" to R.drawable.user_joined,
        "add_product" to R.drawable.plus,
        "edit" to R.drawable.edit,
        "name" to R.drawable.edit,
        "consumed" to R.drawable.remove,
        "thrown_away" to R.drawable.remove,
        "quantity" to R.drawable.update,
        "expiry" to R.drawable.update,
        "storage" to R.drawable.update,
        "category" to R.drawable.update,
    )

val activityTypeMap =
    mapOf(
        "consumed" to R.string.activity_consumed,
        "thrown_away" to R.string.activity_thrown_away,
        "name" to R.string.activity_name_changed,
        "quantity_increased" to R.string.activity_quantity_increased,
        "quantity_decreased" to R.string.activity_quantity_decreased,
        "expiry" to R.string.activity_expiry_changed,
        "storage" to R.string.activity_storage_changed,
        "category" to R.string.activity_category_changed,
        "edit" to R.string.activity_edited,
        "product_added" to R.string.activity_added,
        "default" to R.string.activity_default,
    )
