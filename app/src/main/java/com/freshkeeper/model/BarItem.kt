package com.freshkeeper.model

data class BarItem(
    val title: String,
    val selectedIcon: Icon,
    val unselectedIcon: Icon,
    val hasNews: Boolean,
    val badgeCount: Int? = null,
    val route: String,
)
