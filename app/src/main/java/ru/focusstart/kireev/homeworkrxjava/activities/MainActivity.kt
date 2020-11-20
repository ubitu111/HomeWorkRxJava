package ru.focusstart.kireev.homeworkrxjava.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.focusstart.kireev.homeworkrxjava.fragments.MainFragment
import ru.focusstart.kireev.homeworkrxjava.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            addFragment()
        }
    }

    private fun addFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.main_container, MainFragment.newInstance())
        transaction.commit()
    }
}