package com.rmd.ecommerce.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmd.ecommerce.R
import com.rmd.ecommerce.databinding.RowSubcategoryBinding
import com.rmd.ecommerce.model.SubCategory
import com.squareup.picasso.Picasso

class SubCategoryRecyclerView(var subCategoryList: ArrayList<SubCategory>) :
    RecyclerView.Adapter<SubCategoryRecyclerView.SubCategoryViewHolder>() {

    inner class SubCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_subcategory, parent, false)
        return SubCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {

        val binding = RowSubcategoryBinding.bind(holder.itemView)

        holder.itemView.apply {
            binding.subCategoryNameTv.text = subCategoryList[position].name
            Picasso.get().load(subCategoryList[position].imageUrl).into(binding.subCategoryImv)
        }
    }

    override fun getItemCount(): Int {
        return subCategoryList.size
    }
}