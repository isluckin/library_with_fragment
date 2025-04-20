package com.example.library_with_fragment

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RadioGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.library_with_fragment.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ItemViewModel by viewModels()

    private val isLandscape: Boolean
        get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.loadItems()
            setup(savedInstanceState)
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.addButton.setOnClickListener {
            lifecycleScope.launch {
                showDialog()
            }
        }

        viewModel.selectedItem.observe(this) { item ->
            if (item != null) {
                val detailFragment = DetailFragment.newInstance(item)
                if (isLandscape) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.detail_container, detailFragment).commit()
                } else {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.single_container, detailFragment).addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val listFragment = supportFragmentManager.findFragmentById(R.id.list_container)
        val detailFragment = supportFragmentManager.findFragmentById(R.id.detail_container)

        if (listFragment != null) {
            supportFragmentManager.putFragment(outState, "listFragment", listFragment)
        }
        if (detailFragment != null) {
            supportFragmentManager.putFragment(outState, "detailFragment", detailFragment)
        }
    }

    private fun showDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.menu, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)

        val dialog = AlertDialog.Builder(this).setTitle("Add new Item").setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val selectedItem = when (radioGroup.checkedRadioButtonId) {
                    R.id.addBook -> Book.createEmptyBook()
                    R.id.addNews -> Newspaper.createEmptyNewspaper()
                    R.id.addDisk -> Disk.createEmptyDisk()
                    else -> null
                }

                selectedItem?.let {
                    viewModel.currentlyEditingItem = it
                    viewModel.isInEditMode = true

                    val detailFragment = DetailFragment.newInstance(it, isEditMode = true)

                    val containerId =
                        if (isLandscape) R.id.detail_container else R.id.single_container

                    supportFragmentManager.beginTransaction().replace(containerId, detailFragment)
                        .addToBackStack(null).commit()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.create()

        dialog.show()
    }
    private fun setup(state: Bundle?)
    {
        if (viewModel.isInEditMode && viewModel.currentlyEditingItem != null) {
            val detailFragment = DetailFragment.newInstance(
                viewModel.currentlyEditingItem, isEditMode = true
            )
            if (isLandscape) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.list_container, ListFragment()).commit()

            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.single_container, detailFragment).addToBackStack(null).commit()
            }
        }
        if (state == null) {
            if (isLandscape) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.list_container, ListFragment())
                    .replace(R.id.detail_container, DetailFragment.newInstance(null)).commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.single_container, ListFragment()).commit()
            }
        } else {
            if (isLandscape) {
                val listFragment = supportFragmentManager.findFragmentByTag("listFragment")
                val detailFragment = supportFragmentManager.findFragmentByTag("detailFragment")

                if (listFragment == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.list_container, ListFragment(), "listFragment").commit()
                }

                if (detailFragment == null) {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.detail_container, DetailFragment.newInstance(null), "detailFragment"
                    ).commit()
                }
            } else {
                val listFragment = supportFragmentManager.findFragmentByTag("listFragment")

                if (listFragment == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.single_container, ListFragment(), "listFragment").commit()
                }
            }
        }

    }
}