package de.salomax.ndx.ui.preferences

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.view.View
import de.salomax.ndx.R

class ChangelogDialog : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.fragment_changelog, null)

        return AlertDialog.Builder(context!!)
                .setPositiveButton(R.string.ok, null)
                .setTitle(R.string.prefTitle_changelog)
                .setView(view)
                .create()
    }

}
