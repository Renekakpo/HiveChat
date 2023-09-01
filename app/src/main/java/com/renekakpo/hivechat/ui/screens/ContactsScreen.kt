package com.renekakpo.hivechat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.rounded.Group
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.Gson
import com.renekakpo.hivechat.R
import com.renekakpo.hivechat.app.HiveChatApp.Companion.mqClientId
import com.renekakpo.hivechat.models.User
import com.renekakpo.hivechat.navigation.NavDestination
import com.renekakpo.hivechat.utils.showMessage
import com.renekakpo.hivechat.viewModels.ContactsUiState
import com.renekakpo.hivechat.viewModels.ContactsViewModel
import com.renekakpo.hivechat.viewModels.provider.AppViewModelProvider

object ContactsScreen : NavDestination {
    override val route: String = "contacts_screen"
}

@Composable
fun ContactsScreen(
    navController: NavController,
    viewModel: ContactsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var contactsCount by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                elevation = 4.dp,
                title = {
                    Column {
                        Text(
                            text = "Contacts",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.body1,
                            textAlign = TextAlign.Start
                        )

                        Text(
                            text = "$contactsCount contact(s)",
                            style = MaterialTheme.typography.caption,
                            textAlign = TextAlign.Start
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIos,
                            contentDescription = "Back button"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val contactsUiState = viewModel.contactsUiState) {
            is ContactsUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(35.dp),
                        color = MaterialTheme.colors.primary
                    )
                }
            }

            is ContactsUiState.Success -> {
                contactsCount = contactsUiState.data.size

                ContactsScreenContent(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = viewModel,
                    navController = navController,
                    contacts = contactsUiState.data
                )
            }

            is ContactsUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = contactsUiState.message,
                        textAlign = TextAlign.Justify,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colors.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun ContactsScreenContent(
    modifier: Modifier,
    viewModel: ContactsViewModel,
    navController: NavController,
    contacts: List<User>
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
            .background(color = MaterialTheme.colors.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showMessage(context, "Create chat group coming soon") }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(5.dp)
                    .clip(shape = MaterialTheme.shapes.large)
                    .background(color = MaterialTheme.colors.primary)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Group,
                    contentDescription = "New group icon",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(7.dp),
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            Text(
                text = "New group",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Justify,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Contacts on HiveChat",
            fontSize = 15.sp,
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Justify,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(contacts) { _, item ->
                val profileInfo = if (item.uniqueIdentifier == mqClientId) {
                    "Message yourself"
                } else {
                    null
                }
                
                ContactItems(
                    contact = item,
                    profileComment = profileInfo,
                    onItemClick = {
                        val jsonChatItemArg = Gson().toJson(item.copy(info = profileInfo))
                        val chatUuidArg = " "
                        val chatItemExists = false

                        navController.navigate(
                            route = "${ChatScreen.route}/$jsonChatItemArg/$chatUuidArg/$chatItemExists"
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun ContactItems(contact: User, profileComment: String?, onItemClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color = MaterialTheme.colors.background)
            .clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(contact.profilePictureUrl)
                .crossfade(true)
                .build(),
            contentDescription = "chat profile picture",
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.user_profile_placeholder),
            placeholder = painterResource(id = R.drawable.user_profile_placeholder),
            modifier = Modifier
                .weight(1f)
                .size(45.dp)
                .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = true)
                .clip(shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(5f)
        ) {
            Text(
                text = if (profileComment.isNullOrEmpty())
                    contact.username
                else
                    "${contact.username} (You)",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = profileComment ?: contact.uniqueIdentifier,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}