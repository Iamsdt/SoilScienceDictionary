/*
 * Developed By Shudipto Trafder
 *  on 8/21/18 6:00 PM
 *  Copyright (c) 2018  Shudipto Trafder.
 */

package com.iamsdt.pssd.ui.flash

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.iamsdt.pssd.R
import com.iamsdt.pssd.ext.ViewModelFactory
import com.iamsdt.pssd.ui.favourite.FavouriteVM
import kotlinx.android.synthetic.main.activity_flash_card.*
import kotlinx.android.synthetic.main.content_flash_card.*
import javax.inject.Inject

class FlashCardActivity:AppCompatActivity(){

    @Inject
    lateinit var factory: ViewModelFactory

    private val viewModel: FavouriteVM by lazy {
        ViewModelProviders.of(this, factory).get(FavouriteVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ThemeUtils.initialize(this)
        setContentView(R.layout.activity_flash_card)
        setSupportActionBar(toolbar)

        val manager = GridLayoutManager(this,2,
                GridLayoutManager.VERTICAL,false)

        flash_rcv.layoutManager = manager

        val adapter = FlashAdapter(this)

        flash_rcv.adapter = adapter

        viewModel.getData().observe(this, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

}