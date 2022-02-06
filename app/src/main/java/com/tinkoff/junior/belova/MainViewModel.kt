package com.tinkoff.junior.belova

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class MainViewModel constructor(private val mainRepository: MainRepository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val joke = MutableLiveData<Joke>()
    var job: Job? = null
    val storage = hashMapOf<Int, Joke>()
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun getJoke(counter: Int) {
        loading.postValue(true)
        if (storage[counter] != null) {
            loading.postValue(false)
            joke.postValue(storage[counter])
        }
        else
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                val response = mainRepository.getRandom()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val respJoke = response.body()
                        joke.postValue(respJoke!!)
                        loading.value = false
                        storage[counter] = respJoke
                    } else {
                        onError("Error : ${response.message()} ")
                    }
                }
            }
    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}