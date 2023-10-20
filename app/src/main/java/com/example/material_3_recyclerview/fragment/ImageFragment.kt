package com.example.material_3_recyclerview.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.material_3_recyclerview.R

class ImageFragment : Fragment() {



    companion object{
         val KEY_IMAGE_RES = "com.example.material_3_recyclerview.key.imageRes"

        fun newInstance(@DrawableRes drawableRes: Int): ImageFragment {
            val fragment = ImageFragment()
            val argument = Bundle()
            argument.putInt(KEY_IMAGE_RES, drawableRes)
            fragment.arguments = argument
            return fragment
        }
    }

    @Nullable
    override fun onCreateView(
        @NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image, container, false)
        val arguments = arguments

        @DrawableRes
        val imageRes = arguments?.getInt(KEY_IMAGE_RES) ?: 0

        if (imageRes != 0) {
            view?.findViewById<View>(R.id.image)?.transitionName = imageRes.toString()

            Glide.with(this)
                .load(imageRes)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        @Nullable e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        parentFragment?.startPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        parentFragment?.startPostponedEnterTransition()
                        return false
                    }
                })
                .into(view?.findViewById<View>(R.id.image) as ImageView)
        } else {
            // Handle the case where imageRes is 0 (or any other appropriate action)
        }
        return view
    }
}