package de.salomax.ndx.ui.timer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.joaquimverges.helium.retained.RetainedPresenters
import de.salomax.ndx.R

class TimerActivity : AppCompatActivity() {

    private lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = RetainedPresenters.get(this, Presenter::class.java)
        val viewDelegate = ViewDelegate(layoutInflater)
        presenter.attach(viewDelegate)
        setContentView(viewDelegate.view)

        // title bar
        setTitle(R.string.title_timer)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        presenter.pushState(State.Finish)
        return true
    }

}
