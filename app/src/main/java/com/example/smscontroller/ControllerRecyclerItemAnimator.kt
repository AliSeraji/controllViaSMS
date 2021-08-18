package com.example.smscontroller

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class ControllerRecyclerItemAnimator: DefaultItemAnimator() {
    override fun canReuseUpdatedViewHolder(
        viewHolder: RecyclerView.ViewHolder, payloads: MutableList<Any>
    ): Boolean = true

   override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder) = true

    override fun recordPreLayoutInformation(
        state: RecyclerView.State,
        viewHolder: RecyclerView.ViewHolder,
        changeFlags: Int,
        payloads: MutableList<Any>
    ): ItemHolderInfo {

        if (changeFlags == FLAG_CHANGED) {
            for (payload in payloads) {
                val holder=(viewHolder as ControllerRecyclerAdopter.ControllerItemHolder)
                if (payload as? Int == ITEM_IS_PENDING) {
                    circularRevealAnimation(holder.binding.getDeviceQuantity,holder)
                    return ControllerItemHolderInfo(true)

                }
                if(payload as? Int== ITEM_IS_NOT_PENDING){
                    circularHideAnimation(holder.binding.getDeviceQuantity,holder)
                    return ControllerItemHolderInfo(false)
                }

            }
        }
        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
    }


    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder, newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo, postInfo: ItemHolderInfo
    ): Boolean {

        val preHolder = oldHolder as ControllerRecyclerAdopter.ControllerItemHolder
        val postHolder= newHolder as ControllerRecyclerAdopter.ControllerItemHolder
        if(preInfo is ControllerItemHolderInfo){
            if(preInfo.isPending){
                // It is already pending or not
                circularHideAnimation(preHolder.binding.getDeviceQuantity,postHolder)
            } else {
                // It is not pending so now start pending
                circularRevealAnimation(preHolder.binding.getDeviceQuantity,postHolder)
            }
            return true
        }

        /*if(postInfo is ControllerItemHolderInfo){
            if(postInfo.isPending){
                circularHideAnimation(preHolder.binding.getDeviceQuantity,postHolder)
            }
            else {
                // It is not pending so now start pending
                circularRevealAnimation(preHolder.binding.getDeviceQuantity,postHolder)
            }
            return true
        }*/
        //return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
        return false
    }


    private fun circularRevealAnimation(view: View,holder:RecyclerView.ViewHolder){
        // Check if the runtime version is at least Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            val cx = view.width / 2
            val cy = view.height / 2

            // get the final radius for the clipping circle
            val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

            // create the animator for this view (the start radius is zero)
            val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 1f, finalRadius)
            //set animation duration
            anim.duration=1000
            //set animation interpolator
            anim.interpolator= LinearInterpolator()
            // make the view visible and start the animation
            view.visibility = View.VISIBLE

            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    dispatchAnimationFinished(holder)
                }
            })

            anim.start()

        } else {
            // set the view to invisible without a circular reveal animation below Lollipop
            view.visibility = View.INVISIBLE
        }
    }

    private fun circularHideAnimation(view: View,holder:RecyclerView.ViewHolder){
        // Check if the runtime version is at least Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            val cx = view.width / 2
            val cy = view.height / 2

            // get the initial radius for the clipping circle
            val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

            // create the animation (the final radius is zero)
            val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 1f)
            //set animation duration
            anim.duration=1000
            //set animation interpolator
            anim.interpolator= LinearInterpolator()
            // make the view invisible when the animation is done
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    view.visibility = View.INVISIBLE
                    dispatchAnimationFinished(holder)
                }
            })
            // start the animation
            anim.start()
        } else {
            // set the view to visible without a circular reveal animation below Lollipop
            view.visibility = View.VISIBLE
        }
    }

    class ControllerItemHolderInfo(val isPending: Boolean) : ItemHolderInfo()
}