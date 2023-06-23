package com.renekakpo.hivechat.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.renekakpo.hivechat.R
import com.renekakpo.hivechat.models.Message
import com.renekakpo.hivechat.models.mockMessages
import com.renekakpo.hivechat.navigation.NavDestination
import com.renekakpo.hivechat.utils.getTimeFromMilliseconds


object ChatScreen : NavDestination {
    override val route: String = "chat_screen"
}

@Composable
fun ChatScreen(navController: NavHostController) {
    val messages = mockMessages
    var userInput by remember { mutableStateOf("") }

    Scaffold(topBar = {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = {
                    navController.popBackStack(
                        route = ChatListScreen.route,
                        inclusive = true
                    )
                }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBackIos,
                        tint = MaterialTheme.colors.onPrimary,
                        contentDescription = "Back"
                    )
                }

                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data("")
                        .crossfade(true)
                        .build(),
                    contentDescription = "chat profile picture",
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.user_profile_placeholder),
                    placeholder = painterResource(id = R.drawable.user_profile_placeholder),
                    modifier = Modifier
                        .weight(1f)
                        .size(40.dp)
                        .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = true)
                        .clip(shape = CircleShape)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Column(
                    modifier = Modifier.weight(5f)
                ) {
                    Text(
                        text = "chat.name",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "lastMessage",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onPrimary.copy(alpha = 0.3f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colors.onBackground.copy(alpha = 0.05f))
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(5.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(messages) { _, item ->
                    ChatItems(item)
                }
            }

            Row(
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .padding(start = 15.dp, end = 15.dp, bottom = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    singleLine = false,
                    placeholder = {
                        Text(
                            text = "Message",
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = MaterialTheme.colors.onBackground,
                        disabledTextColor = Color.Transparent,
                        backgroundColor = MaterialTheme.colors.background,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .padding(start = 0.dp, end = 0.dp)
                        .heightIn(min = 30.dp, max = 80.dp)
                        .weight(1f)
                )

                Button(
                    onClick = {  /*Send message in the chat*/ },
                    modifier = Modifier
                        .size(45.dp),
                    shape = CircleShape,
                    elevation = ButtonDefaults.elevation(5.dp),
                    contentPadding = PaddingValues(0.dp),
                    border = BorderStroke(width = 0.dp, color = MaterialTheme.colors.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        tint = MaterialTheme.colors.onPrimary,
                        contentDescription = "Send message"
                    )

                }
            }
        }
    }

}

@Composable
fun ChatItems(item: Message) {
    val bubbleColor =
        if (item.sender.username == "John") MaterialTheme.colors.primary.copy(alpha = 0.2f) else MaterialTheme.colors.background
    val bubbleContentColor =
        if (item.sender.username == "John") MaterialTheme.colors.onPrimary else MaterialTheme.colors.onBackground
    val horizontalArrangement =
        if (item.sender.username == "John") Arrangement.End else Arrangement.Start
    val horizontalPadding = if (item.sender.username == "John") 5.dp else 10.dp
    val verticalPadding = if (item.sender.username == "John") 5.dp else 5.dp
    val formattedTime = getTimeFromMilliseconds(item.deliveredTime)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        Surface(
            elevation = 2.dp,
            shape = MaterialTheme.shapes.small
        ) {
            Row(
                modifier = Modifier
                    .background(color = bubbleColor)
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            ) {
                Text(
                    text = item.content,
                    color = bubbleContentColor,
                    style = MaterialTheme.typography.body2,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 200.dp)
                )

                Spacer(modifier = Modifier.width(15.dp))

                Text(
                    text = formattedTime,
                    color = bubbleContentColor,
                    style = MaterialTheme.typography.caption.copy(fontSize = 11.sp),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(alignment = Alignment.Bottom)
                )

                if (item.sender.username == "John") {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Message status",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .size(27.dp)
                            .padding(top = 10.dp)
                            .align(alignment = Alignment.Bottom)
                    )
                }
            }
        }
    }
}