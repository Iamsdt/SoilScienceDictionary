/*
 * Developed By Shudipto Trafder
 * on 8/17/18 12:55 PM
 * Copyright (c) 2018 Shudipto Trafder.
 */

package com.iamsdt.pssd.ui.main

import android.os.AsyncTask
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
import javax.inject.Inject

class MainVM @Inject constructor(val wordTableDao: WordTableDao) : ViewModel() {

    val event = SingleLiveEvent<StatusModel>()

    lateinit var liveData: MediatorLiveData<PagedList<WordTable>>

    init {
        if (!::liveData.isInitialized) {
            liveData = MediatorLiveData()
            getData()
        }
    }

    private fun getData() {

        val source = wordTableDao.getAllData()

        val config = PagedList.Config.Builder()
                .setPageSize(10)
                .setInitialLoadSizeHint(20)//by default page size * 3
                .setPrefetchDistance(10) // default page size
                .setEnablePlaceholders(true) //default true
                .build()


        val date = LivePagedListBuilder(source, config)
                .build()

        liveData.addSource(date) {
            liveData.value = it
        }
    }

    fun requestSearch(query: String) {

        if (query.isEmpty()) return

        val source = wordTableDao.getSearchData(query)

        val config = PagedList.Config.Builder()
                .setPageSize(10)
                .setInitialLoadSizeHint(20)//by default page size * 3
                .setPrefetchDistance(10) // default page size
                .setEnablePlaceholders(true) //default true
                .build()


        val data = LivePagedListBuilder(source, config).build()

        liveData.addSource(data) {
            liveData.value = it
        }
    }


    fun submit(query: String?) {
        query?.let {
            AsyncTask.execute {
                val word: WordTable? = wordTableDao.getSearchResult(it)
                Timber.i("Word:$word")
                if (word != null) {
                    event.postValue(StatusModel(true, Constants.SEARCH, "${word.id}"))
                } else {
                    event.postValue(StatusModel(false, Constants.SEARCH, "Word not found!"))
                }
            }
        }
    }

}