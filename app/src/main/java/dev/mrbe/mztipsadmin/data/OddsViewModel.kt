package dev.mrbe.mztipsadmin.data

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.mrbe.mztips.data.OddsRepo
import dev.mrbe.mztips.data.TipsResponse
import dev.mrbe.mztipsadmin.models.Odds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class OddsViewModel (val oddsRepo: OddsRepo):ViewModel() {

    internal val oddsStateFlow = MutableStateFlow<TipsResponse?>(null)
    private val db = Firebase.firestore


    private val _viewModelOdds = MutableLiveData<Odds?>()

    init {
        viewModelScope.launch {
            oddsRepo.getTips().collect {
                oddsStateFlow.value = it
            }
        }
    }
        fun setOddsData(odds: String, date: String, resultValue: Int) {
            _viewModelOdds.postValue(Odds(date, odds, resultValue))

            _viewModelOdds.value?.let {
                db.collection("newodds")
                    .add(it)
                    .addOnSuccessListener { ref ->
                        Log.d("TAG", "doc added with id -> ${ref.id}")
                    }
                    .addOnFailureListener { ref ->
                        Log.d("TAG", "doc added with err -> $ref")
                    }
            }

        }

}
class OddsViewModelFactory(private val oddsRepo: OddsRepo) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OddsViewModel::class.java)) {
            return OddsViewModel(oddsRepo) as T
        }
        throw IllegalStateException()
    }

    }

