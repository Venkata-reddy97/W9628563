package uk.ac.tees.mad.w9628563

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import uk.ac.tees.mad.w9628563.domain.BookItem
import uk.ac.tees.mad.w9628563.domain.BooksResponse

interface GoogleBooksApiService {
    @GET("volumes")
    suspend fun getBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20
    ): BooksResponse

    @GET("volumes/{id}")
    suspend fun getBookById(
        @Path("id") id: String
    ): BookItem
}

// Create the Retrofit instance
val retrofit = Retrofit.Builder()
    .baseUrl("https://www.googleapis.com/books/v1/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

// Create an instance of the API service
val googleBooksApiService = retrofit.create(GoogleBooksApiService::class.java)