package com.freshkeeper.screens.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.freshkeeper.R
import com.freshkeeper.model.FoodItem
import com.freshkeeper.screens.inventory.viewmodel.InventoryViewModel
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun CurrentInventoriesSection(
    editProductSheetState: SheetState,
    onItemClick: (FoodItem) -> Unit,
    selectedStorageLocations: List<String>,
    selectedCategories: List<String>,
    onItemsUpdated: (List<FoodItem>) -> Unit,
) {
    val viewModel: InventoryViewModel = hiltViewModel()

    val fridgeItems by viewModel.fridgeItems.observeAsState(emptyList())
    val cupboardItems by viewModel.cupboardItems.observeAsState(emptyList())
    val freezerItems by viewModel.freezerItems.observeAsState(emptyList())
    val countertopItems by viewModel.countertopItems.observeAsState(emptyList())
    val cellarItems by viewModel.cellarItems.observeAsState(emptyList())
    val bakeryItems by viewModel.bakeryItems.observeAsState(emptyList())
    val spiceRackItems by viewModel.spiceRackItems.observeAsState(emptyList())
    val pantryItems by viewModel.pantryItems.observeAsState(emptyList())
    val fruitBasketItems by viewModel.fruitBasketItems.observeAsState(emptyList())
    val otherItems by viewModel.otherItems.observeAsState(emptyList())

    val storageLocations =
        listOf(
            Triple(stringResource(R.string.fridge), R.drawable.fridge, fridgeItems),
            Triple(stringResource(R.string.cupboard), R.drawable.cupboard, cupboardItems),
            Triple(stringResource(R.string.freezer), R.drawable.freezer, freezerItems),
            Triple(stringResource(R.string.counter_top), R.drawable.counter_top, countertopItems),
            Triple(stringResource(R.string.cellar), R.drawable.cellar, cellarItems),
            Triple(stringResource(R.string.bread_box), R.drawable.bread_box, bakeryItems),
            Triple(stringResource(R.string.spice_rack), R.drawable.spice_rack, spiceRackItems),
            Triple(stringResource(R.string.pantry), R.drawable.pantry, pantryItems),
            Triple(stringResource(R.string.fruit_basket), R.drawable.fruit_basket, fruitBasketItems),
            Triple(stringResource(R.string.other), R.drawable.other, otherItems),
        )

    val allStorageLocationsEmpty =
        storageLocations.all { (_, _, items) ->
            items.isEmpty() ||
                items.none {
                    (selectedStorageLocations.isEmpty() || it.storageLocation in selectedStorageLocations) &&
                        (selectedCategories.isEmpty() || it.category in selectedCategories)
                }
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (allStorageLocationsEmpty) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(ComponentBackgroundColor)
                        .border(1.dp, ComponentStrokeColor, RoundedCornerShape(15.dp)),
            ) {
                Text(
                    text = stringResource(R.string.add_products_inventory),
                    modifier = Modifier.padding(16.dp),
                )
            }
        } else {
            storageLocations.forEach { (title, image, items) ->
                val filteredItems =
                    items.filter {
                        (
                            selectedStorageLocations.isEmpty() ||
                                it.storageLocation in selectedStorageLocations
                        ) &&
                            (
                                selectedCategories.isEmpty() ||
                                    it.category in selectedCategories
                            )
                    }

                if (filteredItems.isNotEmpty()) {
                    StorageLocation(
                        title = title,
                        image = painterResource(id = image),
                        items = filteredItems,
                        editProductSheetState = editProductSheetState,
                        onItemClick = onItemClick,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                onItemsUpdated(filteredItems)
            }
        }
    }
}
