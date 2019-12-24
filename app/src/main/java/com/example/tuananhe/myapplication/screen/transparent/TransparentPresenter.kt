package com.example.tuananhe.myapplication.screen.transparent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.Image.Plane
import android.media.ImageReader
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.tuananhe.myapplication.service.record.RecordService
import com.example.tuananhe.myapplication.utils.Constant
import com.example.tuananhe.myapplication.utils.Constant.Companion.SCREENSHOT_REQUEST_CODE
import com.example.tuananhe.myapplication.utils.Settings
import java.io.File
import java.io.FileOutputStream
import java.lang.Long
import java.nio.ByteBuffer


class TransparentPresenter(private val view: TransparentContract.View) : TransparentContract.Presenter {

    override fun getActivity(): Activity = view.provideActivity()

    private lateinit var mediaProjectionManager: MediaProjectionManager

    override fun getProjectionManager() {
        mediaProjectionManager = getActivity()
                .getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null || resultCode != Activity.RESULT_OK) {
            getActivity().finish()
            return
        }
        if (requestCode == Constant.RECORD_REQUEST_CODE) {
            startRecord(data, resultCode)
        }
        if (requestCode == SCREENSHOT_REQUEST_CODE) {
            Handler().postDelayed({
                takeScreenShot(data, resultCode)
            }, 100)
        }
    }

    override fun handleIntent(intent: Intent) {
        when (intent.action) {
            Constant.START_RECORD -> {
                startRequestRecord()
            }
            Constant.STOP_RECORD -> {

            }
            Constant.START_SCREEN_SHOT -> {
                requestScreenShot()
            }
        }
    }

    override fun startRequestRecord() {
        getActivity().startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                Constant.RECORD_REQUEST_CODE)
    }

    override fun startRecord(data: Intent, resultCode: Int) {
        val intent = Intent(getActivity(), RecordService::class.java)
        intent.action = Constant.START_RECORD
        intent.putExtra(Constant.EXTRA_RESULT_CODE, resultCode)
        intent.putExtra(Constant.EXTRA_DATA_INTENT, data)
        getActivity().startService(intent)
    }

    override fun stopRecord() {
    }

    private fun requestScreenShot() {
        val mediaProjectionManager = getActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        getActivity().startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), SCREENSHOT_REQUEST_CODE)
    }

    private fun takeScreenShot(data: Intent, resultCode: Int) {
        var screenShotImage: com.example.tuananhe.myapplication.data.model.Image? = null
        val mediaProjectionManager = getActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val mProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
        val wm = getActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val size = Point()
        display.getRealSize(size)
        val mWidth: Int = size.x
        val mHeight: Int = size.y
        val mDensity = metrics.densityDpi

        val mImageReader: ImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2)

        val handler = Handler()

        val flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
        mProjection.createVirtualDisplay("screen-mirror", mWidth,
                mHeight, mDensity, flags, mImageReader.surface, null, handler)
        mImageReader.setOnImageAvailableListener({ reader ->
            reader.setOnImageAvailableListener(null, handler)
            val image: Image = reader.acquireLatestImage()
            val planes: Array<Plane> = image.planes
            val buffer: ByteBuffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * metrics.widthPixels
            // create bitmap
            val bmp = Bitmap.createBitmap(metrics.widthPixels + (rowPadding.toFloat() / pixelStride.toFloat()).toInt(), mHeight, Bitmap.Config.ARGB_8888)
            bmp.copyPixelsFromBuffer(buffer)
            image.close()
            reader.close()

            val setting = Settings.getSetting()
            val screenshot = File(setting.rootImageDirectory, "Screenshot"
                    + Long.toHexString(System.currentTimeMillis()) + ".png")
            val outputStream = FileOutputStream(screenshot)
            val quality = 100
            bmp.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            Handler().postDelayed({
                screenShotImage = com.example.tuananhe.myapplication.data.model.Image(screenshot.name, screenshot.canonicalPath, mWidth, mHeight)
                view.onScreenShotSuccess(screenShotImage, bmp)
            }, 100)
        }, handler)
    }

}