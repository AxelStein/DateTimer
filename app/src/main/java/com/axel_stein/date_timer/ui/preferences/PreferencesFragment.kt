package com.axel_stein.date_timer.ui.preferences

import android.content.Intent
import android.media.RingtoneManager.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.axel_stein.date_timer.R
import com.axel_stein.date_timer.data.AppSettings
import com.axel_stein.date_timer.ui.App
import com.axel_stein.date_timer.ui.BillingManager
import com.axel_stein.date_timer.ui.reminder.RingtoneHelper
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class PreferencesFragment : PreferenceFragmentCompat() {
    private lateinit var settings: AppSettings
    private lateinit var ringtoneHelper: RingtoneHelper

    @Inject
    lateinit var billingManager: BillingManager

    init {
        App.appComponent.inject(this)
    }

    @Inject
    fun inject(settings: AppSettings, ringtoneHelper: RingtoneHelper) {
        this.settings = settings
        this.ringtoneHelper = ringtoneHelper
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val theme = preferenceManager.findPreference<ListPreference>("theme")
        theme?.setOnPreferenceChangeListener { _, value ->
            settings.applyTheme(value as String)
            true
        }

        val reminderTrouble = preferenceManager.findPreference<Preference>("reminder_trouble")
        reminderTrouble?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_open_reminder_trouble)
            true
        }

        val ringtone = preferenceManager.findPreference<Preference>("notification_ringtone")
        ringtone?.setOnPreferenceClickListener {
            showRingtonePicker()
            true
        }
        updateRingtoneDescription()

        val disableAds = preferenceManager.findPreference<Preference>("disable_ads")
        disableAds?.setOnPreferenceClickListener {
            billingManager.purchase(requireActivity(), BillingManager.PRODUCT_DISABLE_ADS)
            true
        }
    }

    private fun showRingtonePicker() {
        val existingRingtoneUri = ringtoneHelper.getUri()
        val defaultRingtoneUri = getDefaultUri(TYPE_NOTIFICATION)
        val intent = Intent(ACTION_RINGTONE_PICKER)
        intent.putExtra(EXTRA_RINGTONE_TYPE, TYPE_NOTIFICATION)
        intent.putExtra(EXTRA_RINGTONE_SHOW_DEFAULT, true)
        intent.putExtra(EXTRA_RINGTONE_SHOW_SILENT, true)
        intent.putExtra(EXTRA_RINGTONE_DEFAULT_URI, defaultRingtoneUri)
        intent.putExtra(EXTRA_RINGTONE_EXISTING_URI, existingRingtoneUri)
        startActivityForResult(intent, RINGTONE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RINGTONE_REQUEST_CODE) {
            ringtoneHelper.update(data)
            updateRingtoneDescription()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateRingtoneDescription() {
        val ringtoneName = ringtoneHelper.getName()
        val ringtonePreference = preferenceManager.findPreference<Preference>("notification_ringtone")
        ringtonePreference?.summary = ringtoneName
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    companion object {
        private const val RINGTONE_REQUEST_CODE = 1
    }

    private fun showMessage(msg: String?) {
        if (msg.isNullOrEmpty()) return
        val view = view ?: return
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }

    private fun showMessage(msg: Int) {
        val view = view ?: return
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }
}