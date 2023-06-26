package com.example.fetchapis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.fetchapis.api.TheCatApiService
import com.example.fetchapis.model.ImageResultData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    private val retrofit by lazy {
        Retrofit.Builder().baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create()).build()

    }

    private val theCatApiService by lazy {
        retrofit.create(TheCatApiService::class.java)
    }

    private val serverResponse: TextView by lazy {
        findViewById(R.id.main_server_response)
    }
    private val profileImageView by lazy {
        findViewById<ImageView>(R.id.main_profile_image)
    }
    private val imageLoader: ImageLoader by lazy {
        GlideImageLoader(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getCatImageResponse()
    }

    private fun getCatImageResponse() {
        val call = theCatApiService.searchImages(1, "full")
        call.enqueue(object : Callback<List<ImageResultData>> {
            override fun onResponse(
                call: Call<List<ImageResultData>>,
                response: Response<List<ImageResultData>>

            ) {
                if (response.isSuccessful) {
                    val imageResultData = response.body()?.get(0)
                    if (imageResultData != null) {
                        imageLoader.loadImage(imageResultData.imageUrl, profileImageView)
                    } else {
                        Log.e("MainActivity", "Error: imageResultData is null")
                    }
                    serverResponse.text = imageResultData?.imageUrl
                } else {
                    Log.e("MainActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ImageResultData>>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")

            }

        })
    }
}
