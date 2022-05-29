package com.life.planner.ui.weather

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.life.planner.R
import com.life.planner.databinding.FragmentWeatherBinding
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.roundToInt

/**
 * Klasa obsługująca widok fragmentu Weather
 *
 */
@Suppress("DEPRECATION")
class WeatherFragment : Fragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private var locationRequestCode = 99
    private var locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val mLocationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 600
        fastestInterval = 500
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    private var mLocationRequestCallback: LocationCallback = object : LocationCallback() {}

    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())

    /**
     * Funkcja wykonywana przy tworzeniu widoku
     *
     * @param inflater - uchwyt LayoutInflater
     * @param container - uchwyt grupy widoków
     * @param savedInstanceState - uchwyt Bundle
     * @return widok fragmentu
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        binding.refresh.setOnClickListener {
            requestUpdateLocation()
            getLocation()
        }
        requestUpdateLocation()
        getLocation()
        return binding.root
    }

    /**
     * Wymaga aktualizację lokalizacji
     *
     */
    @SuppressLint("MissingPermission")
    private fun requestUpdateLocation() {
        if(hasPermissions(requireContext(), locationPermissions))
        {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationRequestCallback, Looper.getMainLooper())
        }
    }

    /**
     * Pobiera aktualne dane lokalizacyjne
     *
     */
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        setVisibilityLayout(-1)
        if (hasPermissions(requireContext(), locationPermissions)) {
            mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                val location: Location? = task.result
                if (location != null) {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val geocoderLocation: List<Address> =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    getWeather(
                        location.latitude,
                        location.longitude,
                        geocoderLocation[0].countryCode.toString()
                    )
                } else {
                    setVisibilityLayout(0)
                }
            }
            mFusedLocationClient.lastLocation.addOnFailureListener(requireActivity()) {
                setVisibilityLayout(0)
            }
        } else {
            requirePermissions(locationPermissions)
        }
    }

    /**
     * Pobiera i przetwarza aktualne dane pogodowe dla podanych współrzędnych geograficznych
     *
     * @param latitude - szerokość geograficzna
     * @param longitude - długość geograficzna
     * @param countryCode - kod kraju
     */
    private fun getWeather(latitude: Double, longitude: Double, countryCode: String) {
        var response: String?

        myExecutor.execute {
            response = getAPI(latitude, longitude, countryCode)
            myHandler.post {
                try {
                    val jsonObject = JSONObject(response.toString())
                    val main = jsonObject.getJSONObject("main")
                    val sys = jsonObject.getJSONObject("sys")
                    val wind = jsonObject.getJSONObject("wind")
                    val weather =
                        jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")

                    val address = jsonObject.getString("name") + ", " + sys.getString("country")
                    val updated =
                        resources.getString(R.string.weather_updated_date) + ": " + SimpleDateFormat(
                            "dd/MM/yyyy HH:mm:ss",
                            Locale.getDefault()
                        ).format(
                            Date(jsonObject.getLong("dt") * 1000)
                        )

                    val temp = main.getString("temp").toFloat().roundToInt().toString() + "°C"
                    val tempFeel =
                        resources.getString(R.string.weather_feelTemperature_menu) + ": " + main.getString(
                            "feels_like"
                        ).toFloat().roundToInt()
                            .toString() + "°C"
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
                    binding.sunrise.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(
                        Date(sunrise * 1000)
                    )
                    binding.sunset.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(
                        Date(sunset * 1000)
                    )
                    val windText = String.format(
                        "%s ",
                        (windSpeed.toFloat() * 3.6).roundToInt().toString()
                    ) + resources.getString(R.string.weather_wind_units)
                    binding.wind.text = windText
                    val pressureText = String.format(
                        "%s ",
                        pressure.toString()
                    ) + resources.getString(R.string.weather_pressure_units)
                    binding.pressure.text = pressureText
                    val humidityText = String.format(
                        "%s",
                        humidity.toString()
                    ) + resources.getString(R.string.weather_humidity_units)
                    binding.humidity.text = humidityText

                    setVisibilityLayout(1)
                } catch (e: Exception) {
                    setVisibilityLayout(0)
                }
            }
        }
    }

    /**
     * Pobiera dane pogody z API (format danych JSON)
     *
     * @param latitude - szerokość geograficzna
     * @param longitude - długość geograficzna
     * @param lang - język pobieranych danych
     * @return dane pogody w formacie JSON
     */
    private fun getAPI(latitude: Double, longitude: Double, lang: String): String? {
        val units = "metric"
        val keyAPI = "ad3e0e8a748a956db26f4cb39403848c"
        val response = try {
            URL("https://api.openweathermap.org/data/2.5/weather?lat=${latitude}&lon=${longitude}&units=${units}&appid=${keyAPI}&lang=${lang}").readText(
                Charsets.UTF_8
            )
        } catch (e: Exception) {
            null
        }
        return response
    }

    /**
     * Ustawia widoczność layoutów na podstawie statusu przetworzenia danych
     *
     * @param status - status przetworzenia danych
     */
    private fun setVisibilityLayout(status: Int) {
        when (status) {
            0 -> {
                binding.infoOK.visibility = View.GONE
                binding.infoWait.visibility = View.GONE
                binding.infoError.visibility = View.VISIBLE
            }
            1 -> {
                binding.infoOK.visibility = View.VISIBLE
                binding.infoWait.visibility = View.GONE
                binding.infoError.visibility = View.GONE
            }
            else -> {
                binding.infoOK.visibility = View.GONE
                binding.infoWait.visibility = View.VISIBLE
                binding.infoError.visibility = View.GONE
            }
        }
    }

    /**
     * Sprawdza czy użytkownik zezwolił na uprawienia lokalizacji
     *
     * @param context - kontekst aktualnego stanu aplikacji/obiektu
     * @param permissions - lista uprawnień (lokalizacji)
     * @return true - użytkownik zezwolił na uprawnienia lokalizacji
     */
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    /**
     * Wyświetla użytkownikowi komunikat o zezwolenie na uprawnienia lokalizacji
     *
     * @param permissions - lista uprawnień (lokalizacji)
     */
    private fun requirePermissions(permissions: Array<String>) {
        requestPermissions(permissions, locationRequestCode)
    }

    /**
     * Listener, który sprawdza czy użytkownik zezwolił na uprawnienia lokalizacji
     *
     * @param requestCode - kod komunikatu o zezwolenie na uprawnienia lokalizacji
     * @param permissions - lista uprawnień (lokalizacji)
     * @param grantResults - rezult przyznania uprawnień
     */
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            locationRequestCode -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        requestUpdateLocation()
                        getLocation()
                    } else {
                        setVisibilityLayout(0)
                    }
                }
            }
        }
    }

    /**
     * Funkcja wykonywana przy niszczeniu widoku
     *
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}