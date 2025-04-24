package com.rehman.template.core.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.internal.Primitives
import com.rehman.template.R
import java.lang.reflect.Type
import java.util.Locale


object ProjectUtils {


    fun objectToString(value: Any?): String {
        val gson = Gson()
        val json = gson.toJson(value)
        return json
    }

    fun <T> stringToObject(classObj: String?, objectClass: Class<T>?): T? {
        return Primitives.wrap(objectClass).cast(Gson().fromJson(classObj, objectClass as Type?))
    }


    fun TextView.underline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }


    fun View.dismissKeyboard() {
        val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(this.windowToken, 0)
    }

    fun Context.showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun Fragment.showToast(message: String) {
        context?.showToast(message)
    }


    fun View.showSnackBar(message: String, marginBottom: Int = 30) {
        val snackBar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)

        // Set background and text color
        snackBar.setBackgroundTint(
            ContextCompat.getColor(
                this.context,
                android.R.color.holo_red_light
            )
        )
        snackBar.setTextColor(ContextCompat.getColor(this.context, android.R.color.holo_red_light))
        snackBar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
        snackBar.setTextMaxLines(3)

        // Get SnackBar view
        val snackBarView = snackBar.view
        val snackBarText =
            snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
//        snackBarText.typeface = ResourcesCompat.getFont(context, /*R.font.your_font*/)


        val layoutParams = snackBarView.layoutParams as ViewGroup.LayoutParams
        val displayMetrics = this.resources.displayMetrics
        val widthInPixels = displayMetrics.widthPixels
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        snackBarView.layoutParams = layoutParams

        val params = snackBarView.layoutParams as ViewGroup.MarginLayoutParams
        params.bottomMargin = marginBottom
        snackBarView.layoutParams = layoutParams

        // Show the SnackBar
        snackBar.show()

    }


    fun Fragment.showSnackBar(message: String, marginBottom: Int = 30) {
        view?.showSnackBar(message, marginBottom)
    }


    fun <T : AppCompatActivity> Context.push(
        activityClass: Class<T>,
        finishCurrent: Boolean = false,
        extras: (Intent.() -> Unit)? = null,
    ) {
        val intent = Intent(this, activityClass).apply {
            extras?.invoke(this) // Apply the extras if provided
        }
        if (this !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Required for non-Activity context
        }
        startActivity(intent)

        if (finishCurrent) {
            (this as? AppCompatActivity)?.finish()
        }
    }


    fun Activity.pop() {
        finish()
    }

    fun ImageView.showImage(imageUrl: String?) {
        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(this)
    }


    fun String.toCapitalize(): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }


}