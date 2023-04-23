package com.example.mobileappasm
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mobileappasm.Domain.ItemsDomain
import java.text.DecimalFormat

class CusViewChild : Fragment() {

    private lateinit var titleTxt: TextView
    private lateinit var addressTxt: TextView
    private lateinit var bedTxt: TextView
    private lateinit var bathTxt: TextView
    private lateinit var wifiTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var priceTxt: TextView
    private var item: ItemsDomain? = null
    private lateinit var pic: ImageView
    private val formatter = DecimalFormat("###,###,###.##")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cus_view_child, container, false)
        titleTxt = view.findViewById(R.id.titleTxt)
        addressTxt = view.findViewById(R.id.addressTxt)
        bedTxt = view.findViewById(R.id.bedTxt)
        bathTxt = view.findViewById(R.id.bathTxt)
        wifiTxt = view.findViewById(R.id.wifiTxt)
        descriptionTxt = view.findViewById(R.id.descriptionTxt)
        pic = view.findViewById(R.id.pic)
        priceTxt = view.findViewById(R.id.priceTxt)

        item?.pic?.let {
            val drawableResourceId = resources.getIdentifier(it, "drawable", context?.packageName)
            Glide.with(this)
                .load(drawableResourceId)
                .into(pic)
        }
        return view
    }

}
