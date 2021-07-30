package com.kproject.imagescopedstorage.iu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kproject.imagescopedstorage.databinding.RecyclerviewItemImageBinding
import com.kproject.imagescopedstorage.models.Image


class ImageListAdapter(
    private var imageList: List<Image>,
    private val onLongItemClickListener: ((book: Image) -> Unit)
) : RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, view: Int): ImageViewHolder {
        val binding = RecyclerviewItemImageBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun getItemCount() = imageList.size

    override fun onBindViewHolder(viewHolder: ImageViewHolder, position: Int) {
        viewHolder.bindView(imageList[position])
    }

    fun updateImageList(newImageList: List<Image>) {
        this.imageList = newImageList
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(
        private val binding: RecyclerviewItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(image: Image) {
            with (binding) {
                Glide.with(ivImage)
                    .load(image.contentUri)
                    .centerCrop()
                    .into(ivImage)
            }

            itemView.setOnLongClickListener {
                onLongItemClickListener(image)
                true
            }
        }
    }
}
