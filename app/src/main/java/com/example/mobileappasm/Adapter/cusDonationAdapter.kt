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

class cusDonationAdapter(private val context: Context) : RecyclerView.Adapter<cusDonationAdapter.DonationViewHolder>() {
    private var donations = emptyList<Donation>()
    private var listener: cusDonationAdapter.OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.item_donation2, parent, false)
        return DonationViewHolder(itemView)
    }

        override fun onBindViewHolder(holder: cusDonationAdapter.DonationViewHolder, position: Int) {
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
                donationChildNameView.text = donation.childName
                donationAmountView.text = donation.amount.toString()
                donationDateView.text = donation.date
                donationTimeView.text = donation.time
                donationBankTypeView.text = donation.bankType
            }
        }
}