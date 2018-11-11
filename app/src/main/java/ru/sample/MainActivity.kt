package ru.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
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
    }
}
