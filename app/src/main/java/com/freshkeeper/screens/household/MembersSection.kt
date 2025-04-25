package com.freshkeeper.screens.household

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import com.freshkeeper.R
import com.freshkeeper.model.Member
import com.freshkeeper.screens.household.viewmodel.HouseholdViewModel
import com.freshkeeper.screens.inventory.viewmodel.InventoryViewModel
import com.freshkeeper.service.PictureConverter
import com.freshkeeper.service.householdTypeMap
import com.freshkeeper.ui.theme.AccentTurquoiseColor
import com.freshkeeper.ui.theme.ComponentBackgroundColor
import com.freshkeeper.ui.theme.ComponentStrokeColor
import com.freshkeeper.ui.theme.GreyColor
import com.freshkeeper.ui.theme.TextColor
import com.freshkeeper.ui.theme.WhiteColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun MembersSection(
    navController: NavController,
    inviteSheetState: SheetState,
    onCreateHouseholdClick: (String, String) -> Unit,
    onJoinHouseholdClick: (String) -> Unit,
    onAddProducts: () -> Unit,
    onDeleteProducts: () -> Unit,
    isStory: Boolean = false,
) {
    val pictureConverter = PictureConverter()
    val viewModel: HouseholdViewModel = hiltViewModel()
    val inventoryViewModel: InventoryViewModel = hiltViewModel()

    val items by inventoryViewModel.foodItems.observeAsState(emptyList())
    val members by viewModel.members.observeAsState(emptyList())
    val isInHousehold by viewModel.isInHousehold.observeAsState(false)
    val household by viewModel.household.observeAsState()
    val coroutineScope = rememberCoroutineScope()

    val householdId = remember { mutableStateOf(household?.id ?: "") }
    var householdName by remember { mutableStateOf(household?.name ?: "") }

    val showJoinHouseholdDialog = remember { mutableStateOf(false) }
    var showCreateHouseholdDialog by remember { mutableStateOf(false) }
    var showHouseholdTypeDialog by remember { mutableStateOf(false) }
    var showAddProductsDialog by remember { mutableStateOf(false) }
    var householdType by remember { mutableStateOf("") }

    val imageLoader =
        ImageLoader
            .Builder(LocalContext.current)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()

    Card(
        modifier =
            Modifier.fillMaxWidth().border(
                1.dp,
                ComponentStrokeColor,
                RoundedCornerShape(15.dp),
            ),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = ComponentBackgroundColor),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(id = R.string.members),
                    color = AccentTurquoiseColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp).weight(1f),
                )
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null,
                    modifier =
                        if (!isStory) {
                            Modifier.size(20.dp).clickable {
                                coroutineScope.launch {
                                    navController.navigate("householdSettings")
                                }
                            }
                        } else {
                            Modifier.size(20.dp)
                        },
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (isStory) {
                    val dummyMembers =
                        listOf(
                            Member(null, "Tim", "1"),
                            Member(null, "Emma", "2"),
                        )
                    dummyMembers.forEach { member ->
                        val profilePicture =
                            when (member.userId) {
                                "1" -> R.drawable.avatar1
                                "2" -> R.drawable.avatar2
                                else -> R.drawable.profile
                            }

                        Box(
                            modifier =
                                Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .width(100.dp)
                                    .height(100.dp)
                                    .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                    .background(ComponentBackgroundColor)
                                    .padding(10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Image(
                                    painter = painterResource(id = profilePicture),
                                    contentDescription = "Profile Picture",
                                    modifier =
                                        Modifier
                                            .size(55.dp)
                                            .clip(RoundedCornerShape(50)),
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = member.name,
                                    color = TextColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                } else {
                    members?.forEach { member ->
                        Box(
                            modifier =
                                Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        navController.navigate("profile/${member.userId}")
                                    }.width(100.dp)
                                    .height(100.dp)
                                    .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                    .background(ComponentBackgroundColor)
                                    .padding(10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                val profilePicture = member.profilePicture
                                profilePicture?.let {
                                    when (it.type) {
                                        "base64" -> {
                                            val decodedImage =
                                                it.image?.let { it1 ->
                                                    pictureConverter.convertBase64ToBitmap(
                                                        it1,
                                                    )
                                                }
                                            if (decodedImage != null) {
                                                Image(
                                                    bitmap = decodedImage.asImageBitmap(),
                                                    contentDescription = "Profile Picture",
                                                    modifier =
                                                        Modifier
                                                            .size(55.dp)
                                                            .clip(RoundedCornerShape(50)),
                                                )
                                            } else {
                                                Log.e("MembersSection", "Base64 decoding failed")
                                            }
                                        }

                                        "url" -> {
                                            Image(
                                                painter =
                                                    rememberAsyncImagePainter(
                                                        model = it.image,
                                                        imageLoader = imageLoader,
                                                    ),
                                                contentDescription = "Profile Picture",
                                                modifier =
                                                    Modifier
                                                        .size(55.dp)
                                                        .clip(RoundedCornerShape(50)),
                                            )
                                        }

                                        else -> {}
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = member.name.split(" ").first(),
                                    color = TextColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

                if (isInHousehold) {
                    if (household != null &&
                        household!!.type != "Single household" &&
                        (household!!.type != "Pair" || household!!.users.size < 2)
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                    .background(ComponentBackgroundColor)
                                    .then(
                                        if (!isStory) {
                                            Modifier.clickable {
                                                coroutineScope.launch {
                                                    inviteSheetState.show()
                                                }
                                            }
                                        } else {
                                            Modifier
                                        },
                                    ).padding(10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.invite),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = stringResource(R.string.invite),
                                    color = TextColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                .background(ComponentBackgroundColor)
                                .then(
                                    if (!isStory) {
                                        Modifier.clickable { showJoinHouseholdDialog.value = true }
                                    } else {
                                        Modifier
                                    },
                                ).padding(10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Image(
                                painter = painterResource(id = R.drawable.user_joined),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = stringResource(R.string.join),
                                color = TextColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            )
                        }
                    }

                    Box(
                        modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, ComponentStrokeColor, RoundedCornerShape(10.dp))
                                .background(ComponentBackgroundColor)
                                .then(
                                    if (!isStory) {
                                        Modifier.clickable { showCreateHouseholdDialog = true }
                                    } else {
                                        Modifier
                                    },
                                ).padding(10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Image(
                                painter = painterResource(id = R.drawable.plus),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = stringResource(R.string.create),
                                color = TextColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }
            }
        }

        if (showJoinHouseholdDialog.value) {
            AlertDialog(
                containerColor = ComponentBackgroundColor,
                title = { Text(stringResource(R.string.join_household)) },
                text = {
                    Column {
                        TextField(
                            value = householdId.value,
                            colors =
                                TextFieldDefaults.colors(
                                    focusedTextColor = TextColor,
                                    unfocusedTextColor = TextColor,
                                    focusedContainerColor = GreyColor,
                                    unfocusedContainerColor = GreyColor,
                                    focusedIndicatorColor = AccentTurquoiseColor,
                                    unfocusedIndicatorColor = Color.Transparent,
                                ),
                            onValueChange = { householdId.value = it },
                            placeholder = { Text(text = stringResource(R.string.household_id)) },
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showJoinHouseholdDialog.value = false },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = GreyColor,
                                contentColor = TextColor,
                            ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, ComponentStrokeColor),
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onJoinHouseholdClick(householdId.value)
                            showJoinHouseholdDialog.value = false
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = WhiteColor,
                                contentColor = GreyColor,
                            ),
                        enabled = householdId.value.length == 20,
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Text(text = stringResource(R.string.join))
                    }
                },
                onDismissRequest = { showJoinHouseholdDialog.value = false },
            )
        }

        if (showCreateHouseholdDialog) {
            AlertDialog(
                containerColor = ComponentBackgroundColor,
                title = { Text(stringResource(R.string.create_household)) },
                text = {
                    Column {
                        TextField(
                            value = householdName,
                            colors =
                                TextFieldDefaults.colors(
                                    focusedTextColor = TextColor,
                                    unfocusedTextColor = TextColor,
                                    focusedContainerColor = GreyColor,
                                    unfocusedContainerColor = GreyColor,
                                    focusedIndicatorColor = AccentTurquoiseColor,
                                    unfocusedIndicatorColor = Color.Transparent,
                                ),
                            onValueChange = { householdName = it },
                            placeholder = { Text(text = stringResource(R.string.household_name)) },
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showCreateHouseholdDialog = false },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = GreyColor,
                                contentColor = TextColor,
                            ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, ComponentStrokeColor),
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showCreateHouseholdDialog = false
                            showHouseholdTypeDialog = true
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = WhiteColor,
                                contentColor = GreyColor,
                            ),
                        enabled =
                            householdName.isNotEmpty() &&
                                householdName.all
                                    { it.isLetter() || it.isWhitespace() },
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Text(text = stringResource(R.string.create))
                    }
                },
                onDismissRequest = { showCreateHouseholdDialog = false },
            )
        }

        if (showHouseholdTypeDialog) {
            AlertDialog(
                containerColor = ComponentBackgroundColor,
                title = { Text(stringResource(R.string.select_household_type)) },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        householdTypeMap.forEach { (localName, englishType) ->
                            val borderColor =
                                if (householdType == englishType) {
                                    AccentTurquoiseColor
                                } else {
                                    Color.Transparent
                                }
                            Button(
                                onClick = { householdType = englishType },
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = GreyColor,
                                        contentColor = TextColor,
                                    ),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, borderColor),
                                modifier =
                                    Modifier.padding(vertical = 2.dp).align(
                                        Alignment.CenterHorizontally,
                                    ),
                            ) {
                                Text(text = stringResource(localName))
                            }
                        }
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showHouseholdTypeDialog = false },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = GreyColor,
                                contentColor = TextColor,
                            ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, ComponentStrokeColor),
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onCreateHouseholdClick(householdName, householdType)
                            showHouseholdTypeDialog = false
                            if (items.isNotEmpty()) {
                                showAddProductsDialog = true
                            }
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = WhiteColor,
                                contentColor = GreyColor,
                            ),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Text(text = stringResource(R.string.confirm))
                    }
                },
                onDismissRequest = { showHouseholdTypeDialog = false },
            )
        }
        if (showAddProductsDialog) {
            AlertDialog(
                containerColor = ComponentBackgroundColor,
                title = { Text(stringResource(R.string.add_products)) },
                text = {
                    Text(text = stringResource(R.string.add_products_warning))
                },
                dismissButton = {
                    Button(
                        onClick = {
                            onDeleteProducts()
                            showAddProductsDialog = false
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = GreyColor,
                                contentColor = TextColor,
                            ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, ComponentStrokeColor),
                    ) {
                        Text(text = stringResource(R.string.no))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onAddProducts()
                            showAddProductsDialog = false
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = AccentTurquoiseColor,
                                contentColor = GreyColor,
                            ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, ComponentStrokeColor),
                    ) {
                        Text(text = stringResource(R.string.yes))
                    }
                },
                onDismissRequest = { showAddProductsDialog = false },
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}
