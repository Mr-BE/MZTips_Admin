package dev.mrbe.mztipsadmin.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
    private val dbCollection = db.collection("odds")

    //add var
    private val _addDateText = MutableLiveData<String>()

    private val _addOddText = MutableLiveData<String>()

    private val _addResult = MutableLiveData<Int?>()

    private val _viewModelOdds = MutableLiveData<Odds?>()

    //received var (details)
    private val _receivedOdds = MutableLiveData<Odds?>()

    init {
        viewModelScope.launch {
            oddsRepo.getTips().collect {
                oddsStateFlow.value = it
            }
        }
    }

    fun getReceivedOdds(odds: Odds) {
        _receivedOdds.value = odds
    }

    fun updateDbValues(){
        val ref = dbCollection.document(_receivedOdds.value?.id.toString())

        val oddsUpdate = hashMapOf(
            "id" to _receivedOdds.value?.id,
            "date" to _receivedOdds.value?.date,
            "oddsTip" to _receivedOdds.value?.oddsTip,
            "oddsResult" to _receivedOdds.value?.oddsResult
        )
        ref.update(oddsUpdate as Map<String, Any>)

    }
        fun saveOddsData() {
            _viewModelOdds.value = _addResult.value?.toInt()?.let {
                Odds("",
                    _addDateText.value.toString(), _addOddText.value.toString(), it
                )
            }

            _viewModelOdds.value?.let {
               dbCollection
                   .add(it)
                   .addOnSuccessListener { docRef ->
                       val docId = docRef.id
                       val document = dbCollection.document(docId)
                       document.update("id", docId)
                   }
            }

        }


    //update add vars
    fun addDateText(dateText: String?) {
        _addDateText.value = dateText
    }

    fun addOddsText(oddsText: String) {
        _addOddText.value = oddsText
    }

    fun addResultValue(result: Int?) {
        _addResult.value = result
    }

    /** update details vars */
    fun editDateText(dateText: String?) {
        if (dateText != null) {
            _receivedOdds.value?.date = dateText
        }
    }

    fun editOddsText(oddsText: String?) {
        if (oddsText != null) {
            _receivedOdds.value?.oddsTip = oddsText
        }
    }

    fun editResultValue(result: Int?) {
        if (result != null) {
            _receivedOdds.value?.oddsResult = result
        }
    }

    fun deleteData(id: String?) {
        if (id != null) {
            dbCollection.document(id)
                .delete()
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

