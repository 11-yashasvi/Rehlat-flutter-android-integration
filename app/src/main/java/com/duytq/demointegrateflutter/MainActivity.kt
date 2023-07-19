package com.duytq.demointegrateflutter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.myflutter.MyFlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
  private val CHANNEL = "com.duytq.demointegrateflutter"
  private var flutterEngine: FlutterEngine? = null
  private val PARTIAL_SCREEN_ENGINE_ID = "partialScreenEngineId"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setUpFlutter()
    btnSend.setOnClickListener {
      goToFlutterModule()
    }
  }

  private fun setUpFlutter() {
    if (flutterEngine == null) {
      flutterEngine = FlutterEngineCache.getInstance().get(PARTIAL_SCREEN_ENGINE_ID)
      flutterEngine!!
          .dartExecutor
          .executeDartEntrypoint(
              DartExecutor.DartEntrypoint.createDefault()
          )
    }

    MethodChannel(flutterEngine!!.getDartExecutor().getBinaryMessenger(), CHANNEL)
        .invokeMethod("notifyNavToFlutter", "DEFAULT")

    MethodChannel(flutterEngine!!.getDartExecutor().getBinaryMessenger(), CHANNEL)
        .setMethodCallHandler { call, result ->
          run {
            when (call.method) {
              "exitFlutter" -> finish()
              else -> result.notImplemented()
            }
          }
        }
  }

  private fun goToFlutterModule() {
    val intent = Intent(this, MyFlutterActivity::class.java)
    startActivity(intent)
  }

  override fun onResume() {
    super.onResume()
    flutterEngine!!.lifecycleChannel.appIsResumed()
  }

  override fun onPause() {
    super.onPause()
    flutterEngine!!.lifecycleChannel.appIsInactive()
  }

  override fun onStop() {
    super.onStop()
    flutterEngine!!.lifecycleChannel.appIsPaused()
  }

  override fun onDestroy() {
    super.onDestroy()
  }
}