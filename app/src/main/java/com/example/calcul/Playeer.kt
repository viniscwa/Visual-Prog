package com.example.calcul

import android.Manifest.permission.READ_MEDIA_AUDIO
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class Playeer : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var playstop: Button
    private lateinit var next: Button
    private lateinit var back: Button
    private lateinit var shuffle: Button
    private lateinit var musicPath: String
    private lateinit var directory: File
    private lateinit var seekBar: SeekBar
    private lateinit var albumArt: ImageView
    private var musicList: Array<File> = arrayOf()
    private lateinit var currentTimeTextView: TextView
    private lateinit var totalTimeTextView: TextView
    private lateinit var songNameTextView: TextView
    private lateinit var songsListTextView: TextView
    private var songIndex: Int = 0
    private var isShuffleOn: Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateSeekBarRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_playeer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (!isGranted) {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        requestPermissionLauncher.launch(READ_MEDIA_AUDIO)

        initializeViews()
        setupMusicPlayer()
    }

    private fun initializeViews() {
        seekBar = findViewById(R.id.seekBar)
        playstop = findViewById(R.id.playstop)
        next = findViewById(R.id.next)
        back = findViewById(R.id.back)
        shuffle = findViewById(R.id.shuffle)
        albumArt = findViewById(R.id.albumArt)
        currentTimeTextView = findViewById(R.id.currentTime)
        totalTimeTextView = findViewById(R.id.totalTime)
        songNameTextView = findViewById(R.id.songName)
        songsListTextView = findViewById(R.id.songsList)
    }

    private fun setupMusicPlayer() {
        musicPath = Environment.getExternalStorageDirectory().path + "/Test"
        directory = File(musicPath)

        if (!directory.exists() || !directory.isDirectory) {
            Toast.makeText(this, "Music directory not found", Toast.LENGTH_SHORT).show()
            return
        }

        musicList = directory.listFiles { file ->
            file.isFile && file.name.endsWith(".mp3")
        } ?: arrayOf()

        if (musicList.isEmpty()) {
            Toast.makeText(this, "No MP3 files found", Toast.LENGTH_SHORT).show()
            return
        }

        initializeMediaPlayer()
    }

    private fun initializeMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(musicList[songIndex]))
        mediaPlayer?.let { player ->
            seekBar.max = player.duration
            updateSongInfo()
            updateSongsList()
            loadAlbumArt()

            updateSeekBarRunnable = object : Runnable {
                override fun run() {
                    if (player.isPlaying) {
                        seekBar.progress = player.currentPosition
                        updateCurrentTime()
                    }
                    handler.postDelayed(this, 500)
                }
            }
            handler.post(updateSeekBarRunnable)
        }
    }

    private fun loadAlbumArt() {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(musicList[songIndex].absolutePath)
            val art = retriever.embeddedPicture
            if (art != null) {
                val bitmap = BitmapFactory.decodeByteArray(art, 0, art.size)
                albumArt.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()

        } finally {
            retriever.release()
        }
    }

    private fun updateSongInfo() {
        mediaPlayer?.let {
            songNameTextView.text = musicList[songIndex].name.replace(".mp3", "")
            totalTimeTextView.text = formatTime(it.duration)
            updateCurrentTime()
        }
    }

    private fun updateCurrentTime() {
        mediaPlayer?.let {
            currentTimeTextView.text = formatTime(it.currentPosition)
        }
    }

    private fun formatTime(millis: Int): String {
        return String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(millis.toLong()) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis.toLong())))
    }

    private fun updateSongsList() {
        val songsText = StringBuilder("Available songs:\n")
        for ((index, file) in musicList.withIndex()) {
            songsText.append("${index + 1}. ${file.name.replace(".mp3", "")}\n")
        }
        songsListTextView.text = songsText.toString()
        songsListTextView.setOnClickListener {
            showSongSelectionDialog()
        }
    }

    private fun showSongSelectionDialog() {
        val songNames = musicList.map { it.name.replace(".mp3", "") }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Select Song")
            .setItems(songNames) { _, which ->
                songIndex = which
                initializeMediaPlayer()
                mediaPlayer?.start()
                playstop.text = "⏸"
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()
        mediaPlayer?.setOnCompletionListener {
            if (isShuffleOn) {
                playRandomSong()
            } else {
                next()
            }
        }

        playstop.setOnClickListener {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                    playstop.text = "▶"
                } else {
                    player.start()
                    playstop.text = "⏸"
                }
            }
        }

        next.setOnClickListener {
            if (isShuffleOn) {
                playRandomSong()
            } else {
                next()
            }
        }

        back.setOnClickListener { previous() }

        shuffle.setOnClickListener {
            isShuffleOn = !isShuffleOn
            shuffle.text = if (isShuffleOn) "🔀 ON" else "🔀 OFF"
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    updateCurrentTime()
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun next() {
        mediaPlayer?.release()
        seekBar.progress = 0
        songIndex = (songIndex + 1) % musicList.size
        initializeMediaPlayer()
        mediaPlayer?.start()
        playstop.text = "⏸"
    }

    private fun previous() {
        mediaPlayer?.release()
        seekBar.progress = 0
        songIndex = if (songIndex - 1 < 0) musicList.size - 1 else songIndex - 1
        initializeMediaPlayer()
        mediaPlayer?.start()
        playstop.text = "⏸"
    }

    private fun playRandomSong() {
        mediaPlayer?.release()
        seekBar.progress = 0
        val newIndex = Random.nextInt(musicList.size)
        songIndex = if (newIndex != songIndex || musicList.size == 1) newIndex else (newIndex + 1) % musicList.size
        initializeMediaPlayer()
        mediaPlayer?.start()
        playstop.text = "⏸"
    }


}