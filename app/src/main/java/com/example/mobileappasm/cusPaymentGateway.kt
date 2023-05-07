package com.example.mobileappasm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mobileappasm.data.model.cusViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.sql.Time
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class cusPaymentGateway : Fragment() {
    private lateinit var textView13: TextView
    private lateinit var editTextCardNo : EditText
    private lateinit var expDate :EditText
    private lateinit var cvv :EditText
    private lateinit var spinnerBank :Spinner
    private lateinit var imageViewff : ImageView
    private lateinit var textDetail: TextView
    private lateinit var textDetail2: TextView
    private lateinit var btnPayment: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cus_payment_gateway, container, false)
        textView13 = view.findViewById(R.id.textView13)
        editTextCardNo = view.findViewById(R.id.editTextCardNo)
        expDate = view.findViewById(R.id.expDate)
        cvv = view.findViewById(R.id.cvv)
        spinnerBank = view.findViewById(R.id.spinnerBank)
        imageViewff = view.findViewById(R.id.imageViewff)
        textDetail = view.findViewById(R.id.textDetail)
        textDetail2 = view.findViewById(R.id.textDetail2)
        btnPayment = view.findViewById(R.id.btnPayment)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val spinnerBank = view.findViewById<Spinner>(R.id.spinnerBank)
        val amount123 = arguments?.getString("paymentAmount")
        textView13.text = "RM " + amount123
        val viewModel = ViewModelProvider(requireActivity()).get(cusViewModel::class.java)
        val customerUsername = viewModel.getchildname()
        val username1 = customerUsername // replace with your actual users key
        textDetail.text = customerUsername
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("child")
        val query: Query = databaseReference.orderByChild("childName").equalTo(username1)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Iterate over the child nodes returned by the query
                for (childSnapshot in dataSnapshot.children) {
                    val childName = childSnapshot.child("childName").value.toString()
                    val childNation = childSnapshot.child("childNation").value.toString()
                    val childUrl = childSnapshot.child("childUrl").value.toString()
                    textDetail.text = childName
                    textDetail2.text = childNation
                    if (isAdded && !isDetached && !isRemoving) {
                        Glide.with(requireContext()).load(childUrl).into(imageViewff)
                    }}
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        var paymentId = ""
        btnPayment.setOnClickListener {

            database.child("payment").orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot2: DataSnapshot) {
                    var paymentCount = 1
                    for(paymentSnapshot in snapshot2.children)
                    {
                        paymentId = paymentSnapshot.key.toString()
                        val currentCount = paymentId.substring(7).toInt()
                        if (currentCount >= paymentCount) {
                            paymentCount = currentCount + 1
                        }
                    }
                    paymentId = "payment" + "%03d".format(paymentCount)
                }

                override fun onCancelled(error: DatabaseError) {
                    paymentId = "payment001"
                }
            })
            val paymentRef = FirebaseDatabase.getInstance().getReference("payment")
            paymentRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newPaymentRef = paymentRef.child(paymentId)
                    val bankType = spinnerBank.selectedItem.toString()
                    val amount = amount123?.toDouble() ?: 0.0
                    val cardCVV = cvv.text.toString()
                    val cardExp = expDate.text.toString()
                    val cardNo = editTextCardNo.text.toString()
                    val childName = viewModel.getchildname()
                    val date = Date()
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(date)
                    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    val formattedTime = timeFormat.format(date)
                    val username = viewModel.getCustomerUsername()

                    if (!isValidCardNumber(cardNo)) {
                        editTextCardNo.error = "Invalid card number"
                        return
                    }
                    if (!isValidExpiryDate(cardExp)) {
                        expDate.error = "Invalid expiry date (MM/yy)"
                        return
                    }
                    if (!isValidCVV(cardCVV)) {
                        cvv.error = "Invalid CVV (3 digits)"
                        return
                    }

                    if(isValidCardNumber(cardNo)&&isValidExpiryDate(cardExp)&&isValidCVV(cardCVV)){
                        val payment = Payment(bankType, amount, cardCVV, cardExp, cardNo, childName, formattedDate,formattedTime, username)
                        newPaymentRef.setValue(payment)


                            val childRef = FirebaseDatabase.getInstance().getReference("child").orderByChild("childName").equalTo(childName)
                            childRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        val childSnapshot = snapshot.children.firstOrNull()
                                        childSnapshot?.let {
                                            val childKey = it.key
                                            val child = it.getValue(Child::class.java)
                                            val totalReceived = child?.totalReceived ?: 0.0
                                            val amountinput = arguments?.getString("paymentAmount")
                                            val newTotalReceived = totalReceived + (amountinput?.toDoubleOrNull() ?: 0.0)
                                            FirebaseDatabase.getInstance().getReference("child").child(childKey ?: "").child("totalReceived").setValue(newTotalReceived)

//                                            val newTotalReceived =
//                                                totalReceived + (amountinput?.toIntOrNull()
//                                                    ?: 0.0).toDouble()
//                                            FirebaseDatabase.getInstance().getReference("child")
//                                                .child(childKey ?: "").child("totalReceived")
//                                                .setValue(newTotalReceived)
                                        }
                                    }
                                }
                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                        view.findNavController().navigate(R.id.cusPaymentDone)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Payment"
        setHasOptionsMenu(true)
        return view
    }
    fun isValidCardNumber(cardNumber: String): Boolean {
        return cardNumber.trim().matches("^\\d{16}$".toRegex())
    }
    fun isValidExpiryDate(expiryDate: String): Boolean {
        return expiryDate.trim().matches("^(0[1-9]|1[0-2])/(\\d{2})$".toRegex())
    }
    fun isValidCVV(cvv: String): Boolean {
        return cvv.trim().matches("^\\d{3}$".toRegex())
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
