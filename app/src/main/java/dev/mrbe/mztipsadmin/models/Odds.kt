package dev.mrbe.mztipsadmin.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Odds(
    var date: String = "",
    var oddsTip: String = "",
    var oddsResult: Int = -1
): Parcelable
