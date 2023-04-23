package com.example.mobileappasm.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.mobileappasm.R

class GridAdapter(private val context: Context?, private val flowerName: Array<String>, private val image: IntArray) : BaseAdapter() {

    private var inflater: LayoutInflater? = null

    override fun getCount(): Int {
        return flowerName.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        if (inflater == null) inflater =
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (convertView == null) {
            convertView = inflater!!.inflate(R.layout.cus_view_child_grid, null)
        }
        val imageView = convertView!!.findViewById<ImageView>(R.id.grid_image)
        val textView = convertView.findViewById<TextView>(R.id.item_name)
        imageView.setImageResource(image[position])
        textView.text = flowerName[position]
        return convertView
    }
}
