package uk.ac.tees.mad.w9628563.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.w9628563.database.AppDatabase
import uk.ac.tees.mad.w9628563.database.FavoriteBook
import javax.inject.Inject

@HiltViewModel
class FavoriteBookViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    private val favoriteBookDao = AppDatabase.getDatabase(application).favoriteBookDao()

    val favoriteBooks: Flow<List<FavoriteBook>> = favoriteBookDao.getAllFavoriteBooks()

    fun addFavoriteBook(book: FavoriteBook) {
        viewModelScope.launch {
            favoriteBookDao.insertFavoriteBook(book)
        }
    }

    fun removeFavoriteBook(bookId: String) {
        viewModelScope.launch {
            favoriteBookDao.deleteFavoriteBook(bookId)
        }
    }

    suspend fun isFavorite(bookId: String): Boolean {
        return favoriteBookDao.getFavoriteBookById(bookId) != null
    }
}