package com.example.mobileappasm

import android.R
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.mobileappasm.databinding.FragmentAdminAddChildBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminAddChild : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding: FragmentAdminAddChildBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAdminAddChildBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val asiaCountries = arrayOf("China", "India", "Japan", "Indonesia", "Vietnam", "Philippines", "Thailand", "Pakistan", "Bangladesh", "South Korea")
        val adapter = ArrayAdapter<String>(requireContext(), R.layout.simple_spinner_item, asiaCountries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.childNationSpinner.adapter = adapter


        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val btnAddChild = binding.btnAddChild
        btnAddChild.setOnClickListener {
            database.child("child").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val childCount = snapshot.childrenCount.toInt() + 1
                    val childId = "child" + "%03d".format(childCount)

                    val name = binding.childName.text.toString()
                    val nation = binding.childNationSpinner.selectedItem as String
                    val age = binding.childAge.text.toString()
                    val durationLeft = binding.childDurationLeft.text.toString()
                    val totalReceived = 0;

                    val child = Child(
                        name,
                        nation,
                        age.toInt(),
                        totalReceived.toDouble(),
                        durationLeft.toInt()
                        )
                    val childUpdates = HashMap<String, Any>()
                    childUpdates[childId] = child

                    database.child("child").updateChildren(childUpdates)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Added Successfully!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener{
                            Toast.makeText(requireContext(),"Failed", Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "Failed", it)
                        }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(),"Failed", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    companion object {
        private const val TAG = "AdminAddChild"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminAddChild().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
