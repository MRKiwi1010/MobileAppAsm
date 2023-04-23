package com.example.mobileappasm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappasm.Adapter.ItemsAdapter
import com.example.mobileappasm.Domain.ItemsDomain

class CusMainPage : Fragment() {

    private lateinit var recyclerViewPopular: RecyclerView
    private lateinit var recyclerViewNew: RecyclerView
    private lateinit var adapterPopular: RecyclerView.Adapter<*>
    private lateinit var adapterNew: RecyclerView.Adapter<*>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cus_main_page, container, false)
        recyclerViewPopular = view.findViewById(R.id.viewPupolar)
        recyclerViewNew = view.findViewById(R.id.viewNew)
        recyclerViewPopular.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewNew.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val itemsArraylist = ArrayList<ItemsDomain>()
        itemsArraylist.add(
            ItemsDomain(
                "House with a great view", "San Francisco, CA 94110",
                """This 2 bed /1 bath home boasts an enormous,
 open-living plan, accented by striking 
architectural features and high-end finishes.
 Feel inspired by open sight lines that
 embrace the outdoors, crowned by stunning
 coffered ceilings. """, 2, 1, 841456, "pic1", true
            )
        )
        itemsArraylist.add(
            ItemsDomain(
                "House with a great view", "San Francisco, CA 94110",
                """This 2 bed /1 bath home boasts an enormous,
 open-living plan, accented by striking 
architectural features and high-end finishes.
 Feel inspired by open sight lines that
 embrace the outdoors, crowned by stunning
 coffered ceilings. """, 3, 1, 654987, "pic2", false
            )
        )
        itemsArraylist.add(
            ItemsDomain(
                "House with a great view", "San Francisco, CA 94110",
                """This 2 bed /1 bath home boasts an enormous,
 open-living plan, accented by striking 
architectural features and high-end finishes.
 Feel inspired by open sight lines that
 embrace the outdoors, crowned by stunning
 coffered ceilings. """, 3, 1, 841456, "pic1", true
            )
        )

        adapterNew = ItemsAdapter(itemsArraylist)
        adapterPopular = ItemsAdapter(itemsArraylist)

        recyclerViewNew.adapter = adapterNew
        recyclerViewPopular.adapter = adapterPopular

        return view
    }
}
