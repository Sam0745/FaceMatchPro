package com.example.material_3_recyclerview

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.material_3_recyclerview.databinding.ActivityMainBinding
import com.example.material_3_recyclerview.fragment.RecyclerFragment

class MainActivity : AppCompatActivity() {

    companion object {
        var currentPosition = 0
        private const val KEY_CURRENT_POSITION = "com.example.material_3_recyclerview.key.currentPosition"
    }

    /*  lateinit var navController: NavController*/

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)


        /*   val navHostFragment =
               supportFragmentManager.findFragmentById(R.id.containerMain) as NavHostFragment
           navController = navHostFragment.navController

           setupWithNavController(binding.bottomNavigation, navController)*/


        if (savedInstanceState != null) {
            currentPosition =
                savedInstanceState.getInt(KEY_CURRENT_POSITION, 0)
            return
        }
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager
            .beginTransaction()
            .add(R.id.containerMain, RecyclerFragment(), RecyclerFragment::class.java.simpleName)
            .commit()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

        outState!!.putInt(KEY_CURRENT_POSITION, currentPosition)
    }
}