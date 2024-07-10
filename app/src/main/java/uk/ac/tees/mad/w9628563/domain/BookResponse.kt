package uk.ac.tees.mad.w9628563.domain

// Data class for the main response
data class BooksResponse(
    val kind: String,
    val totalItems: Int,
    val items: List<BookItem>
)

// Data class for each book item
data class BookItem(
    val kind: String,
    val id: String,
    val selfLink: String,
    val volumeInfo: VolumeInfo,
    val saleInfo: SaleInfo,
    val accessInfo: AccessInfo,
)

// Data class for volume information
data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val pageCount: Int?,
    val printType: String,
    val categories: List<String>?,
    val averageRating: Double?,
    val ratingsCount: Int?,
    val imageLinks: ImageLinks?,
    val language: String,
    val previewLink: String,
)

// Data class for sale information
data class SaleInfo(
    val listPrice: Price?,
    val retailPrice: Price?,
    val buyLink: String?,
    val offers: List<Offer>?
)

data class AccessInfo(
    val webReaderLink: String,
)


// Data class for image links
data class ImageLinks(
    val smallThumbnail: String,
    val thumbnail: String
)

// Data class for price
data class Price(
    val amount: Double,
    val currencyCode: String
)

// Data class for offer
data class Offer(
    val finskyOfferType: Int,
    val listPrice: Price,
    val retailPrice: Price
)