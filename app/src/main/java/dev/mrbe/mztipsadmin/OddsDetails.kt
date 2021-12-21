package dev.mrbe.mztipsadmin

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.google.android.material.datepicker.MaterialDatePicker
import dev.mrbe.mztipsadmin.data.OddsViewModel
import dev.mrbe.mztipsadmin.models.Odds
import dev.mrbe.mztipsadmin.nav.NavRoutes
import dev.mrbe.mztipsadmin.ui.theme.MZTipsAdminTheme
import java.text.SimpleDateFormat
import java.util.*

class OddsDetailsActivity : ComponentActivity() {

    private var inputOddsText: String? = ""
    private var inputDateText: String? = ""
    private var clickValue: Int? = -1

    @Composable
    fun OddsDetailsContent( viewModel:OddsViewModel,
                            navController: NavController, receivedOdds: Odds) {
        //pass object to view model
        viewModel.getReceivedOdds(receivedOdds)

            Scaffold(

                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.add_odds)) },
                        backgroundColor = colorResource(id = R.color.orange_500),
                        actions = { IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    )
                },
                floatingActionButton = {

                    FloatingActionButton(
                        onClick = {
                                  viewModel.updateDbValues()
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
                                MyEditTextView()
                            }
                            var datePicked: String? by remember {
                                mutableStateOf(null)
                            }
                            val updatedDate = { date: Long? ->
                                datePicked = dateFormater(date)
                                inputDateText = datePicked
                            }
                            Row(Modifier.padding(0.dp,8.dp)) {
                                MyDatePickerView(datePicked = datePicked, updatedDate = updatedDate)

                            }


                            //passed or failed section
                            Row(Modifier.fillMaxWidth()) {


                                var onClickVal: Int? by remember{
                                    mutableStateOf(-1)
                                }
                                //assign to global var
                                clickValue = onClickVal

                                OutlinedButton(onClick = {
                                    onClickVal = onClickVal?.plus(1)
                                    if (onClickVal!! >1){
                                        onClickVal = -1
                                    }

                                }, Modifier.padding(0.dp,8.dp, 64.dp, 8.dp)){
                                    Text(text = stringResource(R.string.set_results), textAlign = TextAlign.Start,
                                        color = when(clickValue){
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

//    @Composable
//    fun MyCheckBox( checkVal: Boolean){
//        val isChecked = remember { mutableStateOf(false)}
//        isChecked.value = checkVal
//        Checkbox(checked = isChecked.value, onCheckedChange = {
//            isChecked.value = it
//            if (isChecked.value) {
//                clickValue = 1
//            }else {
//                clickValue = 0
//            }
//        })
//    }



        //Edit Text Field
        @Composable
        fun MyEditTextView() {
            var text by remember { mutableStateOf("") }

            OutlinedTextField(value = text,
                onValueChange = { nexText ->
                    text = nexText.trimEnd()
                },
                label = { stringResource(R.string.input_odds) }
            )
            inputOddsText = text
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

        private fun dateFormater(milliseconds: Long?): String? {
            milliseconds?.let {
                val formatter = SimpleDateFormat("EEE, d MMM yyyy", Locale.US)
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeInMillis = it
                return formatter.format(calendar.time)
            }
            return null
        }
    }