package uk.ac.tees.mad.w9628563.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorite_books")
data class FavoriteBook(
    @PrimaryKey val id: String,
    val title: String,
    val thumbnail: String?
)


@Dao
interface FavoriteBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteBook(book: FavoriteBook)

    @Query("DELETE FROM favorite_books WHERE id = :bookId")
    suspend fun deleteFavoriteBook(bookId: String)

    @Query("SELECT * FROM favorite_books")
    fun getAllFavoriteBooks(): Flow<List<FavoriteBook>>

    @Query("SELECT * FROM favorite_books WHERE id = :bookId")
    suspend fun getFavoriteBookById(bookId: String): FavoriteBook?
}