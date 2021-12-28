package dev.mrbe.mztipsadmin

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import dev.mrbe.mztipsadmin.nav.NavRoutes
import java.text.SimpleDateFormat
import java.util.*

class AddOddsActivity : AppCompatActivity() {

    @Composable
    fun AddOddsContent(viewModel: OddsViewModel, navController: NavController,
    context: Context) {
        Scaffold(

            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.add_odds)) },
                    backgroundColor = colorResource(id = R.color.amber_500),
                )
            },
            floatingActionButton = {

                FloatingActionButton(
                    onClick = {
                            viewModel.saveOddsData()
                        navController.navigate(NavRoutes.OddsList.route)

                    },
                    backgroundColor = colorResource(id = R.color.white),
                    content = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_save_24),
                            contentDescription = "Save button",
                            tint = colorResource(id = R.color.button_background)
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
                            mutableStateOf("")
                        }
                        val updatedDate = { date: Long? ->
                            datePicked = dateFormatter(date)
                            viewModel.addDateText( datePicked)
                        }
                        Row(Modifier.padding(0.dp,8.dp)) {
                            MyDatePickerView(datePicked = datePicked, updatedDate = updatedDate)
                        }

                        //passed or failed section
                        Row(Modifier.fillMaxWidth()) {

                            var onClickVal: Int? by remember{
                                mutableStateOf( -1)
                               }

                            OutlinedButton(onClick = {

                                //control click value
                                onClickVal = if (onClickVal!! < 1) {
                                    onClickVal!! + 1
                                } else {
                                    -1
                                }

                                viewModel.addResultValue(onClickVal)

                            }, Modifier.padding(0.dp,8.dp, 64.dp, 8.dp)){
                                Text(text = stringResource(R.string.set_results), textAlign = TextAlign.Start,
                                    //set color based on click value
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

    //Edit Text Field
    @Composable
    fun MyEditTextView(viewModel: OddsViewModel) {
        var text by remember { mutableStateOf("") }

        OutlinedTextField(value = text,
            onValueChange = { nexText ->
                text = nexText
            },
            label = { stringResource(R.string.input_odds) }
        )
        viewModel.addOddsText(text)
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


    //Call android default date picker
    private fun showDatePicker(activity: AppCompatActivity, updatedDate: (Long?) -> Unit) {
        val picker = MaterialDatePicker.Builder.datePicker().build()
        picker.show(activity.supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener { updatedDate(it) }
    }

    //Format date object to readable string
    private fun dateFormatter(milliseconds: Long?): String? {
        var date = ""
        milliseconds?.let {
            val formatter = SimpleDateFormat("EEE, d MMM yyyy", Locale.US)
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            date = formatter.format(calendar.time)
            return date
        }
        return date
    }
}