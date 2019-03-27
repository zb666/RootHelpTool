package com.example.mechrevo.roothelptool.service

import android.annotation.SuppressLint
import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log

@SuppressLint("NewApi")
class JobSchedulerService : JobService() {
    override fun onStopJob(params: JobParameters?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartJob(params: JobParameters?): Boolean{
        Log.d("BobService","服务在周期性的处理中")
        return false
    }
}