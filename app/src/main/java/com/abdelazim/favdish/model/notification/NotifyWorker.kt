package com.abdelazim.favdish.model.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abdelazim.favdish.R
import com.abdelazim.favdish.utils.Constants
import com.abdelazim.favdish.view.activity.MainActivity

// will complete this prt later
class NotifyWorker(context: Context, params: WorkerParameters)
    : Worker(context, params){
    override fun doWork(): Result {
        return Result.success()
    }


    private fun sendNotification(){
        val notification_id = 0

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constants.NOTIFICATION_ID, notification_id)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val titleNotification = applicationContext.getString(R.string.notification_title)
        val subtitleNotification = applicationContext.getString(R.string.notification_subtitle)

        val bitmap = applicationContext.vectorToBitmap(R.drawable.ic_vector_logo)

        val bigPicStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(bitmap)

        //val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)


    }

    // convert from Drawable to Bitmap
    private fun Context.vectorToBitmap(drawableId: Int): Bitmap?{
        // Get the drawable vector image
        val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        ) ?: return null

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)

        drawable.draw(canvas)
        return bitmap
    }
}