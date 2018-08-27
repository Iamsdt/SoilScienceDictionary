/*
 * Developed By Shudipto Trafder
 * on 8/17/18 12:55 PM
 * Copyright (c) 2018 Shudipto Trafder.
 */

package com.iamsdt.pssd.ui.main

import android.os.AsyncTask
import android.provider.SearchRecentSuggestions
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.iamsdt.pssd.database.WordTable
import com.iamsdt.pssd.database.WordTableDao
import com.iamsdt.pssd.ext.SingleLiveEvent
import com.iamsdt.pssd.utils.Constants
import com.iamsdt.pssd.utils.model.StatusModel
import timber.log.Timber

class MainVM(val wordTableDao: WordTableDao) : ViewModel() {

    val event = SingleLiveEvent<StatusModel>()

    lateinit var liveData: MediatorLiveData<PagedList<WordTable>>

    private val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setInitialLoadSizeHint(20)//by default page list * 3
            .setPrefetchDistance(10) // default page list
            .setEnablePlaceholders(false) //default true
            .build()

    init {
        if (!::liveData.isInitialized) {
            liveData = MediatorLiveData()
            getData()
        }
    }

    private fun getData() {

        val source = wordTableDao.getAllData()

        val date = LivePagedListBuilder(source, config)
                .build()

        liveData.addSource(date) {
            liveData.value = it
        }
    }

//    fun getRandomWord(): LiveData<WordTable>? {
//        var liveData:LiveData<WordTable> ?= null
//        AsyncTask.execute {
//            val list = wordTableDao.getAllList().list
//            val random = Random()
//            val id = random.nextInt(list)
//            liveData = wordTableDao.getSingleWord(id)
//        }
//        return liveData
//    }

    fun requestSearch(query: String) {


        val source = wordTableDao.getSearchData(query)

        val data = LivePagedListBuilder(source, config).build()

        liveData.addSource(data) {
            liveData.value = it
        }
    }

    val randomData get() = wordTableDao.getRandomData()


    fun submit(query: String?, suggestions: SearchRecentSuggestions?) {
        query?.let {
            AsyncTask.execute {
                val word: WordTable? = wordTableDao.getSearchResult(it)
                Timber.i("Word:$word")
                if (word != null) {
                    suggestions?.saveRecentQuery(query, null)
                    event.postValue(StatusModel(true, Constants.SEARCH, word.word))
                } else {
                    event.postValue(StatusModel(false, Constants.SEARCH, "Word not found!"))
                }
            }
        }
    }

}