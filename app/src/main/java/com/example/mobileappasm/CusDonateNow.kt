package com.example.mobileappasm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.paypal.android.sdk.payments.*
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal

class CusDonateNow : Fragment() {

    private lateinit var editAmount: EditText
    private lateinit var btnPayment: Button

    private val clientId = "AUAWnejP9yRYLJVtDEGtkut8kv8flF62x_vRcJk7VVmfQTjzxQuW-UoRQ4fiEKDnZUCEqEU82nZ0EoTx"
    private val PAYPAL_REQUEST_CODE = 123

    private lateinit var configuration: PayPalConfiguration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cus_donate_now, container, false)

        editAmount = view.findViewById(R.id.editAmount)
        btnPayment = view.findViewById(R.id.btnPayment)

        configuration = PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(clientId)

        val paypalServiceIntent = Intent(requireContext(), PayPalService::class.java)
        paypalServiceIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration)
        requireActivity().startService(paypalServiceIntent)

        btnPayment.setOnClickListener {
            getPayment()
        }

        return view
    }

    private fun getPayment() {
        val amountString = editAmount.text.toString()
        val amount = BigDecimal(amountString)

        val payment = PayPalPayment(amount, "MYR", "Child Support Organisation",
            PayPalPayment.PAYMENT_INTENT_SALE)

        val paymentIntent = Intent(requireContext(), PaymentActivity::class.java)
        paymentIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration)
        paymentIntent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)

        startActivityForResult(paymentIntent, PAYPAL_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val confirmation = data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                if (confirmation != null) {
                    try {
                        val paymentDetails = confirmation.toJSONObject().toString(4)
                        val jsonObject = JSONObject(paymentDetails)

                        // Do something with the payment details
                        // ...
                    } catch (e: JSONException) {
                        Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(requireContext(), "Payment canceled", Toast.LENGTH_SHORT).show()
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(requireContext(), "Invalid payment", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        requireActivity().stopService(Intent(requireContext(), PayPalService::class.java))
        super.onDestroy()
    }
}
