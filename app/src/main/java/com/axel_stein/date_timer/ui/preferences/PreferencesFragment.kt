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
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.axel_stein.date_timer.R
import com.axel_stein.date_timer.data.AppSettings
import com.axel_stein.date_timer.ui.App
import com.axel_stein.date_timer.ui.reminder.RingtoneHelper
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class PreferencesFragment : PreferenceFragmentCompat() {
    private lateinit var settings: AppSettings
    private lateinit var ringtoneHelper: RingtoneHelper
    private var billingClient: BillingClient? = null
    private var billingConnection = false

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

        val purchasesCategory = preferenceManager.findPreference<Preference>("purchases_category")
        purchasesCategory?.isVisible = settings.isAdsEnabled()

        val disableAds = preferenceManager.findPreference<Preference>("disable_ads")
        disableAds?.setOnPreferenceClickListener {
            disableAds()
            true
        }

        val restorePurchases = preferenceManager.findPreference<Preference>("restore_purchases")
        restorePurchases?.setOnPreferenceClickListener {
            restorePurchase()
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

    private fun disableAds() {
        getBillingClient { client ->
            val products = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId("no_ads")
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )
            client.queryProductDetailsAsync(
                QueryProductDetailsParams.newBuilder()
                    .setProductList(products)
                    .build()
            ) { billingResult, list ->
                if (list.isNotEmpty()) {
                    val productDetailsParams = listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(list.first())
                            .build()
                    )
                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParams)
                        .build()
                    client.launchBillingFlow(requireActivity(), billingFlowParams)
                }
            }
        }
    }

    private fun restorePurchase() {
        getBillingClient { client ->
            client.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            ) { billingResult, list ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (list.isEmpty()) {
                        showMessage(R.string.no_purchases_found)
                    } else {
                        hideAds()
                    }
                } else {
                    showMessage(billingResult.debugMessage)
                }
            }
        }
    }

    private fun getBillingClient(callback: (BillingClient) -> Unit) {
        if (billingClient != null) {
            callback(billingClient!!)
            return
        }
        if (billingConnection) {
            return
        }
        billingConnection = true

        val client = BillingClient.newBuilder(requireContext())
            .enablePendingPurchases(
                PendingPurchasesParams
                    .newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (purchases != null && !purchases.isEmpty()) {
                        hideAds()
                    }
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user canceling the purchase flow.
                } else {
                    showMessage(billingResult.debugMessage)
                }
            }
            .build()
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                billingConnection = false
                billingClient = null
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                billingConnection = false

                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    billingClient = client
                    callback(client)
                } else {
                    showMessage(billingResult.debugMessage)
                }
            }

        })
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

    private fun hideAds() {
        settings.setAdsEnabled(false)
        showMessage(R.string.ads_disabled)
        activity?.recreate()
    }
}