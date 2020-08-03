package com.example.filedownlowder

import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.Status
import com.example.filedownlowder.utils.Utils
import java.io.File


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var dirPath: String? = null
    val URL = " https://bit.ly/30aWVDP"
    var Start_btn: Button? = null
    var Cancel_btn: Button? = null
    var Play_btn: Button? = null
    var Progress_txt: TextView? = null
    var progressBar: ProgressBar? = null
    var downloadId = 0
    var VideoView: VideoView? = null
    var Delete_btn: Button? = null
    var File_layout: RelativeLayout? = null
    var File_name: String = "audio.mp4"
    var files: Array<File>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dirPath = Utils().getRootDirPath(applicationContext)
        initView()

        val directory = File(dirPath)
        files = directory.listFiles()
        for (i in files!!.indices) {
            if (((files as Array<File>)[i].name).equals(File_name)){
                Start_btn!!.setEnabled(false)
                Cancel_btn!!.setEnabled(false)
                Start_btn!!.setText(R.string.completed)
                File_layout!!.visibility = View.VISIBLE
            }

        }
    }

    private fun initView() {
        Start_btn = findViewById(R.id.start_btn)
        Start_btn!!.setOnClickListener(this)
        Cancel_btn = findViewById(R.id.cancel_btn)
        Cancel_btn!!.setOnClickListener(this)
        Play_btn = findViewById(R.id.play_btn)
        Play_btn!!.setOnClickListener(this)
        progressBar = findViewById(R.id.progressBar)
        Progress_txt = findViewById(R.id.textViewProgress)
        VideoView = findViewById(R.id.videoView)
        Delete_btn = findViewById(R.id.delete_btn)
        Delete_btn!!.setOnClickListener(this)
        File_layout = findViewById(R.id.file_layout)
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
                        PRDownloader.download(URL, dirPath, File_name)
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
                                    File_layout!!.visibility = View.VISIBLE
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
                R.id.play_btn ->{
                    val m = MediaController(this)
                    VideoView!!.setMediaController(m)
                    val path = dirPath + "/"+File_name
                    val u: Uri = Uri.parse(path)
                    VideoView!!.setVideoURI(u)
                    VideoView!!.start()
                }
                R.id.delete_btn ->{
                    val path = dirPath + "/"+File_name
                    val file = File(path)
                    val deleted: Boolean = file.delete()
                }

            }
        }

    }
}