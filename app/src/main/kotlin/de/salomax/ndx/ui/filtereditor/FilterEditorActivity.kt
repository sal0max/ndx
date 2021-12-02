package de.salomax.ndx.ui.filtereditor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import de.salomax.ndx.R
import de.salomax.ndx.data.Filter
import de.salomax.ndx.databinding.ActivityFiltereditorBinding
import de.salomax.ndx.ui.BaseActivity
import de.salomax.ndx.ui.calibrator.CalibratorActivity
import de.salomax.ndx.util.MathUtils
import java.util.*
import kotlin.math.pow

class FilterEditorActivity : BaseActivity() {

    companion object {
        const val ARG_FILTER = "ARG_FILTER"
    }

    private lateinit var binding: ActivityFiltereditorBinding
    private lateinit var viewModel: FilterEditorViewModel

    private var oldId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init view
        binding = ActivityFiltereditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(FilterEditorViewModel::class.java)

        // don't allow > Int.MAX_VALUE
        binding.factor.filters = arrayOf<InputFilter>(MinMaxInputFilter(0, Int.MAX_VALUE))
        binding.size.filters = arrayOf<InputFilter>(MinMaxInputFilter(0, Int.MAX_VALUE))

        // edit existing filter
        val filter = intent.getParcelableExtra<Filter>(ARG_FILTER)
        if (filter != null) {
            // edit mode: show delete button
            if (filter.id != null) {
                oldId = filter.id
                binding.btnDelete.show()
                binding.btnDelete.setOnClickListener {
                    viewModel.delete(filter)
                    val returnIntent = Intent()
                    val deletedFilter = Filter(oldId!!, binding.factor.text.toString().toInt(), binding.name.text.toString(), binding.size.toString().toInt(), binding.info.text.toString())
                    returnIntent.putExtra("FILTER", deletedFilter)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
            }
            // populate editText fields
            binding.name.setText(filter.name)
            binding.size.setText(filter.size?.toString())
            binding.info.setText(filter.info)
            init(filter.factor)
        }

        //
        binding.fStops.calc()
        binding.factor.calc()
        binding.factor.onlyGreaterEqualOne()

        binding.btnCalibrator.setOnClickListener {
            val intent = Intent(this, CalibratorActivity().javaClass)
            calibratorActivityWithResult.launch(intent)
        }

        // title bar
        setTitle(if (filter != null) R.string.title_editFilter else R.string.title_addFilter)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        }
    }

    private val calibratorActivityWithResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data: Intent? = result.data
        if (result.resultCode == Activity.RESULT_OK && data != null && data.hasExtra("FACTOR")) {
            init(data.getIntExtra("FACTOR", 1))
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                // validate
                if (binding.name.validate({ s -> s.isNotBlank() }, getString(R.string.error_nameRequired))
                        && binding.factor.validate({ s -> s.isNotBlank() }, getString(R.string.error_factorRequired))) {
                    // save
                    val filter = Filter(
                            oldId,
                            binding.factor.text.toString().toInt(),
                            binding.name.text.toString(),
                            binding.size.text?.let { if (it.isEmpty()) null else it.toString().toInt() },
                            binding.info.text.toString())
                    viewModel.insert(filter)
                    finish()
                }
                true
            }
            else -> false
        }
    }

    /**
     *
     */
    private fun init(factor: Int) {
        binding.factor.setText(factor.toString())
        // calc fStops & nd
        binding.fStops.setText(MathUtils.factor2fstop(factor.toDouble()))
        binding.nd.setText(MathUtils.factor2nd(factor))
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
                this == binding.factor -> 1.0
                this == binding.fStops -> 0.0
                else -> 0.0
            }

            // set listener
            if (this.isFocused)
                when (this) {
                    binding.factor -> {
                        binding.fStops.setText(MathUtils.factor2fstop(input))
                        binding.nd.setText(MathUtils.factor2nd(input))
                    }
                    binding.fStops -> {
                        binding.factor.setText(2.0.pow(input).toInt().toString())
                        binding.nd.setText(String.format(Locale.US, "%.1f", kotlin.math.log10(2.0.pow(input))))
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

    private class MinMaxInputFilter(private var min: Int, private var max: Int) : InputFilter {

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
            try {
                // calculate the new input, the user wants
                val input = Integer.parseInt(
                      dest.subSequence(0, dstart).toString() +
                            source.subSequence(start, end).toString() +
                            dest.subSequence(dend, dest.length)
                )
                if (isInRange(min, max, input))
                    // accept the new input
                    return null
            } catch (ignored: java.lang.NumberFormatException) {}
            // append "nothing" instead of the input
            return ""
        }

        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a)
                c in a..b
            else
                c in b..a
        }
    }

}
