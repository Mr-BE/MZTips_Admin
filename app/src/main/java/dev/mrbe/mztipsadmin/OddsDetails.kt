package dev.mrbe.mztipsadmin

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.google.android.material.datepicker.MaterialDatePicker
import dev.mrbe.mztipsadmin.data.OddsViewModel
import dev.mrbe.mztipsadmin.models.Odds
import dev.mrbe.mztipsadmin.nav.NavRoutes
import java.text.SimpleDateFormat
import java.util.*

class OddsDetailsActivity : ComponentActivity() {

    private var editOddsText: String? = ""
    private var editDateText: String? = ""
    private var clickValue: Int? = -1

    @Composable
    fun OddsDetailsContent( viewModel:OddsViewModel,
                            navController: NavController, receivedOdds: Odds) {
        //pass object to view model
        viewModel.getReceivedOdds(receivedOdds)

        editOddsText = receivedOdds.oddsTip

        editDateText = receivedOdds.date

        clickValue = receivedOdds.oddsResult

        Scaffold(

                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.add_odds)) },
                        backgroundColor = colorResource(id = R.color.amber_500),
                        actions = { IconButton(onClick = { viewModel.deleteData(receivedOdds.id)
                            navController.navigate(NavRoutes.OddsList.route)
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    )
                },
                floatingActionButton = {

                    FloatingActionButton(
                        onClick = {
//                            receivedOdds.id?.let { viewModel.updateDocumentId(it) }
                            viewModel.updateDbValues()
                            navController.navigate(NavRoutes.OddsList.route)
                        },
                        backgroundColor = colorResource(id = R.color.button_background),
                        content = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_save_24),
                                contentDescription = null,
                                tint = colorResource(id = R.color.white)
                            )
                        }
                    )

                },
                content = {
                    Surface(modifier = Modifier.padding(8.dp)) {

                        //form content
                        Column(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp, 8.dp)) {
                                MyEditTextView(viewModel)
                            }
                            var datePicked: String? by remember {
                                mutableStateOf(receivedOdds.date)
                            }
                            val updatedDate = { date: Long? ->
                                datePicked = dateFormatter(date)
                                editDateText = datePicked
                                //offload data to VM
                                viewModel.editDateText(datePicked)
                            }
                            Row(Modifier.padding(0.dp,8.dp)) {
                                MyDatePickerView(datePicked = datePicked, updatedDate = updatedDate)

                            }

                            var colorVal: Int?
                            //passed or failed section
                            Row(Modifier.fillMaxWidth()) {
                                colorVal = if (receivedOdds.oddsResult != -1) {
                                    receivedOdds.oddsResult
                                } else {
                                    -1
                                }


                                var onClickVal: Int? by remember {
                                    mutableStateOf(colorVal)
                                }

                                OutlinedButton(onClick = {
                                    //control click value
                                    onClickVal = if (onClickVal!! < 1) {
                                        onClickVal!! + 1
                                    } else {
                                        -1
                                    }
//                                    viewModel.addResultValue(onClickVal)
                                    viewModel.editResultValue(onClickVal)

                                }, Modifier.padding(0.dp,8.dp, 64.dp, 8.dp)){
                                    Text(text = stringResource(R.string.set_results), textAlign = TextAlign.Start,
                                        color = when(onClickVal){
                                            -1 -> colorResource(id = R.color.button_background)
                                            0 -> Color.Red
                                            1 -> Color.Green
                                            else -> colorResource(id = R.color.button_background)
                                        })

                                }

                            }
                        }

                    }
                }
            )
        }


//        Edit Text Field
        @Composable
        fun MyEditTextView(viewModel: OddsViewModel) {
    var text by remember { mutableStateOf(editOddsText) }

    text?.let {
        OutlinedTextField(value = it,
            onValueChange = { nexText ->
                text = nexText.trimEnd()
            },
            label = { stringResource(R.string.input_odds) }
        )
    }
//    text?.let { viewModel.addOddsText(it) }
    viewModel.editOddsText(text)
}


        //DatePicker
        @Composable
        fun MyDatePickerView(
            datePicked: String?,
            updatedDate: (date: Long?) -> Unit
        ) {
            val activity = LocalContext.current as AppCompatActivity

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopStart)
                    .padding(top = 10.dp)
                    .border(0.5.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
                    .clickable {
                        showDatePicker(activity, updatedDate)

                    }
            ) {


                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    val (label, iconView) = createRefs()

                    Text(
                        text = datePicked ?: "Date Picker",
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .constrainAs(label) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(iconView.start)
                                width = Dimension.fillToConstraints
                            }
                    )

                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp, 20.dp)
                            .constrainAs(iconView) {
                                end.linkTo(parent.end)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            },
                        tint = MaterialTheme.colors.onSurface
                    )

                }

            }
        }


    private fun showDatePicker(activity: AppCompatActivity, updatedDate: (Long?) -> Unit) {
        val picker = MaterialDatePicker.Builder.datePicker().build()
        picker.show(activity.supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener { updatedDate(it) }
    }

    private fun dateFormatter(milliseconds: Long?): String? {
        var date: String? = ""
        milliseconds?.let {
            val formatter = SimpleDateFormat("EEE, d MMM yyyy", Locale.US)
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            date = formatter.format(calendar.time)
            return formatter.format(calendar.time)
        }
        return date
    }
}