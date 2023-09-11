package com.app.mybase

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.app.mybase.base.BaseActivity
import com.app.mybase.databinding.ActivityMainBinding
import dagger.android.AndroidInjection
import javax.inject.Inject


class MainActivity : BaseActivity() {

    val TAG = this::class.java.name
    lateinit var binding: ActivityMainBinding
    lateinit var viewmodel: MainViewModel
    lateinit var navController: NavController

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewmodel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        binding.mainViewModel = viewmodel
        binding.lifecycleOwner = this@MainActivity

        initNavigation()

    }

    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupWithNavController(binding.bottomNavView, navController)
    }


}