package com.renekakpo.hivechat.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.renekakpo.hivechat.R
import com.renekakpo.hivechat.app.HiveChatApp
import com.renekakpo.hivechat.navigation.NavDestination
import com.renekakpo.hivechat.services.MQTTForegroundService
import com.renekakpo.hivechat.ui.theme.HiveChatTheme
import com.renekakpo.hivechat.utils.showMessage
import com.renekakpo.hivechat.viewModels.SignInUiState
import com.renekakpo.hivechat.viewModels.SignInViewModel
import com.renekakpo.hivechat.viewModels.provider.AppViewModelProvider
import kotlinx.coroutines.*

object SignInScreen : NavDestination {
    override val route: String = "sign_in_screen"
}

@Composable
fun SignInScreen(
    navController: NavHostController,
    viewModel: SignInViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("Test_1") }
    var password by remember { mutableStateOf("hiveMq321") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisibilityState by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
            .padding(horizontal = 45.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(2.dp),
            text = stringResource(R.string.signin_screen_title_text),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            modifier = Modifier.padding(2.dp),
            text = stringResource(R.string.signin_screen_subtitle_text),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            singleLine = true,
            onValueChange = { username = it },
            label = {
                Text(
                    text = stringResource(R.string.username_text),
                    color = MaterialTheme.colors.onBackground
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            placeholder = {
                Text(
                    text = stringResource(R.string.username_field_placeholder),
                    color = Color.LightGray
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.primary,
                unfocusedBorderColor = MaterialTheme.colors.onBackground.copy(alpha = 0.2f)
            ),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            singleLine = true,
            onValueChange = { password = it },
            label = {
                Text(
                    text = stringResource(R.string.password_text),
                    color = MaterialTheme.colors.onBackground
                )
            },
            visualTransformation = if (passwordVisibilityState) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            placeholder = {
                Text(
                    text = stringResource(R.string.password_field_placeholder),
                    color = Color.LightGray
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        passwordVisibilityState = !passwordVisibilityState
                    }
                ) {
                    Icon(
                        imageVector = if (passwordVisibilityState) {
                            Icons.Rounded.Visibility
                        } else {
                            Icons.Rounded.VisibilityOff
                        },
                        contentDescription = stringResource(R.string.password_toggle_button_desc),
                        tint = MaterialTheme.colors.primary
                    )
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.primary,
                unfocusedBorderColor = MaterialTheme.colors.onBackground.copy(alpha = 0.2f)
            ),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (username.isEmpty()) {
                    showMessage(context, "Username should not be empty!")
                } else if (password.isEmpty()) {
                    showMessage(context, "Password should not be empty!")
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.connectHiveClientToBroker(username, password)
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .height(height = if (isLoading) 65.dp else 50.dp)
                .align(alignment = Alignment.CenterHorizontally),
            shape = if (isLoading) CircleShape else MaterialTheme.shapes.medium
        ) {
            when (val signInUiState = viewModel.signInUiState) {
                is SignInUiState.Loading -> {
                    isLoading = true

                    CircularProgressIndicator(
                        modifier = Modifier.size(35.dp),
                        color = MaterialTheme.colors.primary
                    )
                }
                is SignInUiState.Error -> {
                    isLoading = false

                    showMessage(context, signInUiState.message)

                    Text(
                        text = stringResource(R.string.signin_text),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is SignInUiState.Success -> {

                    Text(
                        text = stringResource(R.string.signin_text),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    navController.navigate(route = ChatListScreen.route)

                    isLoading = false
                }
                else -> {
                    isLoading = false

                    Text(
                        text = stringResource(R.string.signin_text),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun startHiveMQService() {
    val hiveMQService = Intent(HiveChatApp.appContext, MQTTForegroundService::class.java)
    HiveChatApp.appContext.startService(hiveMQService)
}

@Preview
@Composable
fun SignInScreenPreview() {
    HiveChatTheme {
        SignInScreen(navController = rememberNavController())
    }
}