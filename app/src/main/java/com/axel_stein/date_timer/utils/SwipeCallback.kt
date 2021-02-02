package com.axel_stein.date_timer.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlin.math.roundToInt

class SwipeCallback(private val context: Context) : ItemTouchHelper.SimpleCallback(0, 0) {
    private val backgroundLeft = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundRight = Paint(Paint.ANTI_ALIAS_FLAG)
    var iconLeft: Drawable? = null
    var iconRight: Drawable? = null
    var swipeLeftEnabled = false
    var swipeRightEnabled = false
    var onSwipeLeft: ((position: Int) -> Unit)? = null
    var onSwipeRight: ((position: Int) -> Unit)? = null

    fun setSwipeRightColor(color: Int) {
        backgroundLeft.color = color
    }

    fun setSwipeRightColorRes(colorRes: Int) {
        backgroundLeft.color = ContextCompat.getColor(context, colorRes)
    }

    fun setSwipeRightIconRes(iconRes: Int) {
        iconLeft = ContextCompat.getDrawable(context, iconRes)
    }

    fun setSwipeLeftColor(color: Int) {
        backgroundRight.color = color
    }

    fun setSwipeLeftColorRes(colorRes: Int) {
        backgroundRight.color = ContextCompat.getColor(context, colorRes)
    }

    fun setSwipeLeftIconRes(iconRes: Int) {
        iconRight = ContextCompat.getDrawable(context, iconRes)
    }

    var iconMargin = 0f
        set(value) {
            field = value.intoPx(context)
        }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        target: ViewHolder
    ): Boolean {
        return true
    }

    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
        val l = if (swipeLeftEnabled) LEFT else 0
        val r = if (swipeRightEnabled) RIGHT else 0
        return l or r
    }

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        when (direction) {
            LEFT -> onSwipeLeft?.invoke(pos)
            RIGHT -> onSwipeRight?.invoke(pos)
        }
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val view = viewHolder.itemView
        val rect = view.getRect()

        val margin = iconMargin.toInt()
        val icon = if (dX > 0) iconLeft else iconRight
        val iconWidth = icon?.intrinsicWidth ?: 0
        val iconHeight = icon?.intrinsicHeight ?: 0
        val boundTop = rect.top + (rect.height() - iconHeight) / 2
        val boundBottom = boundTop + iconHeight
        val boundLeft = if (dX > 0) margin else rect.right - margin - iconWidth
        val boundRight = boundLeft + iconWidth

        val paint = if (dX > 0) {
            rect.right = dX.roundToInt()
            backgroundLeft
        } else {
            rect.left = rect.right + dX.roundToInt()
            backgroundRight
        }

        canvas.drawRect(rect, paint)
        icon?.setBounds(boundLeft, boundTop, boundRight, boundBottom)
        icon?.draw(canvas)

        view.elevation = if (dX != 0f) 1f.intoPx(context) else 0f
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun Float.intoPx(context: Context): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
    }

    private fun View.getRect(): Rect {
        return Rect().apply {
            left = getLeft()
            top = getTop()
            right = getRight()
            bottom = getBottom()
        }
    }
}