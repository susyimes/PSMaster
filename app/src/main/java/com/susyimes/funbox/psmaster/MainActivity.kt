package com.susyimes.funbox.psmaster

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var bgBitmap: Bitmap? = null
    private var resultBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        refresh.setOnClickListener {
            resultBitmap=null
            bgImage.setImageBitmap(null)
            resultImage.setImageBitmap(null)
            bgBitmap = null
            drawView.clear()
        }
        save.setOnClickListener {

            if (resultBitmap != null) {
                if (bgBitmap == null) {
                    SaveUtils().saveBitmap(
                        this,
                        resultBitmap,
                        System.currentTimeMillis().toString()
                    )
                } else {
                    resultBitmap = cropMaskOnImage(bgBitmap!!, resultBitmap)
                    SaveUtils().saveBitmap(
                        this,
                        resultBitmap,
                        System.currentTimeMillis().toString()
                    )
                }
            }
        }
        cut.setOnClickListener {
            if (bgBitmap != null) {
                resultBitmap = cropBitmapWithMask(bgBitmap!!, drawView.viewGetImage())
                Glide.with(this).load(resultBitmap)
                    .into(resultImage)
                bgImage.setImageBitmap(null)
                bgBitmap = null
                drawView.clear()
            } else {
                Toast.makeText(this, "oppssss,你还没有放置背景图", Toast.LENGTH_SHORT).show()
            }
        }
        revert.setOnClickListener {
            drawView.goBack()
        }
        add.setOnClickListener {
            PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine()) // Please refer to the Demo GlideEngine.java
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        Log.e("pssssss", Uri.parse(result?.get(0)?.path.toString()).toString())

                        Glide.with(this@MainActivity).asBitmap().load(
                            MediaStore.Images.Media.getBitmap(
                                contentResolver,
                                Uri.parse(result?.get(0)?.path.toString())
                            )
                        ).centerCrop()
                            .listener(object : RequestListener<Bitmap> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Bitmap>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return true
                                }

                                override fun onResourceReady(
                                    resource: Bitmap?,
                                    model: Any?,
                                    target: Target<Bitmap>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    bgBitmap = resource
                                    return false
                                }

                            }).into(bgImage)


                    }

                    override fun onCancel() {
                    }
                })
        }
    }

    //https://developer.android.com/reference/android/graphics/PorterDuff.Mode?authuser=1
    private fun cropBitmapWithMask(original: Bitmap, mask: Bitmap?): Bitmap? {
        if (mask == null
        ) {
            return null
        }
        val w = original.width
        val h = original.height
        if (w <= 0 || h <= 0) {
            return null
        }
        val styled = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(styled)
        val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawBitmap(original, 0f, 0f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

        canvas.drawBitmap(mask, 0f, 0f, paint)
        paint.xfermode = null
        return styled
    }

    private fun cropMaskOnImage(original: Bitmap, mask: Bitmap?): Bitmap? {
        if (mask == null
        ) {
            return null
        }
        val w = original.width
        val h = original.height
        if (w <= 0 || h <= 0) {
            return null
        }
        val styled = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(styled)
        val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawBitmap(original, 0f, 0f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        canvas.drawBitmap(mask, 0f, 0f, paint)
        paint.xfermode = null
        return styled
    }
}