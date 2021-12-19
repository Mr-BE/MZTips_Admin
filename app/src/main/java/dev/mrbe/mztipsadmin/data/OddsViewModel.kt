package dev.mrbe.mztips.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class OddsViewModel (val oddsRepo: OddsRepo):ViewModel() {

    val oddsStateFlow = MutableStateFlow<TipsResponse?>(null)

    init {
        viewModelScope.launch {
            oddsRepo.getTips().collect {
                oddsStateFlow.value = it
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