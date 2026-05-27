package com.example.radiation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.radiation.navigation.Screen
import com.example.radiation.navigation.TopLevelRoute
import com.example.radiation.ui.theme.PrimaryBlueLight
import com.example.radiation.ui.theme.OnSurfaceMuted
import com.example.radiation.ui.theme.Surface
import com.example.radiation.ui.theme.BorderColor

@Composable
fun RadBottomNavigationBar(
    modifier: Modifier = Modifier,
    currentDestination: NavDestination?,
    items: List<TopLevelRoute<out Screen.Main>>,
    onItemSelect: (Screen.Main) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Surface)
            .border(1.dp, BorderColor)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { topLevelRoute ->
            RadNavigationBarItem(
                item = topLevelRoute,
                isSelected = currentDestination?.hierarchy?.any {
                    it.hasRoute(
                        route = topLevelRoute.route::class
                    )
                } == true,
                onItemSelect = { onItemSelect(topLevelRoute.route) }
            )
        }
    }
}

@Composable
fun RadNavigationBarItem(
    item: TopLevelRoute<out Screen.Main>,
    isSelected: Boolean = false,
    onItemSelect: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onItemSelect() }
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(26.dp),
            painter = painterResource(if (isSelected) item.selectedIcon else item.unselectedIcon),
            contentDescription = null,
            tint = if (isSelected) PrimaryBlueLight else OnSurfaceMuted
        )
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = stringResource(item.title),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PrimaryBlueLight else OnSurfaceMuted
        )
    }
}
