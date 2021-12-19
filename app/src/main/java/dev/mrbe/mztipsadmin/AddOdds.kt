package dev.mrbe.mztipsadmin

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.android.material.datepicker.MaterialDatePicker
import dev.mrbe.mztipsadmin.ui.theme.MZTipsAdminTheme
import java.text.SimpleDateFormat
import java.util.*

class AddOddsActivity : AppCompatActivity() {
    private var inputOddsText: String? = ""
    private var inputDateText: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MZTipsAdminTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    AddOddsContent()
                }
            }
        }
    }


    @Composable
    fun AddOddsContent() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.add_odds)) },
                    backgroundColor = colorResource(id = R.color.orange_500)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        Log.i("TAG", "Saved data are -> $inputOddsText and $inputDateText")
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
                Surface(modifier = Modifier.padding(24.dp)) {
                    var textData: String
                    //form content
                    Column(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth()) {
                            MyEditTextView()
                        }
                        var datePicked: String? by remember {
                            mutableStateOf(null)
                        }
                        val updatedDate = { date: Long? ->
                            datePicked = dateFormater(date)
                            inputDateText = datePicked
                        }
                        Row() {
                            MyDatePickerView(datePicked = datePicked, updatedDate = updatedDate)

                        }
                    }

                }
            }
        )
    }

    fun saveData(datePicked: String?, myEditTextView: String) {
        Log.i("TAG", "Data is -> $datePicked and $myEditTextView")
    }

    //Edit Text Field
    @Composable
    fun MyEditTextView() {
        var text by remember { mutableStateOf("") }

        OutlinedTextField(value = text,
            onValueChange = { nexText ->
                text = nexText.trimEnd()
            },
            label = { "Input Odds" }
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
                .fillMaxSize()
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

                val (lable, iconView) = createRefs()

                Text(
                    text = datePicked ?: "Date Picker",
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(lable) {
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

    fun showDatePicker(activity: AppCompatActivity, updatedDate: (Long?) -> Unit) {
        val picker = MaterialDatePicker.Builder.datePicker().build()
        picker.show(activity.supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener { updatedDate(it) }
    }

    fun dateFormater(milliseconds: Long?): String? {
        milliseconds?.let {
            val formatter = SimpleDateFormat("EEE, d MMM yyyy", Locale.US)
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            return formatter.format(calendar.time)
        }
        return null
    }
}