package com.example.schoolplanner.ui.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.schoolplanner.databinding.FragmentWeatherBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class WeatherFragment : Fragment() {
    companion object {
        var PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        getLocation()
        return binding.root
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        activity?.let {
            if(hasPermissions(activity as Context, PERMISSIONS)) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if(location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val list: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        getWeather(list[0].locality.toString(), list[0].countryCode.toString())
                    }
                    else {
                        Log.d("Location", "error 1")
                    }
                }
            }
            else {
                permissionRequireLauncher.launch(PERMISSIONS)
            }
        }
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private val permissionRequireLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {permissions ->
        val granted = permissions.entries.all {
            it.value == true
        }
        if(granted) {
            getLocation()
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun getWeather(cityName : String, countryCode : String) {
        val units = "metric"
        val keyAPI = "ad3e0e8a748a956db26f4cb39403848c"
        var response: String?

        myExecutor.execute {
            response = getAPI(cityName, countryCode, units, keyAPI, countryCode)
            myHandler.post {
                try {
                    val jsonObject = JSONObject(response.toString())
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

                    setVisibilityLayout(1)
                }
                catch (e: Exception) {
                    setVisibilityLayout(-1)
                }
            }
        }
    }

    private fun getAPI(cityName: String, countryCode: String, units : String, keyAPI : String, lang : String) : String? {
        var response = try {
            URL("https://api.openweathermap.org/data/2.5/weather?q=${cityName},${countryCode}&units=${units}&appid=${keyAPI}&lang=${lang}").readText(
                Charsets.UTF_8
            )
        }
        catch (e: Exception) {
            null
        }
        return response
    }

    private fun setVisibilityLayout(status : Int) {
        when (status) {
            0 -> {
                binding.infoOK.visibility = View.GONE
                binding.infoError.visibility = View.VISIBLE
            }
            1 -> {
                binding.infoOK.visibility = View.VISIBLE
                binding.infoError.visibility = View.GONE
            }
            else -> {
                binding.infoOK.visibility = View.GONE
                binding.infoError.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}