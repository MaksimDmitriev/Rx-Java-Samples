package ru.sample

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "RxJavaSamples"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        combineLatest.setOnClickListener {
            val networkRepo = NetworkRepo()
            val localRepo = LocalRepo()

            Observable.combineLatest(networkRepo.getData(), localRepo.getData(), BiFunction<Data, Data, Data> { t1, t2 ->
                if (t1.timeStamp > t2.timeStamp) {
                    t1
                } else {
                    t2
                }
            }).subscribe { data ->
                Log.d(TAG, "data=$data")
            }
        }

        Observable.combineLatest(
                RxTextView.textChanges(loginEditText),
                RxTextView.textChanges(passEditText),
                BiFunction<CharSequence, CharSequence, Boolean> { login, pass ->
                    login.isNotEmpty() && pass.isNotEmpty()
                }
        ).subscribe(loginButton::setEnabled)
        // https://youtu.be/3jdvLrYZfB4?t=1190 - No BiFunction TODO

        switchIfEmpty.setOnClickListener {
            val networkRepo = NetworkRepo()
            val localRepo = LocalRepo()
            networkRepo.getRawData().switchIfEmpty(
                    localRepo.getRawData()
            ).subscribe { s ->
                Log.d(TAG, "s=$s")
            }
        }

        loadLargeImage.setOnClickListener {
            Single.fromCallable {
                decodeSampledBitmapFromAssets(
                        resources.getDimensionPixelSize(R.dimen.required_size),
                        resources.getDimensionPixelSize(R.dimen.required_size)
                )
            }.subscribe(
                    { bitmap ->
                        largeImage.setImageBitmap(bitmap)
                    },
                    { e ->
                        Toast.makeText(MainActivity@ this, "Error while loading a large image", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "", e)
                    }
            )
        }
    }

    private fun decodeSampledBitmapFromAssets(reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val stream = assets.open("large_image.png")
        stream.mark(Int.MAX_VALUE)
        BitmapFactory.decodeStream(stream, null, options)
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false

        stream.reset()
        return BitmapFactory.decodeStream(stream, null, options)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}
