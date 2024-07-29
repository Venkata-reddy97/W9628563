package uk.ac.tees.mad.w9628563.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class UserProfile(
    val email: String = "",
    val username: String = "",
    val profileImage: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                try {
                    val document = firestore.collection("users").document(userId).get().await()
                    val userProfile = document.toObject(UserProfile::class.java)
                    _userProfile.value = userProfile
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        _userProfile.value = null
    }
}