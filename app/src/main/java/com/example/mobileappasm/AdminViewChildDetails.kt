package com.example.mobileappasm

/*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mobileappasm.databinding.FragmentChildDetailBinding
import com.google.firebase.database.*

private const val ARG_CHILD_ID = "child_id"

class AdminViewChildDetails : Fragment() {

    private lateinit var binding: FragmentChildDetailBinding
    private lateinit var database: DatabaseReference
    private var childId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            childId = it.getString(ARG_CHILD_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChildDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childId?.let {
            database = FirebaseDatabase.getInstance().reference.child("child").child(it)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val child = dataSnapshot.getValue(Child::class.java)
                    child?.let { setupChildDetails(it) }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load child data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setupChildDetails(child: Child) {
        binding.apply {
            Glide.with(childImageView)
                .load(child.childUrl)
                .into(childImageView)
            childName.text = child.childName
            childNation.text = child.childNation
            childAge.text = child.childAge.toString()
            childDesc.text = child.childDesc
            childTarget.text = child.childTarget.toString()
            totalReceived.text = child.totalReceived.toString()
        }
    }

    companion object {
        private const val ARG_CHILD_ID = "child_id"

        @JvmStatic
        fun newInstance(childId: String) =
            AdminViewChildDetails().apply {
                arguments = Bundle().apply {
                    putString(ARG_CHILD_ID, childId)
                }
            }
    }
}
*/