package uk.ac.tees.mad.w9628563.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import uk.ac.tees.mad.w9628563.viewmodels.AuthViewModel


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val signInStatus = viewModel.loginStatus.collectAsState(initial = null)
    val signInState = viewModel.loginState.collectAsState().value
    val loginUiState = viewModel.loginUiState.collectAsState().value
    val focusManager = LocalFocusManager.current
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(text = "Welcome Back!", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Please enter your details to sign in.",
            fontSize = 17.sp,
            fontWeight = FontWeight.Light,
            lineHeight = 20.sp,
            color = Color.Gray,
            letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = loginUiState.email,
            onValueChange = {
                viewModel.updateLoginUiState(loginUiState.copy(email = it))
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Email")
            },
            maxLines = 1,
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Email, contentDescription = "")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            visualTransformation = VisualTransformation.None,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
        )
        HorizontalDivider()
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = loginUiState.password,
            onValueChange = {
                viewModel.updateLoginUiState(loginUiState.copy(password = it))
            },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Lock, contentDescription = "")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            label = {
                Text(text = "Password")
            },
            trailingIcon = {
                val icon = if (isPasswordVisible)
                    Icons.Default.Visibility
                else Icons.Filled.VisibilityOff

                val description =
                    if (isPasswordVisible) "Hide password" else "Show password"

                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = icon,
                        description,
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            })
        )
        HorizontalDivider()

        Spacer(modifier = Modifier.height(80.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
                .height(65.dp)
                .clickable {
                    viewModel.authenticateUser(loginUiState.email, loginUiState.password)
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (signInStatus.value?.isLoading == true) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text(text = "Login", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Don't have an account? SIGN UP",
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable {
                onSignUpClick()
            }
        )


        LaunchedEffect(key1 = signInStatus.value?.isSuccess) {
            coroutineScope.launch {
                if (signInStatus.value?.isSuccess?.isNotEmpty() == true) {
                    focusManager.clearFocus()
                    val successMessage = signInStatus.value?.isSuccess
                    Toast.makeText(context, "$successMessage", Toast.LENGTH_LONG).show()
                    onLoginSuccess()
                }
            }
        }

        LaunchedEffect(key1 = signInStatus.value?.isError) {
            coroutineScope.launch {
                if (signInStatus.value?.isError?.isNotEmpty() == true) {
                    val errorMessage = signInStatus.value?.isError
                    Toast.makeText(context, "$errorMessage", Toast.LENGTH_LONG).show()
                }
            }
        }
        LaunchedEffect(key1 = signInState.signInError) {
            coroutineScope.launch {
                if (signInState.signInError?.isNotEmpty() == true) {
                    val errorMessage = signInState.signInError
                    Toast.makeText(context, "$errorMessage", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}