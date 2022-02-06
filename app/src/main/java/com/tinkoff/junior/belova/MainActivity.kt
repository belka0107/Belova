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
    lateinit var prevButton: ImageButton
    lateinit var errorLayout: LinearLayout

    var counter = Variable(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        label = findViewById(R.id.label)
        progressBar = findViewById(R.id.progressBar)
        nextButton = findViewById(R.id.nextButton)
        prevButton = findViewById(R.id.prevButton)

        errorLayout = findViewById(R.id.errorLayout)

        val retrofitService = RetrofitService.getInstance()
        val mainRepository = MainRepository(retrofitService)

        viewModel =
            ViewModelProvider(this, ViewModelFactory(mainRepository)).get(MainViewModel::class.java)

        viewModel.joke.observe(this) {
            imageView.visibility = View.VISIBLE
            Glide
                .with(this)
                .load(it.gifURL)
                .centerCrop()
                .into(imageView)

            label.text = it.description
        }

        viewModel.errorMessage.observe(this) {
            errorLayout.visibility = View.VISIBLE
        }

        viewModel.loading.observe(this, Observer {
            imageView.visibility = if (!it) View.VISIBLE else View.GONE
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.getJoke(counter.value)

        nextButton
            .setOnClickListener {
                counter.value = counter.value + 1
                viewModel.getJoke(counter.value)
            }
        prevButton
            .setOnClickListener {
                counter.value = counter.value - 1
                viewModel.getJoke(counter.value)
            }

        counter
            .observable
            .subscribe { prevButton.visibility = if (it > 0) View.VISIBLE else View.GONE }

        findViewById<TextView>(R.id.repeat).setOnClickListener {
            viewModel.getJoke(counter.value)
            errorLayout.visibility = View.GONE
        }
    }
}