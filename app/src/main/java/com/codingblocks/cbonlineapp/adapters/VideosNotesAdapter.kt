package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.database.*
import com.codingblocks.cbonlineapp.utils.OnItemClickListener
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.item_notes.view.*
import java.util.*


class VideosNotesAdapter(private var notesData: ArrayList<NotesModel>, var listener: OnItemClickListener) : RecyclerView.Adapter<VideosNotesAdapter.NotesViewHolder>() {


    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var contentDao: ContentDao
    private lateinit var notesDao: NotesDao


    fun setData(notesData: ArrayList<NotesModel>) {
        this.notesData = notesData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        context = parent.context
        database = AppDatabase.getInstance(context)
        contentDao = database.contentDao()
        notesDao = database.notesDao()


        return NotesViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notes, parent, false))
    }

    override fun getItemCount(): Int {
        return notesData.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bindView(notesData[position],position)
    }

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(note: NotesModel, position: Int) {
            itemView.contentTitleTv.text = contentDao.getContentWithId(note.runAttemptId, note.contentId).title
            itemView.bodyTv.setText(note.text)
            itemView.timeTv.text = secToTime(note.duration)

            itemView.setOnClickListener {
                listener.onItemClick(note.duration.toInt(), note.contentId)
            }

            itemView.editTv.setOnClickListener {

            }

            itemView.deleteTv.setOnClickListener {
                Clients.onlineV2JsonApi.deleteNoteById(note.nttUid).enqueue(retrofitCallback { throwable, response ->
                    response.let {
                        if(it?.isSuccessful!!){
                            notesDao.deleteNoteByID(note.nttUid)
//                            notesData.removeAt(position)
//                            notifyDataSetChanged()

                        }
                    }
                })
            }
        }

        private fun resolveDoubt(doubt: DoubtsModel) {
//            val solvedDoubt = DoubtsJsonApi()
//            solvedDoubt.body = doubt.body
//            solvedDoubt.title = doubt.title
//            val runAttempts = RunAttemptsModel() // type run-attempts
//            val contents = Contents() // type contents
//            runAttempts.id = doubt.runAttemptId
//            contents.id = doubt.contentId
//            solvedDoubt.status = "RESOLVED"
//            solvedDoubt.discourseTopicId = doubt.discourseTopicId
//            solvedDoubt.id = doubt.dbtUid
//            solvedDoubt.resolvedById = (context as Activity).getPrefs().SP_USER_ID
//            solvedDoubt.postrunAttempt = runAttempts
//            solvedDoubt.content = contents
//            Clients.onlineV2JsonApi.resolveDoubt(doubt.dbtUid, solvedDoubt).enqueue(retrofitCallback { throwable, response ->
//                response?.body().let {
//                    if (response?.isSuccessful!!) {
//                        doubtDao.updateStatus(doubt.dbtUid, solvedDoubt.status)
//                    }
//                }
//            })

        }
    }

    fun secToTime(time: Double): String {
        val sec = time.toInt()
        val seconds = sec % 60
        var minutes = sec / 60
        if (minutes >= 60) {
            val hours = minutes / 60
            minutes %= 60
            if (hours >= 24) {
                val days = hours / 24
                return String.format("%d days %02d:%02d:%02d", days, hours % 24, minutes, seconds)
            }
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
        return String.format("00:%02d:%02d", minutes, seconds)
    }
}