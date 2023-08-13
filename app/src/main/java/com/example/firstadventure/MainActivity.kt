package com.example.firstadventure

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.firstadventure.ui.theme.FirstAdventureTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirstAdventureTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    GameViewComposable()
                }
            }
        }
    }
}

@Composable
fun GameViewComposable() {
    val context = LocalContext.current
    AndroidView(factory = { GameView(context) }) {
        // Optional: any configurations or interactions with the GameView
    }
}


class GameView(context: Context) : SurfaceView(context), Runnable, SurfaceHolder.Callback {
    private val thread: Thread
    private var playing = false
    private var paused = true
    private var canvas: Canvas = Canvas()
    private var paint: Paint = Paint()
    private var character: Rect

    private var map: Bitmap
    private var mapX = 0f
    private var mapY = 0f

    // Map scroll speed
    private var scrollSpeed = 5f

    init {
        holder.addCallback(this)
        map = BitmapFactory.decodeResource(context.resources, R.drawable.map)
        character = Rect(100, 100, 200, 200) // Just an example, replace with your own character rectangle
        thread = Thread(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        playing = true
        paused = false
        thread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Here you can handle changes if needed
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        playing = false
        try {
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    // Handle Touch Events
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Example: change the map scrolling direction when screen is touched
                scrollSpeed = -scrollSpeed
            }
        }
        return true
    }

    override fun run() {
        while (playing) {
            if (!paused) {
                update()
            }
            draw()
            control()
        }
    }

    private fun update() {
        // Update game state (e.g. move character, scroll map)
        // In this case, just scroll the map
        mapX -= scrollSpeed
    }

    private fun draw() {
        // Draw game elements
        if (holder.surface.isValid) {
            canvas = holder.lockCanvas()
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(map, mapX, mapY, paint)
            canvas.drawRect(character, paint)
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun control() {
        // Control game speed (e.g. limit frame rate)
        // Here just a simple sleep
        try {
            Thread.sleep(17) // Aim for ~60 frames per second
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    // Implement other necessary methods here (e.g. surfaceCreated, surfaceDestroyed, etc.)
}
