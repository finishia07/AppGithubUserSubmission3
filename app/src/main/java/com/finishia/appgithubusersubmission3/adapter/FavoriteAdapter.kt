package com.finishia.appgithubusersubmission3.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.finishia.appgithubusersubmission3.R
import com.finishia.appgithubusersubmission3.entity.Gits
import kotlinx.android.synthetic.main.item_favorite.view.*

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
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

    interface OnItemClickCallback : ListGitsAdapter.OnItemClickCallback {
        override fun onItemClicked(data: Gits)
    }
}