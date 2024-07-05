package uk.ac.tees.mad.w9628563.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.w9628563.domain.Resource
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    private val _loginStatus = Channel<LoginStatus>()
    val loginStatus = _loginStatus.receiveAsFlow()

    private val _signUpUiState = MutableStateFlow(SignUpUiState())
    val signUpUiState = _signUpUiState.asStateFlow()

    private val _registerState = Channel<RegisterState>()
    val registerState = _registerState.receiveAsFlow()

    fun resetLoginState() {
        _loginState.update { LoginState() }
    }

    fun updateLoginUiState(value: LoginUiState) {
        _loginUiState.value = value
    }

    fun authenticateUser(email: String, password: String) = viewModelScope.launch {
        signIn(email, password).collect { result ->
            when (result) {
                is Resource.Error -> {
                    _loginStatus.send(LoginStatus(isError = result.message))
                }

                is Resource.Loading -> {
                    _loginStatus.send(LoginStatus(isLoading = true))
                }

                is Resource.Success -> {
                    _loginStatus.send(LoginStatus(isSuccess = "Login Successful"))
                }
            }
        }
    }

    var currentUsername = mutableStateOf("Guest")
    private val userId = firebaseAuth.currentUser?.uid

    init {
        fetchUserDetails()
    }

    private fun fetchUserDetails() {
        viewModelScope.launch {
            userId?.let { uid ->
                firestore.collection("users").document(uid).get()
                    .addOnSuccessListener { snapshot ->
                        Log.d("USER_DETAILS", "$snapshot")

                        if (snapshot.exists()) {
                            snapshot.data?.let { data ->
                                data["username"]?.let { username ->
                                    currentUsername.value = username.toString()
                                }
                            }
                        } else {
                            Log.d("USER_DETAILS", "No data found in Database")
                        }
                    }.addOnFailureListener { exception ->
                        Log.e("USER_DETAILS", "Error fetching user details", exception)
                    }
            }
        }
    }

    fun updateSignUpUiState(value: SignUpUiState) {
        _signUpUiState.value = value
    }

    fun registerNewUser(email: String, password: String, username: String) = viewModelScope.launch {
        signUp(email, password, username).collect { result ->
            when (result) {
                is Resource.Error -> {
                    _registerState.send(RegisterState(isError = result.message))
                }

                is Resource.Loading -> {
                    _registerState.send(RegisterState(isLoading = true))
                }

                is Resource.Success -> {
                    _registerState.send(RegisterState(isSuccess = "Registration Successful"))
                }
            }
        }
    }


    private fun signIn(email: String, password: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success(authResult))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }

    fun signUp(email: String, password: String, username: String): Flow<Resource<AuthResult>> = flow {
        emit(Resource.Loading())
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid
            storeUser(email = email, username = username, userId = userId)
            emit(Resource.Success(authResult))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }
    private suspend fun storeUser(email: String?, username: String?, userId: String?) {
        userId?.let {
            val userMap = mapOf(
                "email" to email,
                "username" to username,
                "profileImage" to "https://example.com/default-profile.png"
                // Add other user data if needed
            )
            firestore.collection("users").document(it).set(userMap).await()
        }
    }

}

data class LoginUiState(
    val email: String = "",
    val password: String = ""
)

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = ""
)

data class LoginState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)

data class RegisterState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)

data class LoginStatus(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)
