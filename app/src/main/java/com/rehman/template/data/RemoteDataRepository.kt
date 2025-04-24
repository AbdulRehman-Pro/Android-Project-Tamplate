package com.rehman.template.data


import com.rehman.template.core.enums.ApiTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RemoteDataRepository @Inject constructor(private val apiService: ApiService) : ApiConverter() {

    // Auth APIs
    fun authLoginUser(email: String, password: String) = flow {
        emit(
            handleApiCall(
                { apiService.authLoginUser(email, password) },
                apiName = ApiTypes.AuthLoginUser
            )
        )
    }.flowOn(Dispatchers.IO)

    fun forgetLoginUser(email: String) = flow {
        emit(
            handleApiCall(
                { apiService.forgetLoginUser(email) },
                apiName = ApiTypes.ForgetLoginUser
            )
        )
    }.flowOn(Dispatchers.IO)

    fun authLogoutUser() = flow {
        emit(
            handleApiCall(
                { apiService.logoutUser() },
                apiName = ApiTypes.AuthLogoutUser
            )
        )
    }.flowOn(Dispatchers.IO)

}