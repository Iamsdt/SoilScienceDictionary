package com.iamsdt.pssd.ui.main

import android.content.Intent
import android.os.*
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.iamsdt.pssd.R
import com.iamsdt.pssd.database.WordTableDao
import com.iamsdt.pssd.ext.ToastType
import com.iamsdt.pssd.ext.addStr
import com.iamsdt.pssd.ext.showToast
import kotlinx.android.synthetic.main.random_sheet.view.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class RandomDialog : BottomSheetDialogFragment(), TextToSpeech.OnInitListener {

    val wordTableDao: WordTableDao by inject()

    private lateinit var textToSpeech: TextToSpeech

    private var wordTxt = ""

    var size = 100

    var bookmark = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AsyncTask.execute {
            size = wordTableDao.getAllList().size
            Timber.i("Size found: $size")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.random_sheet, container, false)

        textToSpeech = TextToSpeech(context, this)

        val wordTV: TextView = view.wordTV
        val desTV: TextView = view.desTV
        val favIcon: ImageButton = view.like
        val speak: ImageButton = view.speak

        val id = getRandomID()

        Timber.i("ID get: $id")

        //draw ui
        wordTableDao.getSingleWord(id).observe(this, Observer { wordTable ->

            //save bookmark
            bookmark = wordTable.bookmark

            //save word txt
            wordTxt = wordTable.word

            wordTV.addStr(wordTable.word)
            desTV.addStr(wordTable.des)

            // complete: 8/17/2018 favourite icon
            if (wordTable.bookmark) {
                favIcon.setImageDrawable(view.context.getDrawable(R.drawable.ic_like_fill))
            } else {
                favIcon.setImageDrawable(view.context.getDrawable(R.drawable.ic_like_blank))
            }

        })

        favIcon.setOnClickListener {
            AsyncTask.execute {
                if (bookmark) {
                    val status = wordTableDao.deleteBookmark(id)
                    if (status > 0) {
                        Handler(Looper.getMainLooper()).post {
                            showToast(ToastType.INFO, "Bookmark Delete")
                        }
                    }
                } else {
                    val status = wordTableDao.setBookmark(id)
                    if (status > 0) {
                        Handler(Looper.getMainLooper()).post {
                            showToast(ToastType.SUCCESSFUL, "Bookmarked")
                        }
                    }
                }
            }
        }

        speak.setOnClickListener {
            speakOut()
        }

        return view
    }

    private fun getRandomID(): Int {
        val random = Random()
        return random.nextInt(size)
    }

    private fun speakOut() {

        if (!::textToSpeech.isInitialized) {
            showToast(ToastType.ERROR, "Can not speak right now. Try again")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(wordTxt, TextToSpeech.QUEUE_FLUSH, null, null)

        } else {
            textToSpeech.speak(wordTxt, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.ERROR && ::textToSpeech.isInitialized) {

            val result = textToSpeech.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED ||
                    result == TextToSpeech.ERROR_NOT_INSTALLED_YET) {

                val installIntent = Intent()
                installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                startActivity(installIntent)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
}