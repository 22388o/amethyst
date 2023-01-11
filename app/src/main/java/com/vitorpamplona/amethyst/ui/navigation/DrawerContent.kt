package com.vitorpamplona.amethyst.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.vitorpamplona.amethyst.R
import com.vitorpamplona.amethyst.model.User
import com.vitorpamplona.amethyst.ui.screen.AccountStateViewModel
import com.vitorpamplona.amethyst.ui.screen.loggedIn.AccountViewModel
import kotlinx.coroutines.launch

val bottomNavigations = listOf(
    Route.Profile,
    Route.Lists,
    //Route.Topics,
    Route.Bookmarks,
    //Route.Moments
)

@Composable
fun DrawerContent(navController: NavHostController,
                  scaffoldState: ScaffoldState,
                  accountViewModel: AccountViewModel,
                  accountStateViewModel: AccountStateViewModel) {

    val accountUserState by accountViewModel.userLiveData.observeAsState()
    val accountUser = accountUserState?.user

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colors.background
    ) {
        Column() {
            Box {
                Image(
                    painter = painterResource(R.drawable.profile_banner),
                    contentDescription = "Profile Banner",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )

                ProfileContent(
                    accountUser,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                        .padding(top = 125.dp)
                )
            }
            Divider(
                thickness = 0.25.dp,
                modifier = Modifier.padding(top = 20.dp)
            )
            ListContent(
                navController,
                scaffoldState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                accountStateViewModel
            )
        }
    }
}

@Composable
fun ProfileContent(accountUser: User?, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        AsyncImage(
            model = accountUser?.profilePicture() ?: "https://robohash.org/ohno.png",
            contentDescription = "Profile Image",
            modifier = Modifier
                .width(100.dp)
                .clip(shape = CircleShape)
                .border(3.dp, MaterialTheme.colors.background, CircleShape)
                .background(MaterialTheme.colors.background)
        )
        Text(
            accountUser?.bestDisplayName() ?: "",
            modifier = Modifier.padding(top = 7.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(" @${accountUser?.bestUsername()}", color = Color.LightGray)
        Row(modifier = Modifier.padding(top = 15.dp)) {
            Row() {
                Text("${accountUser?.follows?.size}", fontWeight = FontWeight.Bold)
                Text(" Following")
            }
            Row(modifier = Modifier.padding(start = 10.dp)) {
                Text("${accountUser?.follower ?: "--"}", fontWeight = FontWeight.Bold)
                Text(" Followers")
            }
        }
    }
}

@Composable
fun ListContent(
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    modifier: Modifier,
    accountViewModel: AccountStateViewModel
) {
    Column(
        modifier = modifier
    ) {
        LazyColumn() {
            items(items = bottomNavigations) {
                NavigationRow(navController, scaffoldState, it)
            }
            item {
                Divider(
                    modifier = Modifier.padding(vertical = 15.dp),
                    thickness = 0.25.dp
                )
                Column(modifier = modifier.padding(horizontal = 25.dp)) {
                    Text(
                        text = "Settings",
                        fontSize = 18.sp,
                        fontWeight = W500
                    )
                    Row(
                        modifier = Modifier.clickable(onClick = { accountViewModel.logOff() }),
                    ) {
                        Text(
                            text = "Log out",
                            modifier = Modifier.padding(vertical = 15.dp),
                            fontSize = 18.sp,
                            fontWeight = W500
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationRow(navController: NavHostController, scaffoldState: ScaffoldState, route: Route) {
    val coroutineScope = rememberCoroutineScope()
    val currentRoute = currentRoute(navController)
    Row(
        modifier = Modifier
            .padding(vertical = 15.dp, horizontal = 25.dp)
            .clickable(onClick = {
                if (currentRoute != route.route) {
                    navController.navigate(route.route)
                }
                coroutineScope.launch {
                    scaffoldState.drawerState.close()
                }
            }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(route.icon), null,
            modifier = Modifier.size(22.dp),
            tint = MaterialTheme.colors.primary
        )
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = route.route,
            fontSize = 18.sp,
        )
    }
}