package com.example.orion.media_player

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ticket_song.view.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    var listofsong = ArrayList<songinfo>()
    var adapter: mySongAdapter? = null
    var mp: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        loadurlonline()
        CheckUserPermsions()
//        adapter = mySongAdapter(listofsong)
//        lslistofsongs.adapter = adapter

        var mytracking=mySongTrack()
        mytracking.start()
    }

    fun loadurlonline() {
        listofsong.add(
            songinfo(
                "Shaamat",
                "Ankit Tiwari, Tara Sutaria",
                "https://pagalworld.com.se/deva-deva-mp3-song-download.html"
            )
        )
        listofsong.add(
            songinfo(
                "Deva Deva",
                "Ankit Tiwari, Tara Sutaria",
                "https://firebasestorage.googleapis.com/v0/b/uploading-pic.appspot.com/o/Deva%20Deva_320(PagalWorld.com.se).mp3?alt=media"
            )
        )
        listofsong.add(
            songinfo(
                "Shaamat",
                "Ankit Tiwari, Tara Sutaria",
                "https://pagalworld.com.se/files/download/id/6576"
            )
        )
        listofsong.add(
            songinfo(
                "Shaamat",
                "Ankit Tiwari, Tara Sutaria",
                "https://pagalworld.com.se/files/download/id/6576"
            )
        )
        listofsong.add(
            songinfo(
                "Shaamat",
                "Ankit Tiwari, Tara Sutaria",
                "https://pagalworld.com.se/files/download/id/6576"
            )
        )

    }

    inner class mySongAdapter() : BaseAdapter() {
        var mylistofsong = ArrayList<songinfo>()

        constructor(mylistofsong: ArrayList<songinfo>) : this() {
            this.mylistofsong = mylistofsong
        }

        override fun getCount(): Int {
            return this.mylistofsong.size
        }

        override fun getItem(p0: Int): Any {
            return this.mylistofsong[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var myView = layoutInflater.inflate(R.layout.ticket_song, null)
            var song = this.mylistofsong[p0]
            myView.tvSong.text = song.title
            myView.tvSinger.text = song.Authername
            myView.buPlay.setOnClickListener(View.OnClickListener {
                if (myView.buPlay.text.equals("STOP")) {
                    mp!!.stop()
                    myView.buPlay.text = "PLAY"
                } else {
                    mp = MediaPlayer()
                    try {
                        mp!!.setDataSource(song.songUrl)
                        mp!!.prepare()
                        mp!!.start()
                        myView.buPlay.setText("STOP")
                        sbprgress.max = mp!!.duration
                    } catch (ex: Exception) {
                    }
                }
            })
            return myView;
        }
    }
    inner  class  mySongTrack :Thread(){


        override fun run() {
            while(true){
                try{
                    sleep(1000)
                }catch (ex:Exception){}

                runOnUiThread {

                    if (mp!=null){
                        sbprgress.progress = mp!!.currentPosition
                    }
                }
            }

        }
    }

    fun CheckUserPermsions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_ASK_PERMISSIONS)
                return
            }
        }

        LoadSong()

    }

    //get acces to location permsion
    private val REQUEST_CODE_ASK_PERMISSIONS = 123


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LoadSong()
            } else {
                // Permission Denied
                Toast.makeText(this, "denail", Toast.LENGTH_SHORT)
                    .show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
    @SuppressLint("Range")
    fun LoadSong(){
        val allSongURI=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection=MediaStore.Audio.Media.IS_MUSIC+"!=0"
        val cursur=contentResolver.query(allSongURI,null,selection,null,null)
        if (cursur!=null){
            if(cursur!!.moveToFirst()){
                do {
                        val songUrl=cursur!!.getString(cursur.getColumnIndex(MediaStore.Audio.Media.DATA))
                        val SongAuthor=cursur!!.getString(cursur.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                        var SongName=cursur!!.getString(cursur.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                        listofsong.add(
                        songinfo(
                            SongName,
                            SongAuthor,
                            songUrl
                        ))
                }while (cursur!!.moveToNext())
            }
            cursur!!.close()
            adapter = mySongAdapter(listofsong)
            lslistofsongs.adapter = adapter
        }
    }
}