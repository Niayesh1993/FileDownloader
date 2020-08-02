package com.example.filedownlowder

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.Status
import com.example.filedownlowder.utils.Utils

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var dirPath: String? = null
    val URL = " https://bit.ly/30aWVDP"
    var Start_btn: Button? = null
    var Cancel_btn: Button? = null
    var Progress_txt: TextView? = null
    var progressBar: ProgressBar? = null
    var downloadId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dirPath = Utils().getRootDirPath(applicationContext)
        initView()
    }

    private fun initView() {
        Start_btn = findViewById(R.id.start_btn)
        Start_btn!!.setOnClickListener(this)
        Cancel_btn = findViewById(R.id.cancel_btn)
        Cancel_btn!!.setOnClickListener(this)
        progressBar = findViewById(R.id.progressBar)
        Progress_txt = findViewById(R.id.textViewProgress)
    }

    override fun onClick(p0: View?) {

        if (p0 != null) {
            when (p0.getId()) {

                R.id.start_btn -> {
                    if (Status.RUNNING == PRDownloader.getStatus(downloadId)) {
                        PRDownloader.pause(downloadId)
                        return
                    }
                    Start_btn!!.setEnabled(false)
                    progressBar!!.setIndeterminate(true)
                    progressBar!!.getIndeterminateDrawable().setColorFilter(
                        Color.BLUE, PorterDuff.Mode.SRC_IN
                    )

                    if (Status.PAUSED == PRDownloader.getStatus(downloadId)) {
                        PRDownloader.resume(downloadId)
                        return
                    }
                    downloadId =
                        PRDownloader.download(URL, dirPath, "audio")
                            .build()
                            .setOnStartOrResumeListener {
                                progressBar!!.setIndeterminate(false)
                                Start_btn!!.setEnabled(true)
                                Start_btn!!.setText(R.string.pause)
                                Cancel_btn!!.setEnabled(true)
                            }
                            .setOnPauseListener { Start_btn!!.setText(R.string.resume) }
                            .setOnCancelListener {
                                Start_btn!!.setText(R.string.start)
                                Cancel_btn!!.setEnabled(false)
                                progressBar!!.setProgress(0)
                                Progress_txt!!.setText("")
                                downloadId = 0
                                progressBar!!.setIndeterminate(false)
                            }
                            .setOnProgressListener { progress ->
                                val progressPercent =
                                    progress.currentBytes * 100 / progress.totalBytes
                                progressBar!!.setProgress(progressPercent.toInt())
                                Progress_txt!!.setText(
                                    Utils().getProgressDisplayLine(
                                        progress.currentBytes,
                                        progress.totalBytes
                                    )
                                )
                                progressBar!!.setIndeterminate(false)
                            }
                            .start(object : OnDownloadListener {
                                override fun onDownloadComplete() {
                                    Start_btn!!.setEnabled(false)
                                    Cancel_btn!!.setEnabled(false)
                                    Start_btn!!.setText(R.string.completed)
                                }

                                override fun onError(error: Error) {
                                    Start_btn!!.setText(R.string.start)
                                    Toast.makeText(
                                        applicationContext,
                                        getString(R.string.some_error_occurred) + " " + "1",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Progress_txt!!.setText("")
                                    progressBar!!.setProgress(0)
                                    downloadId = 0
                                    Cancel_btn!!.setEnabled(false)
                                    progressBar!!.setIndeterminate(false)
                                    Start_btn!!.setEnabled(true)
                                }
                            })

                }
                R.id.cancel_btn ->{
                    PRDownloader.cancel(downloadId)
                }

            }
        }

    }
}