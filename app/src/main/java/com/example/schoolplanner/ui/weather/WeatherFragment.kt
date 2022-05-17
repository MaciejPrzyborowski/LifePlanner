package com.example.schoolplanner.ui.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
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
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    var cityName: String = "Poznań"
    var countryCode: String = "PL"
    private val units : String = "metric"
    private val keyAPI: String = "ad3e0e8a748a956db26f4cb39403848c"
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        getLocation()
        return binding.root
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                val location: Location? = task.result
                if (location != null) {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val list: List<Address> =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    Log.d("Latitude",list[0].latitude.toString())
                    Log.d("Longitude",list[0].longitude.toString())
                    Log.d("Country Name",list[0].countryName.toString())
                    Log.d("Locality",list[0].locality.toString())
                    Log.d("Address",list[0].getAddressLine(0).toString())
                    countryCode = list[0].countryCode.toString()
                    cityName = list[0].locality.toString()
                    weatherTask()

                }
                else
                {
                    Log.d("Location", "error 1")
                }
            }
            Log.d("Location", "error 2")
        } else {
            requestPermissions()
        }
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
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
                    val jsonObject = JSONObject(this.response.toString())
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