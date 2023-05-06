package com.example.mobileappasm.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappasm.Donation
import com.example.mobileappasm.R

class DonationAdapter(private val context: Context) :RecyclerView.Adapter<DonationAdapter.DonationViewHolder>() {
    private var donations = emptyList<Donation>()
    private var listener: DonationAdapter.OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donation2, parent, false)
        return DonationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val donation = getItem(position)
        holder.bind(donation)

        val currentDonation = donations[position]
        holder.itemView.setOnClickListener {
            val bundle = bundleOf("username" to currentDonation.username)
            holder.itemView.findNavController().navigate(R.id.adminViewDonationDetails, bundle)
        }
    }

    override fun getItemCount(): Int {
        return donations.size
    }

    fun getItem(position: Int): Donation {
        return donations[position]
    }

    fun submitList(newDonation: List<Donation>) {
        donations = newDonation
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class DonationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val donationNumberView: TextView = itemView.findViewById(R.id.donationNumberTextView)
        private val donationUsernameView: TextView = itemView.findViewById(R.id.donationUsernameTextView)
        private val donationChildNameView: TextView = itemView.findViewById(R.id.donationChildNameTextView)
        private val donationAmountView: TextView = itemView.findViewById(R.id.donationAmountTextView)
        private val donationDateView: TextView = itemView.findViewById(R.id.donationDateTextView)
        private val donationTimeView: TextView = itemView.findViewById(R.id.donationTimeTextView)
        private val donationBankTypeView: TextView = itemView.findViewById(R.id.donationBankTypeTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(position)
                }
            }
        }

        fun bind(donation: Donation) {
            donationNumberView.text = (adapterPosition + 1).toString()
            donationUsernameView.text = "Name" + donation.username
            donationChildNameView.text = String.format("Donation Child Name: %s", donation.childName)
            donationAmountView.text = "RM "+ donation.amount.toString()
            donationDateView.text = "Date"+ donation.date
            donationTimeView.text = "Time"+ donation.time
            donationBankTypeView.text = "Bank Type"+ donation.bankType
        }
    }
}