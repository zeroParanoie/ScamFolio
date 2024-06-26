package com.dosti.scamfolio.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dosti.scamfolio.db.entities.User
import com.dosti.scamfolio.db.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val repository: Repository) : ViewModel() {
    private val _loginResult = MutableStateFlow<User?>(null)

    private val _eventToast= MutableStateFlow(false)
    val eventToast: StateFlow<Boolean> get()=_eventToast

    private val _loginError=MutableStateFlow(false)
    val loginError: StateFlow<Boolean> get()=_loginError

    private val _signInError=MutableStateFlow(false)
    val signInError:StateFlow<Boolean> get()=_signInError


    enum class loginScreens{
        LOGIN,REGISTER, HOME
    }



    private var _currentScreen=mutableStateOf(loginScreens.LOGIN)
    var currentScreen= _currentScreen

    fun navigateToLogin(){
        _currentScreen.value=(loginScreens.LOGIN)
    }

    fun navigateToLoginFromLoggedIn(){
        _loginResult.value= null
        _currentScreen.value=(loginScreens.LOGIN)
    }

    fun navigateToRegister(){
        _currentScreen.value=(loginScreens.REGISTER)
    }

    fun navigateToHome(){
        _currentScreen.value=(loginScreens.HOME)

    }

    val loginResult: StateFlow<User?> =_loginResult

    fun checkLogin(
        username: String,
        password: String
    ) {
        viewModelScope.launch {
            val user = withContext(Dispatchers.IO) {
                repository.login(username, password)
            }
            if (user!=null) {
                _loginResult.value = user
                _loginError.value=false
            }
            else {
                _loginError.value=true
                _eventToast.value=true
            }
        }
    }

    fun createNewUser(
        username: String,
        password: String
    ) {
        viewModelScope.launch {
            val exist = withContext(Dispatchers.IO) {
                repository.checkUserExistence(username)
            }

            if (!exist) {
                withContext(Dispatchers.IO) {
                    repository.signIn(username, password)
                    _signInError.value=false
                }
            } else {
                _eventToast.value = true
                _signInError.value=true
            }

        }
    }

    fun resetEventToast(){
        _eventToast.value=false
    }

    fun signalSignInErr(){
        _signInError.value=true
    }
}