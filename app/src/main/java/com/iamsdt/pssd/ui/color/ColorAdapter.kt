/*
 * Developed By Shudipto Trafder
 *  on 8/24/18 8:15 PM
 *  Copyright (c)2018  Shudipto Trafder.
 */

package com.iamsdt.pssd.ui.color

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.iamsdt.pssd.R
import com.iamsdt.pssd.ui.callback.ClickListener
import kotlinx.android.synthetic.main.color_item.view.*

internal data class ColorAdapter(private val myTheme: ArrayList<MyTheme>,
                                 private val context: Context,
                                 private val clickListener: ClickListener) :
        RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.color_item, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val theme = myTheme[position]
        holder.bindTo(theme)
    }


    override fun getItemCount(): Int {
        return myTheme.size
    }

    /**
     * This method for create a new drawable
     * we take a color id and show color through drawable
     *
     * @param id color id for selected color
     */
    private fun getDrawablesWithColor(@ColorInt id: Int): Drawable {
        val shapeDrawable = ShapeDrawable(OvalShape())

        //initialize 32 db value
        shapeDrawable.intrinsicWidth = pixelConverter(32)
        shapeDrawable.intrinsicHeight = pixelConverter(32)

        shapeDrawable.setColorFilter(id, PorterDuff.Mode.SRC)

        return shapeDrawable
    }

    /**
     * This methods convert dp to px
     *
     * @param dp float to convert
     * @return converted value in px
     */
    private fun pixelConverter(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
                context.resources.displayMetrics).toInt()
    }

    /**
     * This methods for find color id through theme
     * @param stylesID theme id of any theme
     * @param attrId color type id like color primary, accent color
     * @return given color id from theme in the basis of colorAttr
     */
    private fun getThemeColor(stylesID: Int, @AttrRes attrId: Int): Int {

        val typedValue = context.obtainStyledAttributes(stylesID, intArrayOf(attrId))
        val colorFromTheme = typedValue.getColor(0, 0)
        typedValue.recycle()

        return colorFromTheme
    }


    inner class ColorViewHolder(viewItem: View)
        : RecyclerView.ViewHolder(viewItem), View.OnClickListener {

        private val name: TextView = viewItem.color_list_name
        private val primaryColorTxt: TextView = viewItem.color_list_primary
        private val primaryColorDarkTxt: TextView = viewItem.color_list_primaryDark
        private val accentColorTxt: TextView = viewItem.color_list_accent
        //layout
        private val linearLayout: ConstraintLayout = viewItem.color_list_linearLayout

        /*
         * set click listener
         */
        init {
            linearLayout.setOnClickListener(this)
        }

        /*
         * Methods for bind all view with latest value
         */
        fun bindTo(myTheme: MyTheme) {
            //exert from myTheme
            val primaryColor = getThemeColor(myTheme.id, R.attr.colorPrimary)
            val primaryColorDark = getThemeColor(myTheme.id, R.attr.colorPrimaryDark)
            val accentColor = getThemeColor(myTheme.id, R.attr.colorAccent)

            //set name
            name.text = myTheme.name
            name.setTextColor(primaryColor)

            //circle of color
            primaryColorTxt.background = getDrawablesWithColor(primaryColor)
            primaryColorDarkTxt.background = getDrawablesWithColor(primaryColorDark)
            accentColorTxt.background = getDrawablesWithColor(accentColor)
        }

        override fun onClick(v: View?) {
            clickListener.onItemClick(adapterPosition)
        }
    }
}