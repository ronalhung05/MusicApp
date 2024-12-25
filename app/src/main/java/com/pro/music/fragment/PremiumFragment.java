package com.pro.music.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.pro.music.MyApplication;
import com.pro.music.R;
import com.pro.music.activity.MainActivity;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.constant.PayPalConfig;
import com.pro.music.databinding.FragmentPremiumBinding;
import com.pro.music.model.Premium;
import com.pro.music.model.User;
import com.pro.music.prefs.DataStoreManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class PremiumFragment extends Fragment {

    public static final int PAYPAL_REQUEST_CODE = 199;
    public static final String PAYPAL_PAYMENT_STATUS_APPROVED = "approved";
    public static final PayPalConfiguration PAYPAL_CONFIG = new PayPalConfiguration()
            .environment(PayPalConfig.PAYPAL_ENVIRONMENT_DEV)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID_DEV)
            .acceptCreditCards(false);

    private FragmentPremiumBinding mFragmentPremiumBinding;

    public static PremiumFragment newInstance(boolean isFromMenuLeft) {
        PremiumFragment fragment = new PremiumFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mFragmentPremiumBinding = FragmentPremiumBinding.inflate(inflater, container, false);

        // Start PayPal Service
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PAYPAL_CONFIG);
        getActivity().startService(intent);

        mFragmentPremiumBinding.btnUpgradePremium.setOnClickListener(v -> {
            mFragmentPremiumBinding.btnUpgradePremium.setEnabled(false);
            onClickUpgradePremium();
        });

        return mFragmentPremiumBinding.getRoot();
    }

    private void onClickUpgradePremium() {
        getPaymentPaypal(10);
    }

    private void getPaymentPaypal(int price) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(price)),
                PayPalConfig.PAYPAL_CURRENCY, PayPalConfig.PAYPAl_CONTENT_TEXT,
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PAYPAL_CONFIG);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragmentPremiumBinding.btnUpgradePremium.setEnabled(true);

        if (requestCode == PAYPAL_REQUEST_CODE) {
            boolean isPaymentSuccess = false;

            if (resultCode == Activity.RESULT_OK && data != null) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.e("Payment Result", paymentDetails);

                        JSONObject jsonDetails = new JSONObject(paymentDetails);
                        JSONObject jsonResponse = jsonDetails.getJSONObject("response");
                        String strState = jsonResponse.getString("state");
                        Log.e("Payment State", strState);

                        if (PAYPAL_PAYMENT_STATUS_APPROVED.equals(strState)) {
                            isPaymentSuccess = true;
                        }
                    } catch (JSONException e) {
                        Log.e("Payment Error", "JSON Parsing error: " + e.getMessage());
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), getString(R.string.msg_payment_canceled), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), getString(R.string.msg_payment_error), Toast.LENGTH_SHORT).show();
            }

            if (isPaymentSuccess) {
                sendRequestUpgrade();
            }
        }
    }
    private void sendRequestUpgrade() {
        if (getActivity() == null) {
            return;
        }
        MainActivity activity = (MainActivity) getActivity();

        activity.showProgressDialog(true);
        Premium premium = new Premium(DataStoreManager.getUser().getEmail(), true);
        MyApplication.get(getActivity()).getPremiumDatabaseReference()
                .child(String.valueOf(System.currentTimeMillis()))
                .setValue(premium, (databaseError, databaseReference) -> {
                    activity.showProgressDialog(false);
                    User user = DataStoreManager.getUser();
                    user.setPremium(true);
                    DataStoreManager.setUser(user);
                    sendUpgradeSuccess();
                });
    }

    private void sendUpgradeSuccess() {
        GlobalFunction.showToastMessage(getActivity(), getString(R.string.msg_upgrade_premium_success));
        if (getActivity() != null) {
            getActivity().finish();
            getActivity().startActivity(getActivity().getIntent());
        }
    }

    @Override
    public void onDestroy() {
        if (getActivity() != null) {
            getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        }
        super.onDestroy();
    }
}
