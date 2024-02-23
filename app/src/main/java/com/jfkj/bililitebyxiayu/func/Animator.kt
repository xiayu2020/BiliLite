package com.jfkj.bililitebyxiayu.func

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.viewpager2.widget.ViewPager2

fun dpToPx(dp: Float, context: Context): Float {
    val density = context.resources.displayMetrics.density
    return (dp * density + 0.5f)
}

fun slip_up(animatedView: View, Height: Float, context: Context) {
    // 计算视图当前的高度
    val currentHeight = animatedView.height
    // 计算下滑后的目标高度（这里假设下滑 200dp）
    val targetHeight = currentHeight + dpToPx(Height, context)
    // 创建动画对象
    val slideDownAnimation = ObjectAnimator.ofFloat(animatedView, "translationY", -targetHeight)
    // 设置动画插值器，使动画加速
    slideDownAnimation.interpolator = AccelerateInterpolator()
    // 设置动画时长
    slideDownAnimation.duration = 300L
    // 启动动画
    slideDownAnimation.start()
}

fun slip_down(animatedView: View, Height: Float, context: Context) {
    // 计算视图当前的高度
    val currentHeight = animatedView.height
    // 计算下滑后的目标高度（这里假设下滑 200dp）
    val targetHeight = currentHeight + dpToPx(Height, context)
    // 创建动画对象
    val slideDownAnimation = ObjectAnimator.ofFloat(animatedView, "translationY", targetHeight)
    // 设置动画插值器，使动画加速
    slideDownAnimation.interpolator = AccelerateInterpolator()
    // 设置动画时长
    slideDownAnimation.duration = 300L
    // 启动动画
    slideDownAnimation.start()
}

fun slip_left_show(animatedView: View, context: Context) {
    // 计算视图当前的宽度
    val currentWidth = animatedView.width.toFloat()
    ObjectAnimator.ofFloat(animatedView, "translationX", -currentWidth * 2, 0f)
        .apply {
            duration = 400 // 设置动画持续时间为 500 毫秒
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    animatedView.visibility = View.VISIBLE
                }
            })
            start() // 启动动画
        }
}

fun slip_right_close(animatedView: View, context: Context) {
    // 计算视图当前的宽度
    val currentWidth = animatedView.width.toFloat()
    ObjectAnimator.ofFloat(animatedView, "translationX", 0f, currentWidth * 2)
        .apply {
            duration = 400 // 设置动画持续时间为 500 毫秒
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    animatedView.visibility = View.GONE
                }
            })
            start() // 启动动画
        }
}

//// 自定义退出动画
//class ExitPageTransformer : ViewPager2.PageTransformer {
//    override fun transformPage(page: View, position: Float) {
//        val width = page.width.toFloat()
//        val translationX = width * -position
//        page.translationX = translationX
//        page.alpha = 1 - Math.abs(position)
//    }
//}
//
//// 自定义进入动画
//class EnterPageTransformer : ViewPager2.PageTransformer {
//    override fun transformPage(page: View, position: Float) {
//        val width = page.width.toFloat()
//        val translationX = width * -position
//        page.translationX = translationX
//        page.alpha = 1 - Math.abs(position)
//    }
//}