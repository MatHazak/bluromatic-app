package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI

private const val TAG = "BlurWorker"

class BlurWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val appContext = applicationContext
        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Blurring image", appContext)
        sleep()

        return try {

            if (TextUtils.isEmpty(resourceUri))
                throw IllegalArgumentException("Invalid input URI")

            val picture = BitmapFactory.decodeStream(
                appContext.contentResolver
                    .openInputStream(Uri.parse(resourceUri))
            )
            val output = blurBitmap(picture, appContext)
            val outputUri = writeBitmapToFile(appContext, output)
            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

            makeStatusNotification("blurredPictureUri: $outputUri", appContext)
            Result.success(outputData)
        } catch (throwable: Throwable) {
            Log.e(TAG, throwable.stackTraceToString())
            Result.failure()
        }
    }
}