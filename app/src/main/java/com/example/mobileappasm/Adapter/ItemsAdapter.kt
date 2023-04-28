package com.example.mobileappasm.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mobileappasm.Domain.ItemsDomain
import com.example.mobileappasm.R

class ItemsAdapter(private val itemsList: List<ItemsDomain>) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemImage: ImageView = itemView.findViewById(R.id.pic)
        val itemName: TextView = itemView.findViewById(R.id.childName)
        val itemNation: TextView = itemView.findViewById(R.id.childNation)
//        val itemDescription: TextView = itemView.findViewById(R.id.item_description)
//        val itemTarget: TextView = itemView.findViewById(R.id.item_target)
        val itemReceived: TextView = itemView.findViewById(R.id.totalReceived)
//        val itemAge: TextView = itemView.findViewById(R.id.item_age)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_viewholder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemsList[position]

        // Load the image using Glide library
        Glide.with(context)
            .load(currentItem.child_pic)
            .apply(RequestOptions().centerCrop())
            .into(holder.itemImage)

        holder.itemName.text = currentItem.child_name
        holder.itemNation.text = currentItem.childNation
//        holder.itemDescription.text = currentItem.child_Des
//        holder.itemAge.text = currentItem.child_age.toString()
//        holder.itemTarget.text = currentItem.target.toString()
        holder.itemReceived.text = currentItem.totalReceive.toString()
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}
