package com.enigma.georocks.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.enigma.georocks.R
import com.enigma.georocks.application.GeoRocksApp
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.data.remote.model.RockDetailDto
import com.enigma.georocks.data.remote.model.RockDto
import com.enigma.georocks.databinding.FragmentRocksListBinding
import com.enigma.georocks.ui.activities.LoginActivity
import com.enigma.georocks.ui.adapters.RocksAdapter
import com.enigma.georocks.ui.adapters.RocksViewHolder
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RocksListFragment : Fragment() {

    // The view binding variable is declared
    private var _binding: FragmentRocksListBinding? = null
    private val binding get() = _binding!!

    // FirebaseAuth is declared
    private lateinit var auth: FirebaseAuth

    // Repository for data is declared
    private lateinit var repository: RockRepository

    // The full list of rocks is held for search/filtering
    private var fullRocksList: MutableList<RockDto> = mutableListOf()

    // The adapter is declared as a global property
    private lateinit var rocksAdapter: RocksAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // The binding is inflated here
        _binding = FragmentRocksListBinding.inflate(inflater, container, false)
        return binding.root
    }

    // The fragment is set up here with data loading and toolbar setup
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // The repository and auth instances are retrieved
        repository = (requireActivity().application as GeoRocksApp).repository
        auth = FirebaseAuth.getInstance()

        // The toolbar is configured
        setupToolbar()

        // The menu is added using MenuProvider instead of the deprecated setHasOptionsMenu
        setupMenu()

        // The list of rocks is requested from the repository
        loadRocks()
    }

    // The toolbar's menu item clicks are managed here
    private fun setupToolbar() {
        binding.toolbarRocksList.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    performLogout()
                    true
                }
                else -> false
            }
        }
    }

    // A logout event is handled here
    private fun performLogout() {
        // The signOut() method is called on FirebaseAuth
        auth.signOut()
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
        // The user is taken to the Login screen, finishing the current activity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    // The menu is set up with a MenuProvider
    private fun setupMenu() {
        // A MenuHost is created from the activity
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // The custom menu is inflated
                menuInflater.inflate(R.menu.menu_rocks_list, menu)

                // The SearchView handling is placed here if a search icon is in the menu
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as? android.widget.SearchView
                // The search hint is set
                searchView?.queryHint = getString(R.string.menu_search_hint)
                // The query text listener is set to filter the rocks on text change
                searchView?.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        filterRocks(newText ?: "")
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Additional logic can be placed here if needed
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    // This function calls the repository to fetch all rocks
    private fun loadRocks() {
        // The loading indicator is made visible
        binding.pbLoading.visibility = View.VISIBLE

        val call: Call<MutableList<RockDto>> = repository.getRocksApiary()
        call.enqueue(object : Callback<MutableList<RockDto>> {
            override fun onResponse(call: Call<MutableList<RockDto>>, response: Response<MutableList<RockDto>>) {
                // The loading indicator is hidden
                binding.pbLoading.visibility = View.GONE

                if (response.isSuccessful) {
                    response.body()?.let { rocks ->
                        // The full list of rocks is saved for filtering
                        fullRocksList = rocks
                        // The RecyclerView is set up with the adapter
                        setupRecyclerView(rocks)
                    }
                } else {
                    // A toast is shown for server-side errors
                    Toast.makeText(requireContext(), "Error loading data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MutableList<RockDto>>, t: Throwable) {
                // A toast is shown for network/connection issues
                Toast.makeText(requireContext(), "No connection available", Toast.LENGTH_SHORT).show()
                binding.pbLoading.visibility = View.GONE
            }
        })
    }

    // The RecyclerView is set up here
    private fun setupRecyclerView(rocks: List<RockDto>) {
        // A new RocksAdapter instance is assigned with a callback for loading details
        rocksAdapter = RocksAdapter(rocks) { rockId, viewHolder ->
            loadRockDetails(rockId, viewHolder)
        }
        binding.rvRocks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rocksAdapter
        }
    }

    // Each rock's additional details are loaded for that ViewHolder
    private fun loadRockDetails(rockId: String, viewHolder: RocksViewHolder) {
        repository.getRockDetail(rockId).enqueue(object : Callback<RockDetailDto> {
            override fun onResponse(call: Call<RockDetailDto>, response: Response<RockDetailDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { detail ->
                        // The additional info is applied to the ViewHolder
                        viewHolder.updateDetails(detail.aMemberOf, detail.color)
                    }
                }
            }

            override fun onFailure(call: Call<RockDetailDto>, t: Throwable) {
                // The failure is logged or displayed
            }
        })
    }

    // This function filters rocks by matching titles with the search query
    private fun filterRocks(query: String) {
        // A filtered list is created from the full list
        val filteredList = fullRocksList.filter { rock ->
            rock.title.contains(query, ignoreCase = true)
        }
        // The RocksAdapter is updated with the filtered list
        rocksAdapter.updateData(filteredList)
    }

    // The binding is cleaned up here
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
