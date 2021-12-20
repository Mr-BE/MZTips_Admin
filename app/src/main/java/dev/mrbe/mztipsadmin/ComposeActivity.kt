package dev.mrbe.mztipsadmin

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.mrbe.mztips.data.OddsRepo
import dev.mrbe.mztips.data.OnError
import dev.mrbe.mztips.data.OnSuccess
import dev.mrbe.mztipsadmin.data.OddsViewModel
import dev.mrbe.mztipsadmin.data.OddsViewModelFactory
import dev.mrbe.mztipsadmin.models.Odds
import dev.mrbe.mztipsadmin.nav.NavRoutes
import dev.mrbe.mztipsadmin.ui.theme.MZTipsAdminTheme
import kotlinx.coroutines.flow.asStateFlow

class ComposeActivity : AppCompatActivity() {
    private lateinit var viewModel: OddsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MZTipsAdminTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                   val  oddsViewModel: OddsViewModel = viewModel(modelClass = OddsViewModel::class.java,
                    this, factory = OddsViewModelFactory(OddsRepo())
                   )
                    val context = this.applicationContext

                    //set
                    val navController = rememberNavController()

                   NavHost(navController = navController,
                       startDestination =NavRoutes.OddsList.route,){
                       //home screen (Odds List)
                       composable(NavRoutes.OddsList.route){
                           HomeContent(navController = navController)
                       }
                       composable(NavRoutes.AddOdds.route){
                           AddOddsActivity().AddOddsContent(
                               oddsViewModel, navController, context
                           )
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
                OddsList()
            }
        }
    )

    }


    @Composable
    private fun OddsList(

        oddsViewModel: OddsViewModel = viewModel(modelClass = OddsViewModel::class.java,
            this, factory = OddsViewModelFactory(OddsRepo()))
    ) {
        val arimoFont = Font(R.font.arimo)

        when (val oddsList = oddsViewModel
            .oddsStateFlow.asStateFlow().collectAsState().value) {

            is OnError -> {
                Text(text = "Error: Please try again later")
            }
            is OnSuccess -> {
                val listOfOdds = oddsList.querySnapshot?.toObjects(Odds::class.java)
                listOfOdds?.let {
                    //load list
                    LazyColumn{
                        items(listOfOdds){item: Odds? ->

                        //Items
                        Card(modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                        ) {
                            Row() {
                                Column(modifier = Modifier.fillMaxWidth()) {

                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp, 8.dp, 0.dp, 0.dp)
                                        .background(Color.Black)
                                    ) {
                                       Column(modifier = Modifier.fillMaxWidth(),
                                       horizontalAlignment =
                                       Alignment.CenterHorizontally) {
                                           item?.date.let { oddsDate ->
                                               if (oddsDate != null) {
                                                   Text(text = oddsDate,
                                                   style = TextStyle(fontStyle = arimoFont.style,
                                                   fontSize = 14.sp,
                                                   color = Color.White)
                                                   )
                                               }
                                           }

                                       }
                                    }
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp, 2.dp, 0.dp, 8.dp)
                                        .background(colorResource(id = R.color.text_background)), ) {
                                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

                                            item?.oddsTip.let { oddsTip ->
                                                if (oddsTip != null) {
                                                    Text(
                                                        text = oddsTip,
                                                        style = TextStyle(
                                                            fontStyle = arimoFont.style,
                                                            fontSize = 20.sp, color = Color.Black
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }

                                }

                            }

                        }
                        }

                    }
                }
            }
        }

    }
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