package com.bpapps.billingtest1

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.billingclient.api.*
import kotlinx.android.synthetic.main.fragment_purchase.*

private const val TAG = "TAG.PurchaseFragment"

class PurchaseFragment : Fragment(), BillingClientStateListener, View.OnClickListener {

    private val purchaseUpdateListener = PurchasesUpdatedListener { billingResult, mutableList ->
        Log.d(TAG, "${billingResult.responseCode}")
    }

    private var products: MutableList<SkuDetails>? = null
    private var subscriptions: MutableList<SkuDetails>? = null

    private lateinit var billingClient: BillingClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_purchase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        billingClient = BillingClient.newBuilder(requireContext())
            .setListener(purchaseUpdateListener)
            .enablePendingPurchases()
            .build()

        btnLoadProducts.setOnClickListener {
            Log.d(TAG, "btn on click")
            getProducts()
        }

        tvProduct1.setOnClickListener(this)
        tvProduct2.setOnClickListener(this)
        tvProduct3.setOnClickListener(this)
        tvSubscription1.setOnClickListener(this)
        tvSubscription2.setOnClickListener(this)
    }

    private fun getProducts() {
        Log.d(TAG, "getProducts")

        billingClient.startConnection(this)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //BillingClientStateListener
    override fun onBillingServiceDisconnected() {
        Log.d(TAG, "onBillingServiceDisconnected")
        getProducts()
    }

    @SuppressLint("SetTextI18n")
    override fun onBillingSetupFinished(billingResult: BillingResult) {
        Log.d(TAG, "onBillingSetupFinished")
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            val skuList1 = arrayListOf(
                "in_app_product_20",
                "in_app_product_10",
                "in_app_product_30"
            )
            val params1 = SkuDetailsParams.newBuilder()
            params1.setSkusList(skuList1).setType(BillingClient.SkuType.INAPP)
            if (billingClient.isReady) {
                billingClient.querySkuDetailsAsync(params1.build()) { billingResult, skuDetailList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        products = skuDetailList
//                        Log.d(TAG, "Total of ${skuDetailList?.size}")
//                        skuDetailList?.let { it ->
//                            it.forEach { skuDetails: SkuDetails ->
//                                Log.d(TAG, "Title=${skuDetails.title}, Price=${skuDetails.price}")
//                            }
//                        }

                        tvProduct1.text =
                            "${skuDetailList?.get(0)?.title} - ${skuDetailList?.get(0)?.price}"
                        tvProduct2.text =
                            "${skuDetailList?.get(1)?.title} - ${skuDetailList?.get(1)?.price}"
                        tvProduct3.text =
                            "${skuDetailList?.get(2)?.title} - ${skuDetailList?.get(2)?.price}"
                    }
                }
            }

            val skuList2 = arrayListOf(
                "one_month_subscription",
                "three_months_subscription"
            )
            val params2 = SkuDetailsParams.newBuilder()
            params2.setSkusList(skuList2).setType(BillingClient.SkuType.SUBS)
            if (billingClient.isReady) {
                billingClient.querySkuDetailsAsync(params2.build()) { billingResult, skuDetailList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        subscriptions = skuDetailList
//                        Log.d(TAG, "Total of ${skuDetailList?.size}")
//                        skuDetailList?.let { it ->
//                            it.forEach { skuDetails: SkuDetails ->
//                                Log.d(
//                                    TAG,
//                                    "Title=${skuDetails.title}, Price=${skuDetails.price}"
//                                )
//                            }
//                        }

                        tvSubscription1.text =
                            "${skuDetailList?.get(0)?.title} - ${skuDetailList?.get(0)?.price}"
                        tvSubscription2.text =
                            "${skuDetailList?.get(1)?.title} - ${skuDetailList?.get(1)?.price}"
                    }
                }
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onClick(v: View?) {

        if (products != null && subscriptions != null) {
            val skuDetails: SkuDetails? = when (v?.id) {
                R.id.tvProduct1 -> products!![0]
                R.id.tvProduct2 -> products!![1]
                R.id.tvProduct3 -> products!![2]
                R.id.tvSubscription1 -> subscriptions!![0]
                R.id.tvSubscription2 -> subscriptions!![1]
                else -> null
            }

            skuDetails?.let {skuDetails ->
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build()

                val responseCode = billingClient.launchBillingFlow(requireActivity(), flowParams).responseCode
                Log.d(TAG, responseCode.toString())
            }
        } else {
            val dialog: AlertDialog = AlertDialog.Builder(requireContext()).let { builder ->
                builder.setTitle("Error")
                    .setMessage("Press 'LOAD PRODUCTS' first")
                    .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }
                    })
                    .create()
            }

            dialog.show()
        }
    }
}