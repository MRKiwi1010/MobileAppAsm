package com.example.mobileappasm.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappasm.Child
import com.example.mobileappasm.R

class ChildAdapter (private val context: Context) : RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {
    private var child = emptyList<Child>()
    private var listener: ChildAdapter.OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val child = getItem(position)
        holder.bind(child)
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
        private val childIdView: TextView = itemView.findViewById(R.id.childIdTextView)
        private val childNameView: TextView = itemView.findViewById(R.id.childNameTextView)
        private val childNationView: TextView = itemView.findViewById(R.id.childNationTextView)
        private val childAgeView: TextView = itemView.findViewById(R.id.childAgeTextView)
        private val durationLeftView: TextView = itemView.findViewById(R.id.durationLeftTextView)
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
            childIdView.text = child.childId.toString()
            childNameView.text = child.childName
            childNationView.text = child.childNation
            childAgeView.text = child.childAge.toString()
            durationLeftView.text = child.durationLeft.toString()
            totalReceivedView.text = child.totalReceived.toString()
        }
    }

    class ChildDiffCallback : DiffUtil.ItemCallback<Child>() {
        override fun areItemsTheSame(oldItem: Child, newItem: Child): Boolean {
            return oldItem.childId == newItem.childId
        }

        override fun areContentsTheSame(oldItem: Child, newItem: Child): Boolean {
            return oldItem == newItem
        }
    }
}