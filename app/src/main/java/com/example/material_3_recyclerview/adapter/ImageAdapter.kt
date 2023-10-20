package com.example.material_3_recyclerview.adapter



import android.content.Context
import android.graphics.drawable.Drawable
import android.transition.TransitionSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.material_3_recyclerview.MainActivity
import com.example.material_3_recyclerview.R
import com.example.material_3_recyclerview.databinding.ImageListItemBinding
import com.example.material_3_recyclerview.fragment.ImageData
import com.example.material_3_recyclerview.fragment.ImageData.IMAGE_DRAWABLES
import com.example.material_3_recyclerview.fragment.ImageFragment

import java.util.concurrent.atomic.AtomicBoolean

/*object ImageData {
    @DrawableRes
    val IMAGE_DRAWABLES = intArrayOf(
        R.drawable.animal_2024172,
        R.drawable.beetle_562035,
        R.drawable.bug_189903,
        R.drawable.butterfly_417971,
        R.drawable.butterfly_dolls_363342,
        R.drawable.dragonfly_122787,
        R.drawable.dragonfly_274059,
        R.drawable.dragonfly_689626,
        R.drawable.grasshopper_279532,
        R.drawable.hover_fly_61682,
        R.drawable.hoverfly_546692,
        R.drawable.insect_278083,
        R.drawable.morpho_43483,
        R.drawable.nature_95365
    )
}*/

class ImageAdapter(private val fragment: Fragment) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private val requestManager: RequestManager = Glide.with(fragment)
    private val viewHolderListener: ViewHolderListener = ViewHolderListenerImpl(fragment)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_list_item, parent, false)
        return ViewHolder(view, requestManager, viewHolderListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        return ImageData.IMAGE_DRAWABLES.size
    }

    interface ViewHolderListener {
        fun onLoadCompleted(view: ImageView, adapterPosition: Int)
        fun onItemClicked(view: View, adapterPosition: Int)
    }

    private class ViewHolderListenerImpl(private val fragment: Fragment) : ViewHolderListener{
        override fun onLoadCompleted(view: ImageView, position: Int) {

             val enterTransitionStarted = AtomicBoolean()

            if (MainActivity.currentPosition != position) {
                return
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return
            }
            fragment.startPostponedEnterTransition()
        }

        override fun onItemClicked(view: View, position: Int) {
            MainActivity.currentPosition = position
            (fragment.exitTransition as? TransitionSet)?.excludeTarget(view, true)

            val transitioningView = view.findViewById<ImageView>(R.id.card_image)
            fragment.parentFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .addSharedElement(transitioningView, transitioningView.transitionName)
                .replace(R.id.containerCarousel, ImageFragment(), ImageFragment::class.simpleName)
                .addToBackStack(null)
                .commit()

            /*val transitioningView = view.findViewById<ImageView>(R.id.card_image)
            fragment.fragmentManager?.beginTransaction()
                ?.setReorderingAllowed(true)
                ?.addSharedElement(transitioningView, transitioningView.transitionName)
                ?.replace(R.id.containerCarousel, ImageFragment(), ImageFragment::class.simpleName)
                ?.addToBackStack(null)
                ?.commit()*/
        }

    }

    class ViewHolder(itemView: View, private val requestManager: RequestManager, private val viewHolderListener: ViewHolderListener) :
        RecyclerView.ViewHolder(itemView),View.OnClickListener {

        private val binding: ImageListItemBinding = ImageListItemBinding.bind(itemView)

        init {
            binding.cardView.setOnClickListener(this)
        }

        fun onBind() {
            val adapterPosition = adapterPosition
            setImage(adapterPosition)
            binding.cardImage.transitionName = ImageData.IMAGE_DRAWABLES[adapterPosition].toString()
        }

        private fun setImage(imageDrawableId: Int) {
            requestManager
                .load(IMAGE_DRAWABLES[imageDrawableId])
                .listener(object : RequestListener<Drawable> {

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target:Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewHolderListener.onLoadCompleted(binding.cardImage, adapterPosition)
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewHolderListener.onLoadCompleted(binding.cardImage, adapterPosition)
                        return false
                    }
                })
                .into(binding.cardImage)

        }

        override fun onClick(view: View) {
            viewHolderListener.onItemClicked(view, adapterPosition)
        }


    }

}


