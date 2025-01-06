// ============================
// RocksListFragment.kt (Passive Voice Example)
// ============================

package com.enigma.georocks.ui.fragments

import android.content.Intent
import android.os.Bundle
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
import com.enigma.georocks.data.remote.model.RockDetailDto
import com.enigma.georocks.data.remote.model.RockDto
import com.enigma.georocks.databinding.FragmentRocksListBinding
import com.enigma.georocks.ui.activities.LoginActivity
import com.enigma.georocks.ui.adapters.RocksAdapter
import com.enigma.georocks.ui.adapters.RocksViewHolder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RocksListFragment : Fragment() {

    private var _binding: FragmentRocksListBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var repository: RockRepository
    private lateinit var rocksAdapter: RocksAdapter

    private var fullRocksList: MutableList<RockDto> = mutableListOf()
    private var showingFavorites: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRocksListBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * FirebaseAuth and the repository are initialized, and the RecyclerView is configured with a LayoutManager
     * and an initially empty adapter to avoid layout warnings.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = (requireActivity().application as GeoRocksApp).repository
        auth = FirebaseAuth.getInstance()

        setupToolbar()
        setupMenu()

        // The LayoutManager is set so that the RecyclerView can lay out items vertically.
        binding.rvRocks.layoutManager = LinearLayoutManager(requireContext())

        // An initially empty adapter is created to avoid "No adapter attached" warnings.
        rocksAdapter = RocksAdapter(emptyList()) { rockId, viewHolder ->
            // Details are loaded using a coroutine.
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val detail: RockDetailDto = repository.getRockDetail(rockId)
                    viewHolder.updateDetails(detail.aMemberOf, detail.color)
                } catch (e: Exception) {
                    // The error is handled silently or logged if desired.
                }
            }
        }

        // The adapter is attached to the RecyclerView.
        binding.rvRocks.adapter = rocksAdapter

        // Data is loaded next.
        loadRocks()
    }

    /**
     * The FavoriteRepository instance is retrieved from the Application.
     */
    private fun getFavoriteRepository() =
        (requireActivity().application as GeoRocksApp).favoriteRepository

    /**
     * The Toolbar is set up along with its basic logout listener.
     */
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

    /**
     * The menu is added to the host Activity, including search and view favorites.
     */
    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_rocks_list, menu)

                // The SearchView is set up.
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem?.actionView as? android.widget.SearchView
                searchView?.queryHint = getString(R.string.menu_search_hint)
                searchView?.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        filterRocks(newText ?: "")
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_view_favorites -> {
                        if (!showingFavorites) {
                            showFavorites()
                        } else {
                            showAllRocks()
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /**
     * Rocks are loaded using coroutines by calling the suspend method getRocksApiary().
     */
    private fun loadRocks() {
        binding.pbLoading.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // The backend service is contacted to get the rocks.
                val rocks = repository.getRocksApiary()
                fullRocksList = rocks

                // The adapter is updated with the newly loaded data.
                rocksAdapter.updateData(rocks)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "No connection available", Toast.LENGTH_SHORT).show()
            } finally {
                binding.pbLoading.visibility = View.GONE
            }
        }
    }

    /**
     * The list of rocks is filtered based on the search query and whether favorites are being shown.
     */
    private fun filterRocks(query: String) {
        if (showingFavorites) {
            // If favorites are being shown, they are filtered from the database.
            CoroutineScope(Dispatchers.Main).launch {
                val favorites = getFavoriteRepository().getAllFavorites()
                val favAsRockDto = favorites.map { it.toRockDto() }
                val filtered = favAsRockDto.filter {
                    it.title?.contains(query, ignoreCase = true) == true
                }
                rocksAdapter.updateData(filtered)
            }
        } else {
            // Otherwise, the full list loaded in memory is filtered.
            val filteredList = fullRocksList.filter { rock ->
                rock.title?.contains(query, ignoreCase = true) == true
            }
            rocksAdapter.updateData(filteredList)
        }
    }

    /**
     * Favorites are shown by retrieving them from Room.
     */
    private fun showFavorites() {
        showingFavorites = true
        Toast.makeText(requireContext(), "Now showing Favorites", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch(Dispatchers.Main) {
            val favoriteRepo = getFavoriteRepository()
            val favoriteEntities: List<FavoriteRockEntity> = favoriteRepo.getAllFavorites()
            val favoriteRocks = favoriteEntities.map { it.toRockDto() }
            rocksAdapter.updateData(favoriteRocks)
        }
    }

    /**
     * The original full list is restored if it was filtered.
     */
    private fun showAllRocks() {
        showingFavorites = false
        Toast.makeText(requireContext(), "Now showing All Rocks", Toast.LENGTH_SHORT).show()
        rocksAdapter.updateData(fullRocksList)
    }

    /**
     * A FavoriteRockEntity is converted to RockDto for display in the RecyclerView.
     */
    private fun FavoriteRockEntity.toRockDto(): RockDto {
        return RockDto(
            id = this.rockId,
            thumbnail = this.thumbnail,
            title = this.title
        )
    }

    /**
     * The user is logged out of FirebaseAuth, returning to the Login screen.
     */
    private fun performLogout() {
        auth.signOut()
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    /**
     * The binding is cleared when the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
