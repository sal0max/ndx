package de.salomax.ndx.ui.timer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.joaquimverges.helium.retained.RetainedPresenters
import de.salomax.ndx.R

class TimerActivity : AppCompatActivity() {

    private lateinit var presenter: Presenter
    private lateinit var viewDelegate: ViewDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = RetainedPresenters.get(this, Presenter::class.java)
        viewDelegate = ViewDelegate(layoutInflater)
        presenter.attach(viewDelegate)
        setContentView(viewDelegate.view)

        // title bar
        setTitle(R.string.title_timer)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        intent.extras?.let {
            viewDelegate.pushEvent(Event.PopulateTimer(it.getLong("MILLIS"), 0))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        viewDelegate.pushEvent(Event.Finish)
        return true
    }

}
