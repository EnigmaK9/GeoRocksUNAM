// File path: app/src/main/java/com/enigma/georocks/ui/fragments/RocksListFragment.kt

package com.enigma.georocks.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.enigma.georocks.R
import com.enigma.georocks.application.GeoRocksApp
import com.enigma.georocks.data.RockRepository
import com.enigma.georocks.data.db.FavoriteRockEntity
import com.enigma.georocks.data.remote.model.RockDto
import com.enigma.georocks.databinding.FragmentRocksListBinding
import com.enigma.georocks.ui.activities.LoginActivity
import com.enigma.georocks.ui.adapters.RocksAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * RocksListFragment displays a list of rocks fetched from an API.
 * It includes toggling between showing all rocks or favorite rocks.
 */
class RocksListFragment : Fragment() {

    // View binding for fragment_rocks_list.xml
    private var _binding: FragmentRocksListBinding? = null
    private val binding get() = _binding!!

    // Firebase Auth instance
    private lateinit var auth: FirebaseAuth

    // Repository for fetching rocks and details
    private lateinit var repository: RockRepository

    // Adapter for displaying the list of rocks
    private lateinit var rocksAdapter: RocksAdapter

    // A cached list of all rocks fetched from the API
    private var fullRocksList: MutableList<RockDto> = mutableListOf()

    // A flag to indicate whether favorites are being shown
    private var showingFavorites: Boolean = false

    /**
     * The fragment's view is inflated using FragmentRocksListBinding.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRocksListBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * FirebaseAuth, the RockRepository, and the RecyclerView are set up here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = (requireActivity().application as GeoRocksApp).repository
        auth = FirebaseAuth.getInstance()

        // A linear LayoutManager is used for the RecyclerView
        binding.rvRocks.layoutManager = LinearLayoutManager(requireContext())

        // An empty adapter is set initially
        rocksAdapter = RocksAdapter(emptyList()) { rockId, viewHolder ->
            // Additional details can be loaded asynchronously
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val detail = repository.getRockDetail(rockId)
                    viewHolder.updateDetails(detail.aMemberOf, detail.color)
                } catch (e: Exception) {
                    Log.e("RocksListFragment", "Error loading rock detail: ${e.localizedMessage}")
                }
            }
        }
        binding.rvRocks.adapter = rocksAdapter

        // The menu (favorites and logout) is configured
        setupMenu()

        // The initial list of rocks is loaded
        loadRocks()
    }

    /**
     * The menu with "Favorites" and "Logout" is set up using a MenuProvider.
     */
    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Inflate the updated menu for rock listing
                menuInflater.inflate(R.menu.menu_rocks_list, menu)

                // Since search is removed, no need to set up SearchView
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_view_favorites -> {
                        // Toggle between showing all rocks and favorites
                        if (!showingFavorites) {
                            showFavorites()
                        } else {
                            showAllRocks()
                        }
                        true
                    }
                    R.id.action_logout -> {
                        performLogout()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /**
     * The rocks are fetched from the API in a coroutine and updated in the adapter.
     */
    private fun loadRocks() {
        binding.pbLoading.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val rocks = repository.getRocksApiary()
                fullRocksList = rocks

                withContext(Dispatchers.Main) {
                    rocksAdapter.updateData(rocks)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "No connection available", Toast.LENGTH_SHORT).show()
                    Log.e("RocksListFragment", "Error fetching rocks: ${e.localizedMessage}")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.pbLoading.visibility = View.GONE
                }
            }
        }
    }

    /**
     * The search functionality is removed. Only filtering by favorites is available.
     */

    /**
     * Favorites from Room are displayed.
     */
    private fun showFavorites() {
        showingFavorites = true
        Toast.makeText(requireContext(), "Now showing Favorites", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch(Dispatchers.IO) {
            val favoriteEntities = getFavoriteRepository().getAllFavorites()
            val favoriteRocks = favoriteEntities.map { it.toRockDto() }

            withContext(Dispatchers.Main) {
                rocksAdapter.updateData(favoriteRocks)
            }
        }
    }

    /**
     * The full list is restored if it was previously filtered by favorites.
     */
    private fun showAllRocks() {
        showingFavorites = false
        Toast.makeText(requireContext(), "Now showing All Rocks", Toast.LENGTH_SHORT).show()
        rocksAdapter.updateData(fullRocksList)
    }

    /**
     * The user is logged out from Firebase, and LoginActivity is started.
     */
    private fun performLogout() {
        auth.signOut()
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    /**
     * Helper function to retrieve FavoriteRepository from the application.
     */
    private fun getFavoriteRepository() =
        (requireActivity().application as GeoRocksApp).favoriteRepository

    /**
     * The ViewBinding is cleared when the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Converts a FavoriteRockEntity to a RockDto for displaying in the adapter.
     */
    private fun FavoriteRockEntity.toRockDto(): RockDto {
        return RockDto(
            id = this.rockId,
            thumbnail = this.thumbnail,
            title = this.title
        )
    }
}
