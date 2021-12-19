package dev.mrbe.mztipsadmin.nav

sealed class NavRoutes(val route: String){
    object OddsList: NavRoutes("list")
    object AddOdds: NavRoutes("add")
    object Details: NavRoutes("details")
}
