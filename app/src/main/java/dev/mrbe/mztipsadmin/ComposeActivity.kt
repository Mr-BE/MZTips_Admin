package dev.mrbe.mztipsadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.mrbe.mztips.data.OddsRepo
import dev.mrbe.mztips.data.OddsViewModel
import dev.mrbe.mztips.data.OddsViewModelFactory
import dev.mrbe.mztipsadmin.nav.NavRoutes
import dev.mrbe.mztipsadmin.ui.theme.MZTipsAdminTheme

class ComposeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MZTipsAdminTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
//na
                    //set
                    val navController = rememberNavController()

                   NavHost(navController = navController,
                       startDestination =NavRoutes.OddsList.route,){
                       //home screen (Odds List)
                       composable(NavRoutes.OddsList.route){
                           HomeContent(navController = navController)
                       }
                       composable(NavRoutes.AddOdds.route){
                           AddOddsActivity().AddOddsContent()
                       }
                   }

                    }
                }
            }
        }
    }


@Composable
fun HomeContent(
    oddsViewModel: OddsViewModel = viewModel(
        factory = OddsViewModelFactory(OddsRepo())
    ), navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.odds_list)) }, backgroundColor = colorResource(id = R.color.orange_500))
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavRoutes.AddOdds.route) },
                backgroundColor = colorResource(id = R.color.button_background),
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_add_24),
                        contentDescription = null,
                        tint = colorResource(id = R.color.white)
                    )
                }
            )
        },
        content = {
            Surface(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                    fontSize = 16.sp,
                )
            }
        }
    )
}
////Ext. fun. for navigating with just route and bundle
//private fun NavController.navigate(
//    route: String,
//    args: Bundle?,
//    navOptions: NavOptions? = null,
//    navigatorExtras: Navigator.Extras? = null
//) {
//    val routeLink = NavDeepLinkRequest
//        .Builder
//        .fromUri(NavDestination.createRoute(route).toUri())
//        .build()
//
//
//        navigate(route, navOptions, navigatorExtras)
//}