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
import com.example.mobileappasm.Admin
import com.example.mobileappasm.R

class AdminAdapter(private val context: Context) : RecyclerView.Adapter<AdminAdapter.AdminViewHolder>() {

    private var admins = emptyList<Admin>()
    private var listener: CustomerAdapter.OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin, parent, false)
        return AdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val admin = getItem(position)
        holder.bind(admin)

        val currentAdmin = admins[position]
        holder.itemView.setOnClickListener{
            val bundle = bundleOf("username" to currentAdmin.username)
            holder.itemView.findNavController().navigate(R.id.adminEditAdmin, bundle)
        }
    }

    override fun getItemCount(): Int {
        return admins.size
    }

    fun getItem(position: Int): Admin {
        return admins[position]
    }

    fun submitList(newAdmins: List<Admin>) {
        admins = newAdmins
        notifyDataSetChanged()
    }

    inner class AdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val adminUsernameView: TextView = itemView.findViewById(R.id.adminUsername)
        private val adminEmailView: TextView = itemView.findViewById(R.id.adminEmail)
        private val adminPositionView: TextView = itemView.findViewById(R.id.adminPosition)

        fun bind(admin: Admin) {
            adminUsernameView.text = admin.username
            adminEmailView.text = "Email : " + admin.email
            adminPositionView.text = "Position : " +  admin.position
        }
    }

    class AdminDiffCallback : DiffUtil.ItemCallback<Admin>() {
        override fun areItemsTheSame(oldItem: Admin, newItem: Admin): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: Admin, newItem: Admin): Boolean {
            return oldItem == newItem
        }
    }
}
