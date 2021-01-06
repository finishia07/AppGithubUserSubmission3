package com.finishia.consumerapp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.finishia.consumerapp.Gits
import com.finishia.consumerapp.R
import kotlinx.android.synthetic.main.item_main.view.*


class FavoriteAdapter(private val activity: Activity): RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    internal var listGits = arrayListOf<Gits>()
        set(listGits) {
            if (listGits.size > 0) {
                this.listGits.clear()
            }
            this.listGits.addAll(listGits)
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(listGits[position])
        holder.itemView.setOnClickListener {onItemClickCallback.onItemClicked(listGits[position])
        }
    }

    override fun getItemCount(): Int = this.listGits.size

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(gits: Gits) {

            with(itemView) {
                tv_item_userName.text = gits.username
                tv_item_name.text = gits.name
                Glide.with(this)
                    .load(gits.avatar)
                    .into(img_item_photo)
                tv_item_location.text = gits.location
            }
        }
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Gits)
    }
}