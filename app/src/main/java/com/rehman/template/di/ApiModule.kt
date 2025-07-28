package com.rehman.template.di

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.rehman.template.BuildConfig
import com.rehman.template.ui.mainscreen.MainActivity
import com.rehman.template.core.utils.PrefUtils
import com.rehman.template.data.ApiService
import com.rehman.template.data.network.PrettyHttpLogger
import com.rehman.template.data.network.PrettyRequestInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Named("BASE_URL")
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideApiService(
        @Named("BASE_URL") baseurl: String,
        converterFactory: GsonConverterFactory,
        client: OkHttpClient,
    ): ApiService = Retrofit.Builder()
        .baseUrl(baseurl)
        .addConverterFactory(converterFactory)
        .client(client)
        .build()
        .create(ApiService::class.java)


    @Provides
    fun provideClient(@ApplicationContext context: Context): OkHttpClient {
        val interceptor = HttpLoggingInterceptor(PrettyHttpLogger()).apply {
            level = HttpLoggingInterceptor.Level.HEADERS
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                val authToken = PrefUtils.getAuthToken(context)

                // Add Authorization header if the token is not null
                authToken?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }

                // You can add any other headers you want to add to the request here
                // Example:
                // requestBuilder.addHeader("key", "value")


                val response = chain.proceed(requestBuilder.build())

                // ✅ Check if API response is 401 (Unauthorized)
                if (response.code == 401) {
                    Log.e("okhttp.OkHttpClient", "Unauthorized (401) - Logging out user")

                    // Clear stored authentication details
                    PrefUtils.clearAuthToken(context)

                    // Redirect user to Login Screen
                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                }

                response

            }
            .addInterceptor(PrettyRequestInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }


    @Provides
    fun provideConvertorFactory(): GsonConverterFactory {
        val gson = GsonBuilder()
            .setStrictness(Strictness.LENIENT)
            .create()
        return GsonConverterFactory.create(gson)
    }


}