package edu.vt.cs3714.tutorial_05

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONObject

class UploadWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // Prepare JSON object
        val json = JSONObject().apply {
            put("date", inputData.getString("date"))
            put("userID", inputData.getString("userID"))
            put("event", inputData.getString("event"))
        }

        // Log for debugging
        Log.d(MainActivity.TAG, "params: $json url: ${MainActivity.URL}")

        // Attempt to upload log
        return try {
            val response = TrackerRetrofitService.create(MainActivity.URL).postLog(json).execute()
            if (response.isSuccessful) {
                Result.success()
            } else {
                Log.e(MainActivity.TAG, "Upload failed with code: ${response.code()}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(MainActivity.TAG, "Upload failed", e)
            Result.failure()
        }
    }
}
