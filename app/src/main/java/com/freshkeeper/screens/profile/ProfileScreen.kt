package com.freshkeeper.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.freshkeeper.R
import com.freshkeeper.navigation.BottomNavigationBar
import com.freshkeeper.screens.notifications.viewmodel.NotificationsViewModel
import com.freshkeeper.screens.profile.viewmodel.ProfileViewModel
import com.freshkeeper.ui.theme.BottomNavBackgroundColor
import com.freshkeeper.ui.theme.FreshKeeperTheme
import com.freshkeeper.ui.theme.TextColor
import kotlinx.coroutines.launch

@Suppress("ktlint:standard:function-naming")
@Composable
fun ProfileScreen(
    navController: NavHostController,
    userId: String,
) {
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()

    profileViewModel.getUserProfile(userId)

    val user = profileViewModel.user.collectAsState().value

    FreshKeeperTheme {
        Scaffold(
            bottomBar = {
                Box(
                    modifier =
                        Modifier
                            .background(BottomNavBackgroundColor)
                            .padding(horizontal = 10.dp),
                ) {
                    BottomNavigationBar(selectedIndex = 2, navController, notificationsViewModel)
                }
            },
        ) { it ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.profile),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor,
                    modifier = Modifier.padding(16.dp).weight(1f),
                )
                if (user?.id == userId) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        modifier =
                            Modifier.padding(top = 17.dp, end = 17.dp).size(25.dp).clickable {
                                coroutineScope.launch {
                                    navController.navigate("profileSettings")
                                }
                            },
                    )
                }
            }
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(top = 45.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(it),
                    ) {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            user?.let {
                                ProfileCard(
                                    name = it.displayName,
                                    profilePicture =
                                        profileViewModel.profilePicture.collectAsState().value,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
