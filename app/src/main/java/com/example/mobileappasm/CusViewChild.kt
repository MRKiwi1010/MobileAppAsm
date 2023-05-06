package com.example.mobileappasm
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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
    private lateinit var button: Button
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
        button = view.findViewById(R.id.button)

        button.setOnClickListener {
            view.findNavController().navigate(R.id.cusDonateNow)
        }

        val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)
        val customerUsername = viewModel.getchildname()
        val childKey = viewModel.getChildKey()
        val username = customerUsername // replace with your actual users key
        childNameTxt.text = customerUsername


        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("child")
        val query: Query = databaseReference.orderByChild("childName").equalTo(username)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Iterate over the child nodes returned by the query
                for (childSnapshot in dataSnapshot.children) {
                    // Get the child information from the snapshot
                    val childAge = childSnapshot.child("childAge").value.toString()
                    val childName = childSnapshot.child("childName").value.toString()
                    val childNation = childSnapshot.child("childNation").value.toString()
                    val child_Des = childSnapshot.child("child_Des").value.toString()
                    val target = childSnapshot.child("target").value.toString()
                    val totalReceived = childSnapshot.child("totalReceived").value.toString()
                    val childUrl = childSnapshot.child("childUrl").value.toString()

                    // Set the retrieved information to the respective TextViews
                    childAgeTxt.text =  childAge + " Years Old"
                    childNameTxt.text = childName
                    childNationTxt.text = childNation
                    descriptionTxt.text = child_Des
                    priceTxt13.text = "RM"+totalReceived
                    targetTxt.text = "RM"+target

                    Glide.with(requireContext()).load(childUrl).into(picTxt)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Rename the fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Child"

        setHasOptionsMenu(true)

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
