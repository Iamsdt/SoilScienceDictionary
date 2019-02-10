/*
 * Developed By Shudipto Trafder
 *  on 8/24/18 9:01 PM
 *  Copyright (c)2018  Shudipto Trafder.
 */

package com.iamsdt.pssd.ui.add

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.iamsdt.pssd.database.WordTable
import com.iamsdt.pssd.database.WordTableDao
import com.iamsdt.pssd.ext.ScopeViewModel
import com.iamsdt.pssd.ext.SingleLiveEvent
import com.iamsdt.pssd.ext.toCapFirst
import com.iamsdt.pssd.utils.Constants.ADD.DES
import com.iamsdt.pssd.utils.Constants.ADD.DIALOG
import com.iamsdt.pssd.utils.Constants.ADD.WORD
import com.iamsdt.pssd.utils.PAGE_CONFIG
import com.iamsdt.pssd.utils.model.StatusModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddVM(private val wordTableDao: WordTableDao) : ScopeViewModel() {

    val dialogStatus = SingleLiveEvent<StatusModel>()

    fun addData(word: CharSequence, des: CharSequence, ref: CharSequence) {

        if (word.isEmpty() || word.length <= 2) {
            dialogStatus.value = (StatusModel(false,
                    WORD, "Please input correct word"))

            return
        }

        if (des.isEmpty() || des.length <= 2) {
            dialogStatus.value = StatusModel(false,
                    DES, "Please input correct description")

            return
        }

        var r: String = ref.toString()

        if (ref.isEmpty() || ref.length <= 3) {
            r = "Added by user"
        }

        uiScope.launch {

            withContext(Dispatchers.IO) {
                val data =
                        wordTableDao.getWord(word.toCapFirst())

                val table = data?.copy(des = des.toCapFirst(), reference = r, addByUser = true)
                        ?: WordTable(
                                word = word.toCapFirst(), reference = r.toCapFirst(), des = des.toCapFirst(),
                                addByUser = true)

                val status = wordTableDao.add(table)
                if (status >= 0) {
                    dialogStatus.postValue(StatusModel(true, DIALOG,
                            "Word added successfully"))
                }
            }
        }

    }

    fun getWord(): LiveData<PagedList<WordTable>> {
        val source =
                wordTableDao.getAddedWordByUser()

        return LivePagedListBuilder(source, PAGE_CONFIG)
                .build()
    }

}