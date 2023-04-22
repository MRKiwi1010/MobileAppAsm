package com.example.mobileappasm.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobileappasm.CusViewChild
import com.example.mobileappasm.Domain.ItemsDomain
import com.example.mobileappasm.R
import java.text.DecimalFormat

class ItemsAdapter(private val items: ArrayList<ItemsDomain>) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    private val formatter = DecimalFormat("###,###,###,###.##")
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate: View = LayoutInflater.from(parent.context).inflate(R.layout.item_viewholder, parent, false)
        context = parent.context
        return ViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleTxt.text = items[position].title
        holder.addressTxt.text = items[position].address
        holder.priceTxt.text = "$" + formatter.format(items[position].price.toLong())
        val drawableResourceId = holder.itemView.resources.getIdentifier(
            items[position].pic,
            "drawable",
            holder.itemView.context.packageName
        )
        Glide.with(holder.itemView.context)
            .load(drawableResourceId)
            .into(holder.pic)
        holder.itemView.setOnClickListener { v: View? ->
            val intent = Intent(context, CusViewChild::class.java)
            intent.putExtra("object", items[position])
            context!!.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTxt: TextView = itemView.findViewById<TextView>(R.id.titleTxt)
        val addressTxt: TextView = itemView.findViewById<TextView>(R.id.addressTxt)
        val priceTxt: TextView = itemView.findViewById<TextView>(R.id.priceTxt)
        val pic: ImageView = itemView.findViewById<ImageView>(R.id.pic)
    }
}
