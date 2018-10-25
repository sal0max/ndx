package de.salomax.ndx.ui.timer

import android.os.Bundle
import com.joaquimverges.helium.core.retained.RetainedPresenters
import de.salomax.ndx.R
import de.salomax.ndx.ui.BaseActivity

class TimerActivity : BaseActivity() {

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
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        }

        intent.extras?.let {
            viewDelegate.pushEvent(Event.PopulateTimer(it.getLong("MILLIS"), 0))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        viewDelegate.pushEvent(Event.Finish)
        super.onBackPressed()
    }

}
