package com.rmd.ecommerce.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.rmd.ecommerce.R
import com.rmd.ecommerce.databinding.RowCategoryBinding
import com.rmd.ecommerce.model.Category
import com.rmd.ecommerce.sharedviewmodel.CategoryViewModel
import com.squareup.picasso.Picasso

class CategoryRecyclerView(
    var mActivity: FragmentActivity,
    var categoryList: ArrayList<Category>
) :
    RecyclerView.Adapter<CategoryRecyclerView.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private lateinit var sharedViewModelCategory: CategoryViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_category, parent, false)

        sharedViewModelCategory =
            ViewModelProvider(mActivity)[CategoryViewModel::class.java]
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        val binding = RowCategoryBinding.bind(holder.itemView)

        holder.itemView.apply {
            binding.categoryNameTv.text = categoryList[position].name
            Picasso.get().load(categoryList[position].imageUrl).into(binding.categoryImv)
        }

        holder.itemView.setOnClickListener {
            sharedViewModelCategory.setSelectedCategoryName(categoryList[position].name)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}