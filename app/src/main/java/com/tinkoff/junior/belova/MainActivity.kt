package com.tinkoff.junior.belova

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide


class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var imageView: ImageView
    lateinit var progressBar: ProgressBar
    lateinit var label: TextView
    lateinit var nextButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        label = findViewById(R.id.label)
        progressBar = findViewById(R.id.progressBar)
        nextButton = findViewById(R.id.nextButton)

        val retrofitService = RetrofitService.getInstance()
        val mainRepository = MainRepository(retrofitService)

        viewModel =
            ViewModelProvider(this, ViewModelFactory(mainRepository)).get(MainViewModel::class.java)

        viewModel.joke.observe(this) {
            Glide
                .with(this)
                .load(it.gifURL)
                .centerCrop()
                .into(imageView)

            label.text = it.description
        }

        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.loading.observe(this, Observer {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })

        viewModel.getJoke()

        nextButton.setOnClickListener { viewModel.getJoke() }

    }
}