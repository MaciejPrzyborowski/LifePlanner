package com.example.schoolplanner.ui.weather

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.schoolplanner.R
import com.example.schoolplanner.databinding.FragmentWeatherBinding
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class WeatherFragment : Fragment() {
    private val cityName: String = "Poznań"
    private val countryCode: String = "PL"
    private val units : String = "metric"
    private val keyAPI: String = "06c921750b9a82d8f5d1294e1586276f" // Use API key
    private val lang: String = "PL"
    private var response : String? = null

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        weatherTask()
        return binding.root
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun weatherTask() {
        myExecutor.execute {
            this.response = try {
                URL("https://api.openweathermap.org/data/2.5/weather?q=${cityName},${countryCode}&units=${units}&appid=${keyAPI}&lang=${lang}").readText(
                    Charsets.UTF_8
                )
            }
            catch (e: Exception) {
                null
            }

            myHandler.post {
                try {
                    val jsonObject = JSONObject(this.response)
                    val main = jsonObject.getJSONObject("main")
                    val sys = jsonObject.getJSONObject("sys")
                    val wind = jsonObject.getJSONObject("wind")
                    val weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")

                    val address = jsonObject.getString("name") + ", " + sys.getString("country")
                    val updated = "Zaktualizowano: " + SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(
                        Date(jsonObject.getLong("dt")*1000))

                    val temp = main.getString("temp").toFloat().roundToInt().toString() + "°C"
                    val tempFeel = "Odczuwalna: " + main.getString("feels_like").toFloat().roundToInt().toString() + "°C"
                    val sunrise = sys.getLong("sunrise")
                    val sunset = sys.getLong("sunset")
                    val windSpeed = wind.getString("speed")
                    val pressure = main.getString("pressure")
                    val humidity = main.getString("humidity")

                    binding.address.text = address
                    binding.updated.text = updated
                    binding.status.text = weather.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }

                    binding.temp.text = temp
                    binding.tempFeel.text = tempFeel
                    binding.sunrise.text = SimpleDateFormat("HH:mm:ss").format(
                        Date(sunrise*1000))
                    binding.sunset.text = SimpleDateFormat("HH:mm:ss").format(
                        Date(sunset*1000))
                    binding.wind.text = ((windSpeed.toFloat() * 3.6).roundToInt().toString() + " km/h")
                    binding.pressure.text = "$pressure hPa"
                    binding.humidity.text = "$humidity%"

                    binding.infoOK.visibility = View.VISIBLE
                    binding.infoError.visibility = View.GONE

                }
                catch (e: Exception) {
                    binding.infoOK.visibility = View.GONE
                    binding.infoError.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}