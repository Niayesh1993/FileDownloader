package com.example.filedownlowder

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.Status
import com.example.filedownlowder.model.DownloadModel
import com.example.filedownlowder.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.ArrayList


class MainActivity : AppCompatActivity() {

    private var dirPath: String? = null
    var VideoView: VideoView? = null
    var files: Array<File>? = null
    lateinit var download_list: MutableList<DownloadModel>
    lateinit var file_list: MutableList<String>
    lateinit var recyclerView: RecyclerView
    lateinit var file_recyclerView: RecyclerView
    lateinit var adapter: ListOfDownloadRecycelerViewAdapter
    lateinit var file_adapter: ListOfFileRecycelerViewAdapter
    lateinit var Downloads: MutableList<DownloadModel>

    /******************************************************************************/
   inner class ListOfDownloadRecycelerViewAdapter (var mDownload: List<DownloadModel>, private  var context: Context): RecyclerView.Adapter<ListOfDownloadRecycelerViewAdapter.ViewHolder>() {

        private var dirPath: String? = null
        init {

            this.mDownload = mDownload
            this.context = context
            dirPath = Utils().getRootDirPath(context)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.downloas_cardview, parent, false)

            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return mDownload.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.mItem = mDownload[position]

            holder.Start_btn!!.setOnClickListener(View.OnClickListener {
                if (Status.RUNNING == PRDownloader.getStatus(mDownload[position].DownloadId)) {
                    PRDownloader.pause(mDownload[position].DownloadId)
                    return@OnClickListener
                }
                holder.Start_btn!!.setEnabled(false)
                holder.progressBar!!.setIndeterminate(true)
                holder.progressBar!!.getIndeterminateDrawable().setColorFilter(
                    Color.BLUE, PorterDuff.Mode.SRC_IN
                )

                if (Status.PAUSED == PRDownloader.getStatus(mDownload[position].DownloadId)) {
                    PRDownloader.resume(mDownload[position].DownloadId)
                    return@OnClickListener
                }
                mDownload[position].DownloadId =
                    PRDownloader.download(mDownload[position].URL, dirPath, mDownload[position].FileName)
                        .build()
                        .setOnStartOrResumeListener {
                            holder.progressBar!!.setIndeterminate(false)
                            holder.Start_btn!!.setEnabled(true)
                            holder.Start_btn!!.setText(R.string.pause)
                            holder.Cancel_btn!!.setEnabled(true)
                        }
                        .setOnPauseListener { holder.Start_btn!!.setText(R.string.resume) }
                        .setOnCancelListener {
                            holder.Start_btn!!.setText(R.string.start)
                            holder.Cancel_btn!!.setEnabled(false)
                            holder.progressBar!!.setProgress(0)
                            holder.Progress_txt!!.setText("")
                            mDownload[position].DownloadId = 0
                            holder.progressBar!!.setIndeterminate(false)
                        }
                        .setOnProgressListener { progress ->
                            val progressPercent =
                                progress.currentBytes * 100 / progress.totalBytes
                            holder.progressBar!!.setProgress(progressPercent.toInt())
                            holder.Progress_txt!!.setText(
                                Utils().getProgressDisplayLine(
                                    progress.currentBytes,
                                    progress.totalBytes
                                )
                            )
                            holder.progressBar!!.setIndeterminate(false)
                        }
                        .start(object : OnDownloadListener {
                            override fun onDownloadComplete() {
                                holder.Start_btn!!.setEnabled(false)
                                holder.Cancel_btn!!.setEnabled(false)
                                holder.Start_btn!!.setText(R.string.completed)
                                file_list.add((mDownload[position].FileName.toString()))
                                Show_Files(file_list)

                            }

                            override fun onError(error: Error) {
                                holder.Start_btn!!.setText(R.string.start)
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.some_error_occurred) + " " + "1",
                                    Toast.LENGTH_SHORT
                                ).show()
                                holder.Progress_txt!!.setText("")
                                holder.progressBar!!.setProgress(0)
                                mDownload[position].DownloadId = 0
                                holder.Cancel_btn!!.setEnabled(false)
                                holder.progressBar!!.setIndeterminate(false)
                                holder.Start_btn!!.setEnabled(true)
                            }
                        })
            })

            holder.Cancel_btn!!.setOnClickListener(View.OnClickListener {
                PRDownloader.cancel(mDownload[position].DownloadId)
            })


        }

        inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView)
        {
            val view: View = mView

            var Start_btn: Button? = null
            var Cancel_btn: Button? = null
            var Progress_txt: TextView? = null
            var progressBar: ProgressBar? = null
            var cv : CardView
            internal var mItem: DownloadModel? = null

            init {

                val view: View = mView
                Start_btn = view.findViewById(R.id.start_btn)
                Cancel_btn = view.findViewById(R.id.cancel_btn)
                progressBar = view.findViewById(R.id.progressBar)
                Progress_txt = view.findViewById(R.id.textViewProgress)
                cv = view.findViewById(R.id.card_view)

            }

        }

    }

    /*****************************************************************************/
    inner class ListOfFileRecycelerViewAdapter (var mFile: List<String>, private  var context: Context): RecyclerView.Adapter<ListOfFileRecycelerViewAdapter.ViewHolder>() {

        private var dirPath: String? = null
        init {

            this.mFile = mFile
            this.context = context
            dirPath = Utils().getRootDirPath(context)

        }

        inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView)
        {
            val view: View = mView

            var Play_btn: Button? = null
            var Delete_btn: Button? = null
            var fileName_txt: TextView? = null
            var cv : CardView
            internal var mItem: String? = null

            init {

                val view: View = mView
                Play_btn = view.findViewById(R.id.play_btn)
                Delete_btn = view.findViewById(R.id.delete_btn)
                fileName_txt = view.findViewById(R.id.filename_txt)
                cv = view.findViewById(R.id.card_view)

            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.files_cardview, parent, false)

            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return mFile.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.mItem = mFile[position]
            holder.fileName_txt!!.setText(mFile[position])
            holder.Play_btn!!.setOnClickListener(View.OnClickListener {
                val m = MediaController(context)
                VideoView!!.setMediaController(m)
                val path = dirPath + "/" + mFile[position]
                val u: Uri = Uri.parse(path)
                VideoView!!.setVideoURI(u)
                VideoView!!.start()
            })

            holder.Delete_btn!!.setOnClickListener(View.OnClickListener {
                val path = dirPath + "/" + mFile[position]
                val file = File(path)
                val deleted: Boolean = file.delete()
                file_list.remove(mFile[position])
                Show_Files(file_list)
                videoView.resume()
            })
        }
    }
    /****************************************************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Downloads = ArrayList()
        file_list = ArrayList()
        download_list = ArrayList()
        dirPath = Utils().getRootDirPath(applicationContext)
        initView()

        var downloadModel = DownloadModel()
        downloadModel.DownloadId = 0
        downloadModel.FileName = "audio.mp4"
        downloadModel.URL = "https://bit.ly/30aWVDP"
        Downloads.add(downloadModel)
        download_list.add(downloadModel)

        var downloadModel2 = DownloadModel()
        downloadModel2.DownloadId = 1
        downloadModel2.FileName = "animate.mp4"
        downloadModel2.URL = " https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"
        Downloads.add(downloadModel2)
        download_list.add(downloadModel2)

        val directory = File(dirPath)
        files = directory.listFiles()
        for (Dm in download_list)
        {
            if (files!!.size>0){
                for (i in files!!.indices) {
                    if (((files as Array<File>)[i].name).equals(Dm.FileName)){
                        file_list.add((files as Array<File>)[i].name)
                        Downloads.remove(Dm)
                    }
                }
            }
        }


             Show_DownloadList(Downloads)
             Show_Files(file_list)
    }

    private fun Show_Files(fileList: List<String>) {

        file_recyclerView.removeAllViews()
        if (fileList.size > 0) {
            file_adapter = ListOfFileRecycelerViewAdapter(fileList, this)
            file_recyclerView.adapter = file_adapter
            file_recyclerView.itemAnimator = DefaultItemAnimator()
            val layoutmanager = LinearLayoutManager(this)
            file_recyclerView.layoutManager = layoutmanager
        }
    }

    private fun initView() {
        VideoView = findViewById(R.id.videoView)
        recyclerView = findViewById(R.id.lists_of_download)
        file_recyclerView = findViewById(R.id.lists_of_files)
    }

    fun Show_DownloadList( ListOfDownload: List<DownloadModel>){
        recyclerView.removeAllViews()
        if (ListOfDownload.size > 0) {
            adapter = ListOfDownloadRecycelerViewAdapter(ListOfDownload, this)
            recyclerView.adapter = adapter
            recyclerView.itemAnimator = DefaultItemAnimator()
            val layoutmanager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutmanager
        }
    }

}