package com.kotlin.kiumee.presentation.store

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.kiumee.core.util.context.pxToDp

class StoreItemDecorator(val context: Context) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)

        if (position == 0) {
            outRect.left = context.pxToDp(28)
        } else {
            outRect.left = context.pxToDp(2)
        }
        outRect.right = context.pxToDp(28)
    }
}
