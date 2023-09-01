package com.renekakpo.hivechat.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.renekakpo.hivechat.R
import com.renekakpo.hivechat.app.HiveChatApp.Companion.mqClientId
import com.renekakpo.hivechat.models.Chat
import com.renekakpo.hivechat.models.Message
import com.renekakpo.hivechat.navigation.NavDestination
import com.renekakpo.hivechat.utils.Constants.INDIVIDUAL_CHAT
import com.renekakpo.hivechat.utils.Constants.INFO_MESSAGE
import com.renekakpo.hivechat.utils.getTimeFromMilliseconds
import com.renekakpo.hivechat.viewModels.ChatViewModel
import com.renekakpo.hivechat.viewModels.MessagesUiState
import com.renekakpo.hivechat.viewModels.provider.AppViewModelProvider
import kotlin.math.roundToInt

object ChatScreen : NavDestination {
    override val route: String = "chat_screen"
    const val itemToJsonArg = "itemToJson"
    const val itemUUIDArg = "itemUUID"
    const val itemExistsArg = "itemExists"
    val routeWithArgs = "$route/{$itemToJsonArg}/{$itemUUIDArg}/{$itemExistsArg}"
}

@Composable
fun ChatScreen(
    navController: NavHostController,
    viewModel: ChatViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val chatState by viewModel.chatUiState.collectAsState()
    val messages by viewModel.messagesUiState.collectAsState(initial = MessagesUiState())
    val chatActionUiState by viewModel.chatActionState.collectAsState(initial = null)

    Scaffold(
        topBar = {
            ChatUiTopAppBar(
                chatItem = chatState,
                navController = navController,
                chatActionUiState = chatActionUiState
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        scaffoldState = rememberScaffoldState()
    ) { innerPadding ->
        ChatUiContent(
            modifier = Modifier.padding(innerPadding),
            messages = messages.data,
            onSendButtonClick = {
                chatState?.let { chat ->
                    // Save the message to the local database
                    viewModel.saveMessage(
                        userInput = it,
                        chatId = chat.uniqueIdentifier
                    )
                }
            },
            onUserInputChanged = { userInput ->
                viewModel.notifyDestOnUserInput(userInput)
            }
        )
    }

    LaunchedEffect(viewModel.chatItemExists) {
        viewModel.initOrReadChat()
    }
}

@Composable
fun ChatUiTopAppBar(
    chatItem: Chat?,
    navController: NavHostController,
    chatActionUiState: Message?
) {
    var chatInfo by remember { mutableStateOf("") }

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 4.dp,
        title = {
            Row(modifier = Modifier.offset(x = (-25).dp)) {
                chatItem?.let { chat ->
                    val chatProfileUrl = when (chat.type) {
                        INDIVIDUAL_CHAT -> chat.participants.firstOrNull { it -> it.uniqueIdentifier != mqClientId }?.profilePictureUrl
                        else -> chat.profilePictureUrl
                    } ?: ""

                    val chatName = when (chat.type) {
                        INDIVIDUAL_CHAT -> chat.participants.firstOrNull { it -> it.uniqueIdentifier != mqClientId }?.username
                        else -> chat.name
                    } ?: ""

                    chatInfo = when (chat.type) {
                        INDIVIDUAL_CHAT -> chat.participants.firstOrNull { it -> it.uniqueIdentifier != mqClientId }?.info
                        else -> chat.description
                    } ?: ""

                    AsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(chatProfileUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "chat profile picture",
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.user_profile_placeholder),
                        placeholder = painterResource(id = R.drawable.user_profile_placeholder),
                        modifier = Modifier
                            .size(40.dp)
                            .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = true)
                            .clip(shape = CircleShape)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Column {
                        Text(
                            text = chatName,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = chatInfo,
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.navigate(route = ChatListScreen.route) {
                    popUpTo(route = ChatListScreen.route) {
                        inclusive = true
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIos,
                    contentDescription = "Back button"
                )
            }
        }
    )

    LaunchedEffect(chatActionUiState) {
        chatInfo = chatActionUiState?.content ?: ""
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatUiContent(
    modifier: Modifier,
    messages: List<Message>,
    onSendButtonClick: (String) -> Unit,
    onUserInputChanged: (String) -> Unit
) {

    Column(
        modifier = modifier
            .background(color = MaterialTheme.colors.onBackground.copy(alpha = 0.05f))
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.weight(1f)) {
            CompositionLocalProvider(
                LocalOverscrollConfiguration provides null
            ) {
                LazyColumn(
                    modifier = Modifier
                        .background(color = Color.Transparent)
                        .fillMaxSize()
                        .padding(2.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    itemsIndexed(messages) { _, item ->
                        ChatItems(item)
                    }
                }
            }
        }

        ChatUiBottomContent(onSendButtonClick = onSendButtonClick, onUserInputChanged = onUserInputChanged)
    }
}

@Composable
fun ChatUiBottomContent(onSendButtonClick: (String) -> Unit, onUserInputChanged: (String) -> Unit) {
    var userInput by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .background(color = Color.Transparent)
            .padding(start = 15.dp, end = 15.dp, bottom = 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        TextField(
            value = userInput,
            onValueChange = {
                userInput = it
                onUserInputChanged(it)
            },
            singleLine = false,
            placeholder = {
                Text(
                    text = "Message",
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
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
                .heightIn(min = 20.dp, max = 80.dp)
                .weight(1f)
        )

        Button(
            onClick = {
                onSendButtonClick(userInput)
                // Clean user input text field
                userInput = ""
            },
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            elevation = ButtonDefaults.elevation(2.dp),
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
                contentDescription = "Send message",
                modifier = Modifier.size(30.dp)
            )

        }
    }
}

@Composable
fun ChatItems(item: Message) {
    val bubbleColor =
        if (item.sender.uniqueIdentifier == mqClientId) MaterialTheme.colors.primaryVariant.copy(
            alpha = 0.5f
        ) else MaterialTheme.colors.background
    val bubbleContentColor =
        if (item.sender.uniqueIdentifier == mqClientId) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onBackground
    val horizontalArrangement =
        if (item.sender.uniqueIdentifier == mqClientId) Arrangement.End else Arrangement.Start
    val horizontalPadding = if (item.sender.uniqueIdentifier == mqClientId) 5.dp else 10.dp
    val verticalPadding = if (item.sender.uniqueIdentifier == mqClientId) 5.dp else 5.dp
    val formattedTime = getTimeFromMilliseconds(
        if (item.sender.uniqueIdentifier == mqClientId) {
            item.timestamp
        } else {
            item.deliveredTime
        }
    )

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

                if (item.sender.uniqueIdentifier == mqClientId) {
                    Icon(
                        imageVector = if (item.sentTime != null && item.deliveredTime != null) {
                            Icons.Filled.DoneAll
                        } else if (item.sentTime != null) {
                            Icons.Filled.Done
                        } else {
                            Icons.Filled.AccessTime
                        },
                        contentDescription = "Message status",
                        tint = if (item.isRead) {
                            MaterialTheme.colors.secondary
                        } else {
                            bubbleContentColor
                        },
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