package de.salomax.ndx.ui.filtereditor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import de.salomax.ndx.R
import de.salomax.ndx.data.Filter
import de.salomax.ndx.ui.BaseActivity
import de.salomax.ndx.ui.calibrator.CalibratorActivity
import de.salomax.ndx.util.MathUtils
import kotlinx.android.synthetic.main.activity_filtereditor.*
import java.util.*
import kotlin.math.pow

class FilterEditorActivity : BaseActivity() {

    companion object {
        const val ARG_FILTER = "ARG_FILTER"
    }

    private lateinit var viewModel: FilterEditorViewModel

    private var oldId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init view
        setContentView(R.layout.activity_filtereditor)
        viewModel = ViewModelProvider(this).get(FilterEditorViewModel::class.java)

        // edit existing filter
        val filter = intent.getParcelableExtra<Filter>(ARG_FILTER)
        if (filter != null) {
            // edit mode: show delete button
            if (filter.id != null) {
                oldId = filter.id
                btn_delete.show()
                btn_delete.setOnClickListener {
                    viewModel.delete(filter)
                    val returnIntent = Intent()
                    val deletedFilter = Filter(oldId!!, factor.text.toString().toInt(), name.text.toString(), info.text.toString())
                    returnIntent.putExtra("FILTER", deletedFilter)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
            }
            // populate editText fields
            name.setText(filter.name)
            info.setText(filter.info)
            init(filter.factor)
        }

        //
        f_stops.calc()
        factor.calc()
        factor.onlyGreaterEqualOne()

        btn_calibrator.setOnClickListener {
                val intent = Intent(this, CalibratorActivity().javaClass)
                startActivityForResult(intent, 1)
        }

        // title bar
        setTitle(if (filter != null) R.string.title_editFilter else R.string.title_addFilter)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.filtereditor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.save -> {
                // validate
                if (name.validate({ s -> s.isNotBlank() }, getString(R.string.error_nameRequired))
                        && factor.validate({ s -> s.isNotBlank() }, getString(R.string.error_factorRequired))) {
                    // save
                    val filter = Filter(
                            oldId,
                            factor.text.toString().toInt(),
                            name.text.toString(),
                            info.text.toString())
                    viewModel.insert(filter)
                    finish()
                }
                true
            }
            else -> false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK
                && data != null && data.hasExtra("FACTOR")) {
            init(data.getIntExtra("FACTOR", 1))
        }
    }

    /**
     *
     */
    private fun init(factor: Int) {
        this.factor.setText(factor.toString())
        // calc fStops & nd
        f_stops.setText(MathUtils.factor2fstop(factor.toDouble()))
        nd.setText(MathUtils.factor2nd(factor))
    }

    /**
     * Auto-fill properties
     */
    private fun EditText.calc() {
        this.afterTextChanged { s ->
            // cast text to double
            val input = if (s.toDoubleOrNull() != null)
                s.toDouble()
            else when {
                this == factor -> 1.0
                this == f_stops -> 0.0
                else -> 0.0
            }

            // set listener
            if (this.isFocused)
                when (this) {
                    factor -> {
                        f_stops.setText(MathUtils.factor2fstop(input))
                        nd.setText(MathUtils.factor2nd(input))
                    }
                    f_stops -> {
                        factor.setText(2.0.pow(input).toInt().toString())
                        nd.setText(String.format(Locale.US, "%.1f", kotlin.math.log10(2.0.pow(input))))
                    }
                    // don't let users enter the ND value: very inaccurate!
                    // factor.setText(kotlin.math.round(Math.pow(10.0, input)).toInt().toString())
                    // fStops.setText(kotlin.math.round(kotlin.math.log2(Math.pow(10.0, input))).toInt().toString())
                }
        }
    }

    /**
     * Validator
     */
    private fun EditText.validate(validator: (String) -> Boolean, message: String): Boolean {
        return if (validator(this.text.toString())) {
            this.error = null
            true
        } else {
            this.error = message
            false
        }
    }

    /**
     * TextChangeListener
     */
    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                afterTextChanged.invoke(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun EditText.onlyGreaterEqualOne() {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    val i = Integer.parseInt(s.toString())
                    if (i < 1) s?.clear()
                } catch (ignored: NumberFormatException) {}
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

}
