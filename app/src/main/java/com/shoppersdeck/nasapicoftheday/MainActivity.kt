package com.shoppersdeck.nasapicoftheday

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.shoppersdeck.nasapicoftheday.data.Factory.ApiFactory
import com.shoppersdeck.nasapicoftheday.data.models.DataResponse
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    val cal = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getData("DEMO_KEY","")
        //yyyy-mm-dd

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        btn_calender!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@MainActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

        })

        layout_refresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )
        )
        layout_refresh.setColorSchemeColors(Color.WHITE)
        layout_refresh.setOnRefreshListener {
            getData("DEMO_KEY","")
        }
    }

    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        //txtError!!.text = sdf.format(cal.getTime())
        getData("DEMO_KEY",sdf.format(cal.getTime()))
    }

    private fun getData(mKey:String,date:String) {
        val call: Call<DataResponse> = ApiFactory.getClient.getData(mKey,date)
        call.enqueue(object : Callback<DataResponse> {
            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                Log.d("resp", t.message)
                layout_refresh.isRefreshing = false
                Toast.makeText(
                    this@MainActivity,
                    "Swipe to refresh",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onResponse(
                call: Call<DataResponse>,
                response: Response<DataResponse>
            ) {
                layout_refresh.isRefreshing = false
                response.body()?.let {
                    setUI(it)
                }
            }

        }
        )
    }


    fun retrieveVideoFrameFromVideo(videoPath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(videoPath, HashMap<String, String>())
            bitmap = mediaMetadataRetriever.frameAtTime

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setUI(it: DataResponse) {
        val media_url: String = it.url

        if (it.media_type == "image") {
            setVisibility(true)
            player_video.visibility=View.INVISIBLE
            txt_desc.setTextColor(Color.WHITE)
            play_or_zoom.setBackgroundResource(R.drawable.ic_zoom_in_black_24dp)
            Glide.with(this)
                .load(it.url)
                .into(bg_img)
        } else {
            play_or_zoom.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp)
            val bm = retrieveVideoFrameFromVideo(it.url)
            img_thumbnail.setImageBitmap(bm)

        }
        play_or_zoom.setOnClickListener {
            //player_video.start() webview
            img_thumbnail.visibility = View.INVISIBLE
            player_video.visibility = View.VISIBLE
            player_video.settings.javaScriptEnabled = true
            player_video.loadUrl(media_url)
        }
        txt_title.setText(it.title)
        txt_desc.setText("Description\n"+it.explanation)
        txt_desc.movementMethod=ScrollingMovementMethod()
    }

    fun setVisibility(image: Boolean) {
        if (image) {
            txtError.visibility = VISIBLE
            player_video.visibility = GONE
        } else {
            txtError.visibility = GONE
            player_video.visibility = VISIBLE
        }
    }
}