package com.example.hw05_firebase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hw05_firebase.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * A simple [Fragment] subclass.
 * Use the [MapsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapsFragment : Fragment() {
    private var binding: FragmentMapsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            // Retrieve the coordinates passed from RecyclerViewFragment
            val latitude = arguments?.getDouble("latitude") ?: return@getMapAsync
            val longitude = arguments?.getDouble("longitude") ?: return@getMapAsync

            // Create a LatLng object from the coordinates
            val location = LatLng(latitude, longitude)

            // Add a marker to the map at the retrieved location
            googleMap.addMarker(MarkerOptions().position(location).title("New Location"))

            // Optionally move the camera to the location with animation
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }

        return binding?.root
    }
}