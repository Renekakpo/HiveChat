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
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.renekakpo.hivechat.R
import com.renekakpo.hivechat.models.Chat
import com.renekakpo.hivechat.models.emptyMockChatList
import com.renekakpo.hivechat.models.mockChatList
import com.renekakpo.hivechat.navigation.NavDestination
import com.renekakpo.hivechat.utils.getTimeFromMilliseconds

object ChatListScreen : NavDestination {
    override val route: String = "chatList_screen"
}

@Composable
fun ChatListScreen(navController: NavHostController) {
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

        if (mockChatList.isEmpty()) {
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
                itemsIndexed(mockChatList) { _, item ->
                    ChatListItems(
                        chat = item,
                        onItemClick = {}
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        FloatingActionButton(
            onClick = {
                // Handle new chat button click here
            },
            backgroundColor = MaterialTheme.colors.primary,
            modifier = Modifier
                .align(alignment = Alignment.End)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Filled.ChatBubbleOutline, contentDescription = stringResource(R.string.new_chat_text))
        }
    }
}

@Composable
fun ChatListItems(
    chat: Chat,
    onItemClick: () -> Unit
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
                .data(chat.profilePictureUrl)
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
                text = chat.name,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = chat.lastMessage.content,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(5.dp))

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(
                text = getTimeFromMilliseconds(
                    chat.lastMessage.deliveredTime ?: System.currentTimeMillis()
                ),
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.caption,
                color = if (chat.unreadCount > 0) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.size(5.dp))

            if (chat.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(if (chat.unreadCount < 100) 25.dp else 30.dp)
                        .background(
                            color = MaterialTheme.colors.primary.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.large
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val count = if (chat.unreadCount < 100)
                        "${chat.unreadCount}"
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