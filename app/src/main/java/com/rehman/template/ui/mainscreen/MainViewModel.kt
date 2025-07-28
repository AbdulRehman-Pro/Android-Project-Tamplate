package com.rehman.template.ui.mainscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rehman.template.data.RemoteDataRepository
import com.rehman.template.data.network.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val repository: RemoteDataRepository) :
    ViewModel() {
    val apiResponse: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()


    fun userLogin(email: String, password: String) {
        viewModelScope.launch {
            apiResponse.value = Resource.Loading()
            repository.authLoginUser(email = email, password = password).collect {
                apiResponse.value = it
            }
        }
    }

}