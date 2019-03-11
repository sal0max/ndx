package de.salomax.ndx.ui.filtereditor

import android.app.Activity
import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import com.joaquimverges.helium.core.viewdelegate.BaseViewDelegate
import de.salomax.ndx.R
import de.salomax.ndx.data.Filter
import de.salomax.ndx.ui.calibrator.CalibratorActivity
import de.salomax.ndx.util.MathUtils
import java.util.*

class ViewDelegate(inflater: LayoutInflater) : BaseViewDelegate<State, Event>(R.layout.activity_filtereditor, inflater) {

    private val name = view.findViewById<EditText>(R.id.name)
    private val info = view.findViewById<EditText>(R.id.info)
    private val factor = view.findViewById<EditText>(R.id.factor)
    private val fStops = view.findViewById<EditText>(R.id.f_stops)
    private val nd = view.findViewById<EditText>(R.id.nd)

    private val calibrator = view.findViewById<Button>(R.id.btn_calibrator)
    private val delete = view.findViewById<FloatingActionButton>(R.id.btn_delete)

    private var oldId: Long? = null

    init {
        factor.calc()
        fStops.calc()
        factor.onlyGreaterEqualOne()

        calibrator.setOnClickListener {
            val intent = Intent(context, CalibratorActivity().javaClass)
            (context as AppCompatActivity).startActivityForResult(intent, 1)
        }
    }

    override fun render(viewState: State) {
        when (viewState) {
            is State.Prepopulate -> {
                // edit mode: show delete button
                if (viewState.data.id != null) {
                    oldId = viewState.data.id
                    delete.show()
                    delete.setOnClickListener {
                        pushEvent(Event.Delete(viewState.data.id!!))
                    }
                }
                // populate editText fields
                name.setText(viewState.data.name)
                info.setText(viewState.data.info)
                init(viewState.data.factor)
            }
            is State.InsertOrUpdate -> {
                // validate
                if (name.validate({ s -> s.isNotBlank() }, context.getString(R.string.error_nameRequired))
                        && factor.validate({ s -> s.isNotBlank() }, context.getString(R.string.error_factorRequired))) {
                    // save
                    val filter = Filter(
                            oldId,
                            factor.text.toString().toInt(),
                            name.text.toString(),
                            info.text.toString())
                    pushEvent(Event.InsertOrUpdate(filter))
                }
            }
            is State.Finish -> {
                (context as AppCompatActivity).finish()
            }
            is State.DeleteAndFinish -> {
                val returnIntent = Intent()
                val deletedFilter = Filter(
                        oldId!!,
                        factor.text.toString().toInt(),
                        name.text.toString(),
                        info.text.toString())
                returnIntent.putExtra("FILTER", deletedFilter)
                val activity = context as AppCompatActivity
                activity.setResult(Activity.RESULT_OK, returnIntent)
                activity.finish()
            }
            is State.Calibrated -> {
                init(viewState.factor)
            }
        }
    }

    private fun init(factor: Int) {
        this.factor.setText(factor.toString())
        // calc fStops & nd
        this.fStops.setText(MathUtils.factor2fstop(factor.toDouble()))
        this.nd.setText(MathUtils.factor2nd(factor))
    }

    // Auto-fill properties
    private fun EditText.calc() {
        this.afterTextChanged { s ->
            // cast text to double
            val input = if (s.toDoubleOrNull() != null)
                s.toDouble()
            else when {
                this == factor -> 1.0
                this == fStops -> 0.0
                else -> 0.0
            }

            // set listener
            if (this.isFocused)
                when (this) {
                    factor -> {
                        fStops.setText(MathUtils.factor2fstop(input))
                        nd.setText(MathUtils.factor2nd(input))
                    }
                    fStops -> {
                        factor.setText(Math.pow(2.0, input).toInt().toString())
                        nd.setText(String.format(Locale.US, "%.1f", kotlin.math.log10(Math.pow(2.0, input))))
                    }
                // don't let users enter the ND value: very inaccurate!
                // factor.setText(kotlin.math.round(Math.pow(10.0, input)).toInt().toString())
                // fStops.setText(kotlin.math.round(kotlin.math.log2(Math.pow(10.0, input))).toInt().toString())
                }
        }
    }

    // Validator
    private fun EditText.validate(validator: (String) -> Boolean, message: String): Boolean {
        return if (validator(this.text.toString())) {
            this.error = null
            true
        } else {
            this.error = message
            false
        }
    }

    // TextChangeListener
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
