package com.example.mobileappasm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel

class CusMainPage : Fragment() {

    private lateinit var readbutton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cus_main_page, container, false)
        val imageSlider: ImageSlider = view.findViewById(R.id.imageSlider)


        // Create custom SlideModel objects
        val slideModels = ArrayList<SlideModel>()
        slideModels.add(SlideModel(R.drawable.slideshow2, ScaleTypes.FIT))
        slideModels.add(SlideModel(R.drawable.slideshow1, ScaleTypes.FIT))
        slideModels.add(SlideModel(R.drawable.slideshow3, ScaleTypes.FIT))
        imageSlider.setImageList(slideModels)



        return view


    }
}
