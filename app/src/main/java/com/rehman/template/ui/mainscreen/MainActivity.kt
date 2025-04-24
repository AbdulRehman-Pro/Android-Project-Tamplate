package com.rehman.template.ui.mainscreen

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.rehman.template.R
import com.rehman.template.core.utils.ProjectUtils
import com.rehman.template.data.Resource
import com.rehman.template.databinding.ActivityMainBinding
import com.rehman.template.ui.mainscreen.model.MainApiResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        viewModel.userLogin("email","password")

        observers()
    }


    private fun observers(){

        viewModel.apiResponse.observe(this) {
            when (it) {
                is Resource.Error -> {
                    // Handle error
                    Log.e(TAG, "ResponseError: ${it.error} \nApiType: ${it.apiType}")
                }

                is Resource.Loading -> {
                    // Handle loading
                }

                is Resource.Success -> {
                    // Handle success

                    it.data?.let { response ->

                        val userLoginResponse = ProjectUtils.stringToObject(
                            response.string(), MainApiResponse::class.java
                        )

                        Log.d(TAG, "ResponseSuccess: $userLoginResponse")



                    }


                }

            }
        }
    }

}