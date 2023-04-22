package com.example.mobileappasm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class CusDonatePersonalDetails : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cus_donate_personal_details, container, false)

        val context: Context? = activity
        val flowerName = arrayOf("Rose","Lotus","Lily","Jasmine",
            "Tulip","Orchid","Levender","RoseMarry","Sunflower","Carnation")
        val image = intArrayOf(R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,
            R.drawable.h,R.drawable.i,R.drawable.j)

        val gridView = view.findViewById<GridView>(R.id.gridView)
        gridView.adapter = CustomGridAdapter(context, flowerName, image)

        gridView.setOnItemClickListener { _, _, position, _ ->
            // Handle click event for each item in the grid view
            val selectedFlower = flowerName[position]
            // Do something with the selected flower
        }

        return view
    }

    class CustomGridAdapter(context: Context?, private val flowerName: Array<String>, private val image: IntArray) :
        ArrayAdapter<String>(context!!, R.layout.cus_view_child_grid, flowerName) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val viewHolder: ViewHolder

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.cus_view_child_grid, parent, false)

                viewHolder = ViewHolder()
                viewHolder.imageView = convertView.findViewById(R.id.grid_image)
                viewHolder.textView = convertView.findViewById(R.id.item_name)

                convertView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ViewHolder
            }

            viewHolder.imageView.setImageResource(image[position])
            viewHolder.textView.text = flowerName[position]

            return convertView!!
        }

        private class ViewHolder {
            lateinit var imageView: ImageView
            lateinit var textView: TextView
        }
    }

}
