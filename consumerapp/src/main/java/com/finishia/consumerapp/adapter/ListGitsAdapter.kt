package com.finishia.consumerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.finishia.consumerapp.Gits
import com.finishia.consumerapp.R
import kotlinx.android.synthetic.main.activity_detail_github.view.*


class ListGitsAdapter(private val listGits: ArrayList<Gits>): RecyclerView.Adapter<ListGitsAdapter.ListViewHolder>() {

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        Companion.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_row_gits, viewGroup, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listGits[position])
        holder.itemView.setOnClickListener() {
            onItemClickCallback.onItemClicked(listGits[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int= listGits.size

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(gits: Gits) {
            with(itemView) {

                tv_item_name.text = gits.name
                tv_item_userName.text = gits.username
                Glide.with(this)
                    .load(gits.avatar)
                    .into(img_item_photo)
                tv_item_repository.text = gits.repository.toString()
                tv_item_follower.text = gits.follower.toString()
                tv_item_following.text = gits.following.toString()
                tv_item_company.text = gits.company
                tv_item_location.text = gits.location
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Gits)
    }

    companion object {
        private lateinit var onItemClickCallback: OnItemClickCallback
    }
}