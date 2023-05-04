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
import com.example.mobileappasm.Child
import com.example.mobileappasm.R

class ChildAdapter (private val context: Context) : RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {
    private var child = emptyList<Child>()
    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_child, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val child = getItem(position)
        holder.bind(child)

        val currentChild = child[position]
        holder.itemView.setOnClickListener{
            val bundle = bundleOf("username" to currentChild.username)
            holder.itemView.findNavController().navigate(R.id., bundle)
        }
    }

    override fun getItemCount(): Int {
        return child.size
    }

    fun getItem(position: Int): Child {
        return child[position]
    }

    fun submitList(newChild: List<Child>) {
        child = newChild
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val childNumberView: TextView = itemView.findViewById(R.id.childNumberTextView)
        private val childNameView: TextView = itemView.findViewById(R.id.childNameTextView)
        private val childNationView: TextView = itemView.findViewById(R.id.childNationTextView)
        private val childDescView: TextView = itemView.findViewById(R.id.childDescTextView)
        private val childAgeView: TextView = itemView.findViewById(R.id.childAgeTextView)
        private val childTargetView: TextView = itemView.findViewById(R.id.childTargetTextView)
        private val totalReceivedView: TextView = itemView.findViewById(R.id.totalReceivedTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(position)
                }
            }
        }

        fun bind(child: Child) {
            childNumberView.text = (adapterPosition + 1).toString()
            childNameView.text = child.childName
            childNationView.text = child.childNation
            childDescView.text = child.child_Des
            childAgeView.text = child.childAge.toString()
            childTargetView.text = child.target.toString()
            totalReceivedView.text = child.totalReceived.toString()
        }
    }
}