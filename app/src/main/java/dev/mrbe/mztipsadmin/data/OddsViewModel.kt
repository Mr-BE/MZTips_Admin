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

    val myTag: String = "MyTag"

    internal val oddsStateFlow = MutableStateFlow<TipsResponse?>(null)
    private val db = Firebase.firestore
    private val dbCollection = db.collection("odds")

    private val _autoId = MutableLiveData<String>()


    //add var
    private val _addDateText = MutableLiveData<String>()

    private val _addOddText = MutableLiveData<String>()

    private val _addResult = MutableLiveData<Int?>()
    val addResult: LiveData<Int?>
    get() = _addResult



    private val _viewModelOdds = MutableLiveData<Odds?>()

    //received var (details)
    private val _receivedOdds = MutableLiveData<Odds?>()
    val receivedOdds: LiveData<Odds?>
    get() = _receivedOdds

    init {
        viewModelScope.launch {
            oddsRepo.getTips().collect {
                oddsStateFlow.value = it
            }
        }
    }

    fun getReceivedOdds(odds: Odds) {
        _receivedOdds.postValue(odds)
    }

    fun updateDbValues(){
        val ref = dbCollection.document(_receivedOdds.value?.id.toString())

        val oddsUpdate = hashMapOf(
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
                    .addOnSuccessListener { ref ->
                        _autoId.value =ref.id

                        updateDocumentId(ref.id)
                        Log.d(myTag, "doc added with id -> ${ref.id}")
                    }
                    .addOnFailureListener { ref ->
                        Log.e(myTag, "doc added with err -> $ref")
                    }
                   .addOnCompleteListener {ref->
                    updateDocumentId(ref.result.id)
                   }
            }

        }

     fun updateDocumentId(id: String) {


        val data = hashMapOf(
            "id" to id,
            "date" to _addDateText.value,
            "oddsTip" to _addOddText.value,
            "oddsResult" to _addResult.value
        )

            dbCollection.document(id)
                .update(data as Map<String, Any>)
                .addOnSuccessListener { listener ->
                    Log.d(myTag, "Update  successful")
                }
    }


    //update add vars
    fun addDateText(dateText: String?) {
        _addDateText.postValue(dateText)
    }
    fun addOddsText(oddsText: String) {
        _addOddText.postValue(oddsText)
    }
    fun addResultValue(result: Int?) {
        _addResult.value = result
    }

    fun deleteData(id:String?) {
        if (id != null) {
            dbCollection.document(id)
                .delete()
                .addOnSuccessListener { Log.d(myTag, "Document deleted successfully") }
                .addOnFailureListener { e -> Log.d(myTag, "could not delete document cos of -> $e") }
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

