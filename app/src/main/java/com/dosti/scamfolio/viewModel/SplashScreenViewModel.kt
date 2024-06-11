package com.dosti.scamfolio.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosti.scamfolio.api.initializeDataAPI
import com.dosti.scamfolio.dbStuff.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashScreenViewModel(private val repository: Repository, private val sharedCoinGeko: SharedCoinGekoViewModel) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            if(repository.isEmpty()) {
                initializeDataAPI(repository)
            }

            if(System.currentTimeMillis() - repository.chechFetchedDate() > 600000){
            //if one week old
                repository.resetCoinList()
                initializeDataAPI(repository)
            }

            fetchAllCryptos()
            _isLoading.value = false
        }
    }
    private fun fetchAllCryptos() {

        viewModelScope.launch {
            val newListCoin = repository.loadAllCoin()
            sharedCoinGeko.setCoinLiveData(newListCoin)
        }
    }
}