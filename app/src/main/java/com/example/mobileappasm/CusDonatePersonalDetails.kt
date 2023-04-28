package com.example.mobileappasm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mobileappasm.data.model.cusViewModel
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
                    val childName = childNames[position]
                    val childKey = dataSnapshot.children.elementAt(position).key

                    // Navigate to the CusViewChild fragment and pass the selected childName and childKey
                    val viewModel = ViewModelProvider(context as FragmentActivity).get(cusViewModel::class.java)
                    viewModel.setchildname(childName)
                    if (childKey != null) {
                        viewModel.setChildKey(childKey)
                    }

                    val fragment = CusViewChild()
                    val transaction = (context).supportFragmentManager.beginTransaction()
                    val containerId = R.id.myNavHostFragment

                    val container = (context).findViewById<ViewGroup>(containerId)
                    container.removeAllViews()

                    transaction.replace(containerId, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
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
                val childname = childNames[position]
                val viewModel = ViewModelProvider(context as FragmentActivity).get(cusViewModel::class.java)
                viewModel.setchildname(childname)

//            val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)
                val fragment = CusViewChild()
                val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()

// Get the ID of the fragment container
                val containerId = R.id.myNavHostFragment

// Remove all views from the parent ViewGroup of the fragment container
                val container = (context as FragmentActivity).findViewById<ViewGroup>(containerId)
                container.removeAllViews()

// Replace the current fragment with the new one
                transaction.replace(containerId, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
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

