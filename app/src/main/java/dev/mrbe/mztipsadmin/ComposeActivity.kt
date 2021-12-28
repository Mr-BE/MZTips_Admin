package dev.mrbe.mztipsadmin

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.mrbe.mztips.data.OddsRepo
import dev.mrbe.mztips.data.OnError
import dev.mrbe.mztips.data.OnSuccess
import dev.mrbe.mztips.data.TipsResponse
import dev.mrbe.mztipsadmin.data.OddsViewModel
import dev.mrbe.mztipsadmin.data.OddsViewModelFactory
import dev.mrbe.mztipsadmin.models.Odds
import dev.mrbe.mztipsadmin.nav.NavRoutes
import dev.mrbe.mztipsadmin.ui.theme.MZTipsAdminTheme
import kotlinx.coroutines.flow.asStateFlow

class ComposeActivity : AppCompatActivity() {

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

                    //set navigator
                    val navController = rememberNavController()

                   NavHost(navController = navController,
                       startDestination =NavRoutes.OddsList.route,){
                       //home screen (Odds List)
                       composable(NavRoutes.OddsList.route){
                           HomeContent(navController = navController)
                       }
                       //Add screen
                       composable(NavRoutes.AddOdds.route){
                           AddOddsActivity().AddOddsContent(
                               oddsViewModel, navController, context
                           )
                       }
                       //Edit Screen
                       composable(NavRoutes.Details.route) {backStack->
                           //extract args
                           val receivedOdds = backStack.arguments?.getParcelable<Odds>(getString(R.string.odds_parcelable_key))
                           if (receivedOdds != null) {
                               OddsDetailsActivity().OddsDetailsContent(receivedOdds = receivedOdds,
                                   viewModel = oddsViewModel, navController = navController)
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
            TopAppBar(
                title = { Text(stringResource(R.string.odds_list)) },
                backgroundColor = colorResource(id = R.color.amber_500)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavRoutes.AddOdds.route) },
                backgroundColor = colorResource(id = R.color.white),
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_add_24),
                        contentDescription = "add button",
                        tint = colorResource(id = R.color.button_background)
                    )
                }
            )
        },
        content = {
            Surface(modifier = Modifier.padding(8.dp)) {
                OddsList(navController)
            }
        }
    )

    }


    @Composable
    private fun OddsList(
        navController: NavController,
        oddsViewModel: OddsViewModel = viewModel(modelClass = OddsViewModel::class.java,
            this, factory = OddsViewModelFactory(OddsRepo()))
    ) {
        val arimoFont = Font(R.font.arimo)

        when (val oddsList: TipsResponse? = oddsViewModel
            .oddsStateFlow.asStateFlow().collectAsState().value) {

            is OnError -> {
                Text(text = "Error: Please try again later")
            }
            is OnSuccess -> {
                val listOfOdds = oddsList.querySnapshot?.toObjects(Odds::class.java)
                listOfOdds?.let {
                    //load list
                    LazyColumn {
                        items(listOfOdds){item: Odds? ->

                        //Items
                        Card(modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(Modifier.clickable { 
                                
                                //create a bundle with selected odds as arg
                                val bundle = Bundle().apply { 
                                    putParcelable(getString(R.string.odds_parcelable_key), item)
                                }
                                
                                //set navigation 
                                navController.navigater(
                                    route = NavRoutes.Details.route,
                                    args = bundle
                                )
//                                navController.navigate(NavRoutes.Test.route, bundle)
                            
                            }) {
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
            else -> {
                Log.wtf("Tag", "wtf  happened")}
        }

    }
}

////Ext. fun. for navigating with just route and bundle
private fun NavController.navigater(
    route: String,
    args: Bundle,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
){
    val routeLink = NavDeepLinkRequest
        .Builder
        .fromUri(NavDestination.createRoute(route).toUri())
        .build()

    val deepLinkMatch = graph.matchDeepLink(routeLink)
    if (deepLinkMatch != null) {
        val destination = deepLinkMatch.destination
        val id = destination.id
        navigate(id, args, navOptions, navigatorExtras)
    } else {
        navigate(route, navOptions, navigatorExtras)
    }
}