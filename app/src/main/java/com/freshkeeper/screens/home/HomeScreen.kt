package com.freshkeeper.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.model.FoodItem
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.LowerTransition
import com.freshkeeper.screens.UpperTransition
import com.freshkeeper.screens.home.viewmodel.HomeViewModel
import com.freshkeeper.screens.notifications.viewmodel.NotificationsViewModel
import com.freshkeeper.sheets.AddEntrySheet
import com.freshkeeper.sheets.BarcodeScannerSheet
import com.freshkeeper.sheets.EditProductSheet
import com.freshkeeper.sheets.ManualInputSheet
import com.freshkeeper.ui.theme.AccentGreenColor
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.TextColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()

    var scannedBarcode by remember { mutableStateOf("") }
    var expiryDate by remember { mutableLongStateOf(0L) }

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val manualInputSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editProductSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val barcodeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val expiringSoonItems by viewModel.expiringSoonItems.observeAsState(emptyList())
    val expiredItems by viewModel.expiredItems.observeAsState(emptyList())

    var foodItem by remember { mutableStateOf<FoodItem?>(null) }

    val listState = rememberLazyListState()
    val showTransition by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }

    FreshKeeperTheme {
        Scaffold(
            bottomBar = {
                Box(
                    modifier =
                        Modifier
                            .background(BottomNavBackgroundColor)
                            .padding(horizontal = 10.dp),
                ) {
                    BottomNavigationBar(selectedIndex = 0, navController, notificationsViewModel)
                }
            },
        ) { it ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(it),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.overview),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        modifier = Modifier.padding(16.dp),
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn(
                            state = listState,
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(start = 15.dp, end = 15.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            if (expiringSoonItems.isNotEmpty()) {
                                item {
                                    FoodList(
                                        title = stringResource(id = R.string.expiring_soon),
                                        image = painterResource(id = R.drawable.expiring_soon),
                                        items =
                                            expiringSoonItems.map { item ->
                                                val displayText =
                                                    when {
                                                        item.daysDifference == 1 ->
                                                            stringResource(R.string.tomorrow)

                                                        item.daysDifference > 1 ->
                                                            stringResource(
                                                                R.string.in_days,
                                                                item.daysDifference,
                                                            )

                                                        item.daysDifference == 0 ->
                                                            stringResource(R.string.today)

                                                        else -> ""
                                                    }
                                                Triple(item.id, item.name, displayText)
                                            },
                                        editProductSheetState = editProductSheetState,
                                        onEditProduct = { id ->
                                            foodItem = expiringSoonItems.find { it.id == id }
                                            coroutineScope.launch { editProductSheetState.show() }
                                        },
                                    )
                                }
                            }
                            if (expiredItems.isNotEmpty()) {
                                item {
                                    FoodList(
                                        title = stringResource(R.string.expired),
                                        image = painterResource(id = R.drawable.warning),
                                        items =
                                            expiredItems.map { item ->
                                                val displayText =
                                                    when {
                                                        -item.daysDifference == 1 ->
                                                            stringResource(
                                                                R.string.yesterday,
                                                            )

                                                        item.daysDifference < 0 ->
                                                            stringResource(
                                                                R.string.days_ago,
                                                                -item.daysDifference,
                                                            )

                                                        else -> ""
                                                    }
                                                Triple(item.id, item.name, displayText)
                                            },
                                        editProductSheetState = editProductSheetState,
                                        onEditProduct = { id ->
                                            foodItem = expiredItems.find { it.id == id }
                                            coroutineScope.launch { editProductSheetState.show() }
                                        },
                                    )
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                        if (showTransition) {
                            UpperTransition()
                        }
                        LowerTransition(
                            modifier = Modifier.align(Alignment.BottomCenter),
                        )
                    }
                    Button(
                        onClick = { coroutineScope.launch { sheetState.show() } },
                        modifier =
                            Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 5.dp, start = 10.dp, end = 10.dp, bottom = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.dp, ComponentStrokeColor),
                        shape = RoundedCornerShape(25.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            FloatingActionButton(
                                onClick = { coroutineScope.launch { sheetState.show() } },
                                modifier = Modifier.size(35.dp),
                                shape = RoundedCornerShape(25.dp),
                                containerColor = AccentGreenColor,
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    tint = BottomNavBackgroundColor,
                                    contentDescription = "Add Food",
                                )
                            }
                            Text(
                                text = stringResource(id = R.string.add_food),
                                style = MaterialTheme.typography.titleMedium,
                                color = AccentGreenColor,
                                modifier = Modifier.padding(start = 10.dp),
                            )
                        }
                    }
                }
            }

            if (sheetState.isVisible) {
                AddEntrySheet(
                    sheetState,
                    barcodeSheetState,
                    manualInputSheetState,
                )
            }

            if (barcodeSheetState.isVisible) {
                ModalBottomSheet(
                    onDismissRequest = { coroutineScope.launch { barcodeSheetState.hide() } },
                    sheetState = barcodeSheetState,
                    containerColor = ComponentBackgroundColor,
                ) {
                    BarcodeScannerSheet(
                        sheetState = barcodeSheetState,
                        onBarcodeScanned = { barcode, date ->
                            scannedBarcode = barcode
                            expiryDate = date
                            coroutineScope.launch { manualInputSheetState.show() }
                        },
                    )
                }
            }

            if (manualInputSheetState.isVisible) {
                ModalBottomSheet(
                    onDismissRequest = { coroutineScope.launch { manualInputSheetState.hide() } },
                    sheetState = manualInputSheetState,
                    containerColor = ComponentBackgroundColor,
                ) {
                    ManualInputSheet(
                        sheetState = manualInputSheetState,
                        barcode = scannedBarcode,
                        expiryTimestamp = expiryDate,
                    )
                }
            }

            if (editProductSheetState.isVisible) {
                ModalBottomSheet(
                    onDismissRequest = { coroutineScope.launch { editProductSheetState.hide() } },
                    sheetState = editProductSheetState,
                    containerColor = ComponentBackgroundColor,
                ) {
                    foodItem?.let { item ->
                        EditProductSheet(editProductSheetState, item)
                    }
                }
            }
        }
    }
}
