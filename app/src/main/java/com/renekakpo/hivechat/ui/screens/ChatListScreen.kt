package com.renekakpo.hivechat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.renekakpo.hivechat.R
import com.renekakpo.hivechat.app.HiveChatApp.Companion.mqClientId
import com.renekakpo.hivechat.models.ChatItemUiState
import com.renekakpo.hivechat.models.Message
import com.renekakpo.hivechat.navigation.NavDestination
import com.renekakpo.hivechat.utils.Constants.INDIVIDUAL_CHAT
import com.renekakpo.hivechat.utils.getTimeFromMilliseconds
import com.renekakpo.hivechat.viewModels.ChatListScreenViewModel
import com.renekakpo.hivechat.viewModels.provider.AppViewModelProvider

object ChatListScreen : NavDestination {
    override val route: String = "chatList_screen"
}

@Composable
fun ChatListScreen(
    navController: NavHostController,
    viewModel: ChatListScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val chatItemsUiState by viewModel.chatItemsUiState.collectAsState()
    val chatActionUiState by viewModel.chatActionState.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
    ) {
        Text(
            text = stringResource(R.string.chat_list_screen_title),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp),
        )

        if (chatItemsUiState.isEmpty()) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.empty_chats_text),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 15.dp),
            )
        } else {
            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(chatItemsUiState) { _, item ->
                    ChatListItems(
                        chatItemUiState = item,
                        onItemClick = {
                            val itemToJsonArg = " "
                            val itemUUIDArg = item.chat.uniqueIdentifier
                            val itemExistsArg = true

                            navController.navigate(
                                route = "${ChatScreen.route}/$itemToJsonArg/$itemUUIDArg/$itemExistsArg"
                            )
                        },
                        chatActionUiState = chatActionUiState
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        FloatingActionButton(
            onClick = {
                navController.navigate(route = ContactsScreen.route)
            },
            backgroundColor = MaterialTheme.colors.primary,
            modifier = Modifier
                .align(alignment = Alignment.End)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ChatBubbleOutline,
                contentDescription = stringResource(R.string.new_chat_text)
            )
        }
    }

    LaunchedEffect(true) {
        viewModel.initMqClientConnection()
    }
}

@Composable
fun ChatListItems(
    chatItemUiState: ChatItemUiState,
    onItemClick: () -> Unit,
    chatActionUiState: Message?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.background)
            .clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(
                    if (chatItemUiState.chat.type == INDIVIDUAL_CHAT)
                        chatItemUiState.chat.participants.first { it.uniqueIdentifier != mqClientId }.profilePictureUrl
                    else
                        "${chatItemUiState.chat.profilePictureUrl}"
                )
                .crossfade(true)
                .build(),
            contentDescription = "chat profile picture",
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.user_profile_placeholder),
            placeholder = painterResource(id = R.drawable.user_profile_placeholder),
            modifier = Modifier
                .weight(1f)
                .size(50.dp)
                .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = true)
                .clip(shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(4f)
        ) {
            Text(
                text = if (chatItemUiState.chat.type == INDIVIDUAL_CHAT)
                    chatItemUiState.chat.participants.first { it.uniqueIdentifier != mqClientId }.username
                else
                    "${chatItemUiState.chat.name}",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (chatItemUiState.lastMessage?.sender?.uniqueIdentifier == mqClientId) {
                    Icon(
                        imageVector = if (chatItemUiState.lastMessage.sentTime != null && chatItemUiState.lastMessage.deliveredTime != null) {
                            Icons.Filled.DoneAll
                        } else if (chatItemUiState.lastMessage.sentTime != null) {
                            Icons.Filled.Done
                        } else {
                            Icons.Filled.AccessTime
                        },
                        contentDescription = "Message status",
                        tint = if (chatItemUiState.lastMessage.isRead) {
                            MaterialTheme.colors.secondary
                        } else {
                            MaterialTheme.colors.onBackground.copy(alpha = 0.3f)
                        },
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 5.dp)
                            .align(alignment = Alignment.Bottom)
                    )
                }

                Text(
                    text = "${chatActionUiState?.content}".ifEmpty {
                        chatItemUiState.lastMessage?.content ?: "No messages available"
                    },
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(5.dp))

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            val itemTime =
                if (chatItemUiState.lastMessage?.sender?.uniqueIdentifier == mqClientId) {
                    chatItemUiState.lastMessage.timestamp
                } else {
                    chatItemUiState.lastMessage?.deliveredTime
                }

            Text(
                text = getTimeFromMilliseconds(itemTime),
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.caption,
                color = if (chatItemUiState.unreadCount > 0) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.size(5.dp))

            if (chatItemUiState.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(if (chatItemUiState.unreadCount < 100) 25.dp else 30.dp)
                        .background(
                            color = MaterialTheme.colors.primary.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.large
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val count = if (chatItemUiState.unreadCount < 100)
                        "${chatItemUiState.unreadCount}"
                    else
                        stringResource(R.string.three_digits_count_text)

                    Text(
                        modifier = Modifier.padding(3.dp),
                        text = count,
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }

    }
}