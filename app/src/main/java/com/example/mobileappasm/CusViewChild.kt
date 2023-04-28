package com.example.mobileappasm
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mobileappasm.Domain.ItemsDomain
import com.example.mobileappasm.data.model.cusViewModel
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class CusViewChild : Fragment() {
    private lateinit var  childAgeTxt : TextView
    private lateinit var childNameTxt: TextView
    private lateinit var childNationTxt: TextView
    private lateinit var targetTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var priceTxt13: TextView
    private lateinit var textView142: TextView
    private lateinit var picTxt: ImageView
    private var item: ItemsDomain? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_cus_view_child, container, false)
        childAgeTxt = view.findViewById(R.id.childAge)
        childNameTxt = view.findViewById(R.id.childName)
        childNationTxt = view.findViewById(R.id.childNation)
        descriptionTxt = view.findViewById(R.id.descriptionTxt)
        targetTxt = view.findViewById(R.id.priceTxt23)
        priceTxt13 = view.findViewById(R.id.priceTxt13)
        textView142 = view.findViewById(R.id.textView142)
        picTxt = view.findViewById(R.id.picTxt)

        val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)
        val childname = viewModel.getchildname()

            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("child")
            val childKey = childname // replace with your actual child key
            val childReference = databaseReference.child(childKey)


            childReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get child information from Firebase Realtime Database
                    val childAge = dataSnapshot.child("childAge").value.toString()
                    val childName = dataSnapshot.child("childName").value.toString()
                    val childNation = dataSnapshot.child("childNation").value.toString()
                    val child_Des = dataSnapshot.child("child_Des").value.toString()
                    val target = dataSnapshot.child("target").value.toString()
                    val totalReceived = dataSnapshot.child("totalReceived").value.toString()
                    val childUrl = dataSnapshot.child("childUrl").value.toString()

                    // Set the retrieved information to the respective TextViews
                    childAgeTxt.text =  childAge + " Years Old"
                    childNameTxt.text = childName
                    childNationTxt.text = childNation
                    descriptionTxt.text = child_Des
                    priceTxt13.text = "RM"+totalReceived
                    targetTxt.text = "RM"+target

                    Glide.with(requireContext()).load(childUrl).into(picTxt)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })

        return view
    }

}