package com.example.mobileappasm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class CusDonatePersonalDetails : Fragment() {
    private lateinit var childName: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cus_donate_personal_details, container, false)


        val context: Context? = activity
        val database = FirebaseDatabase.getInstance().reference
        val childTable = database.child("child")
        childTable.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val childNames = ArrayList<String>()
                val imageUrls = ArrayList<String>()

                for (childSnapshot in dataSnapshot.children) {
                    val childName = childSnapshot.child("childName").value as String
                    val childImageUrl = childSnapshot.child("childUrl").value as String
                    childNames.add(childName)
                    imageUrls.add(childImageUrl)
                }

                val gridView = view.findViewById<GridView>(R.id.gridView)
                gridView.adapter = CustomGridAdapter(activity, childNames, imageUrls)

                gridView.setOnItemClickListener { _, _, position, _ ->
                    // Handle click event for each item in the grid view
                    val selectedChild = childNames[position]
// Navigate to the CusViewChild fragment and pass the selected childName
                    val bundle = Bundle()
                    bundle.putString("childName", selectedChild)
                    val fragment = CusMainPage()
                    fragment.arguments = bundle
                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.cusMainPage, fragment)?.addToBackStack(null)?.commit()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read error
            }
        })

        // Get the childName value from arguments
        childName = arguments?.getString("childName") ?: ""

        return view
    }

    class CustomGridAdapter(
        private val activity: FragmentActivity?,
        private val childNames: ArrayList<String>,
        private val imageUrls: ArrayList<String>
    ) :
        ArrayAdapter<String>(activity!!, R.layout.cus_view_child_grid, childNames) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val viewHolder: ViewHolder

            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.cus_view_child_grid, parent, false)

                viewHolder = ViewHolder()
                viewHolder.imageView = convertView.findViewById(R.id.grid_image)
                viewHolder.textView = convertView.findViewById(R.id.item_name)
                viewHolder.button = convertView.findViewById(R.id.button2)

                convertView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ViewHolder
            }

            viewHolder.textView.text = childNames[position]

            // Load image from Firebase storage based on the URL
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrls[position])
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(activity!!)
                    .load(uri)
                    .into(viewHolder.imageView)
            }

            viewHolder.button.setOnClickListener {
                val selectedChild = childNames[position]
                val bundle = Bundle()
                bundle.putString("childName", selectedChild)
                val fragment = CusMainPage()
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.cusMainPage,fragment)?.addToBackStack(null)?.commit()
            }

            return convertView!!
        }

        private class ViewHolder {
            lateinit var imageView: ImageView
            lateinit var textView: TextView
            lateinit var button: Button
        }
    }
}

