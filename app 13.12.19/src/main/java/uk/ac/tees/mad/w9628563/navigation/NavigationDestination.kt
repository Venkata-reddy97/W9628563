package uk.ac.tees.mad.w9628563.navigation

interface NavigationDestination {
    val navRoute: String
}

object SplashDestination: NavigationDestination{
    override val navRoute: String
        get() = "splash"
}

object HomeDestination: NavigationDestination{
    override val navRoute: String
        get() = "home"
}

object LoginDestination: NavigationDestination{
    override val navRoute: String
        get() = "login"
}

object RegisterDestination: NavigationDestination{
    override val navRoute: String
        get() = "register"
}

object BookDetailDestination: NavigationDestination{
    override val navRoute: String
        get() = "bookDetail"
}

object AddBookDestination: NavigationDestination{
    override val navRoute: String
        get() = "addBook"
}

object BookListDestination: NavigationDestination{
    override val navRoute: String
        get() = "bookList"
}

object ProfileDestination: NavigationDestination{
    override val navRoute: String
        get() = "profile"
}

object FavoriteDestination: NavigationDestination{
    override val navRoute: String
        get() = "favorite"
}