package com.example.hw05_firebase

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hw05_firebase.databinding.FragmentRecyclerViewBinding
import com.example.hw05_firebase.databinding.LocationItemViewBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecyclerViewFragment : Fragment() {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val TAG = "RecyclerViewFragment"
    }

    private var _binding: FragmentRecyclerViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ItemAdapter
    private lateinit var database: DatabaseReference
    private val locationItems: MutableList<LocationItem> = mutableListOf()
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult.locations.isNotEmpty()) {
                val location = locationResult.lastLocation
                val address = location?.let { getAddressFromLocation(it) }
                val newItem = if (address != null) {
                    LocationItem(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        address = address,
                        timestamp = System.currentTimeMillis()
                    )
                } else null

                // Make sure to run on the UI thread since we are updating UI components
                newItem?.let { item ->
                    activity?.runOnUiThread {
                        locationItems.add(item)
                        adapter.notifyItemInserted(locationItems.size - 1)

                        writeLocationItemToFirebase(item)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecyclerViewBinding.inflate(inflater, container, false)

        // Set up the RecyclerView
        adapter = ItemAdapter(locationItems) { locationItem ->
            val bundle = Bundle().apply {
                locationItem.latitude?.let { putDouble("latitude", it) }
                locationItem.longitude?.let { putDouble("longitude", it) }
                putString("address", locationItem.address)
                locationItem.timestamp?.let { putLong("timestamp", it) }
            }
            findNavController().navigate(R.id.action_recyclerViewFragment_to_mapsFragment, bundle)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        database = Firebase.database.reference

        readLocationItemsFromFirebase()

        // Set up the Floating Action Button (FAB)
        binding.fab.setOnClickListener {
            checkPermissionsAndStartLocationRetrieval()
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        // Make sure to check for permissions to avoid SecurityException
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.getFusedLocationProviderClient(requireActivity())
                .removeLocationUpdates(locationCallback)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun writeLocationItemToFirebase(locationItem: LocationItem) {
        // Create a unique key for each new item
        val key = database.child("locations").push().key
        if (key != null) {
            // Write the new item under the "locations" child using the unique key
            database.child("locations").child(key).setValue(locationItem)
                .addOnSuccessListener {
                    Log.d(TAG, "LocationItem saved successfully to Firebase.")
                }
                .addOnFailureListener {
                    Log.e(TAG, "Failed to save LocationItem to Firebase.", it)
                }
        }
    }

    private fun readLocationItemsFromFirebase() {
        // Assuming "locations" is the node where you store location items
        val locationsRef = database.child("locations")

        // Add a value event listener to the "locations" reference
        locationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Clear the existing list
                locationItems.clear()

                // Iterate through all children, this gets you each location item
                for (locationSnapshot in dataSnapshot.children) {
                    // Convert the snapshot to a LocationItem object
                    val locationItem = locationSnapshot.getValue(LocationItem::class.java)
                    locationItem?.let {
                        // Add the item to your list
                        locationItems.add(it)
                    }
                }

                // Notify your adapter that the data has changed so it can update the UI
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error on reading value
                Log.e(TAG, "Failed to read location items.", databaseError.toException())
            }
        })
    }


    private fun checkPermissionsAndStartLocationRetrieval() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        } else {
            getCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            // Permission was denied. Handle the case where the user denies the permission.
        }
    }

    private fun getCurrentLocation() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        val locationRequest = LocationRequest.create()?.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if (locationRequest != null) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun getAddressFromLocation(location: Location): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: MutableList<Address>? =
            geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (addresses != null) {
            return if (addresses.isNotEmpty()) {
                addresses[0].getAddressLine(0) // Get the first address line
            } else {
                "Unknown Location"
            }
        }

        return "Address from location is null"
    }


    class ItemAdapter(
        private val locationItems: List<LocationItem>,
        private val onItemSelected: (LocationItem) -> Unit
    ) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val binding =
                LocationItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val item = locationItems[position]
            with(holder.binding) {
                coordinates.text = "Lat: ${item.latitude}, Long: ${item.longitude}"
                address.text = item.address
                timestamp.text =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                        item.timestamp?.let { Date(it) }
                    )

                // Set the click listener for the item view
                holder.itemView.setOnClickListener {
                    onItemSelected(item)
                }
            }
        }

        override fun getItemCount() = locationItems.size

        class ItemViewHolder(val binding: LocationItemViewBinding) :
            RecyclerView.ViewHolder(binding.root)
    }
}