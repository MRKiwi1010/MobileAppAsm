package com.example.mobileappasm.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappasm.Customer
import com.example.mobileappasm.R

class CustomerAdapter(private val context: Context) :RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    private var users = emptyList<Customer>()
    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = getItem(position)
        holder.bind(customer)

        val currentCustomer = users[position]
        holder.itemView.setOnClickListener {
            val bundle = bundleOf("username" to currentCustomer.username)
            holder.itemView.findNavController().navigate(R.id.adminEditCus, bundle)
        }


    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun getItem(position: Int): Customer {
        return users[position]
    }

    fun submitList(newUsers: List<Customer>) {
        users = newUsers
        notifyDataSetChanged()
    }


    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val customerNumberView: TextView = itemView.findViewById(R.id.customerNumberTextView)
//        private val customerPasswordView: TextView = itemView.findViewById(R.id.customerPasswordTextView)
        private val customerNameView: TextView = itemView.findViewById(R.id.customerNameTextView)
        private val customerEmailView: TextView = itemView.findViewById(R.id.customerEmailTextView)
        private val customerUsernameView: TextView = itemView.findViewById(R.id.customerUsernameTextView)


        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(position)
                }
            }
        }

        fun bind(customer: Customer) {
//            customerNumberView.text = (adapterPosition + 1).toString()
            customerNameView.text =  customer.name
            customerEmailView.text = "Email : " + customer.email
//            customerPasswordView.text = "Password : " + customer.password
            customerUsernameView.text = "Username : " + customer.username
        }
    }

    class CustomerDiffCallback : DiffUtil.ItemCallback<Customer>() {
        override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem == newItem
        }
    }
}