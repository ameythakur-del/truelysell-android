package com.dreamguys.truelysell.payment;

import static butterknife.OnTextChanged.Callback.BEFORE_TEXT_CHANGED;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dreamguys.truelysell.ChangePasswordActivity;
import com.dreamguys.truelysell.MyProfileActivity;
import com.dreamguys.truelysell.R;
import com.dreamguys.truelysell.SplashScreenActivityNew;
import com.dreamguys.truelysell.SubscriptionThankYouActivity;
import com.dreamguys.truelysell.datamodel.BaseResponse;
import com.dreamguys.truelysell.datamodel.DAOPayStackKeys;
import com.dreamguys.truelysell.datamodel.POSTCreategigs;
import com.dreamguys.truelysell.datamodel.POSTCurrencyConversion;
import com.dreamguys.truelysell.network.ApiClient;
import com.dreamguys.truelysell.network.ApiInterface;
import com.dreamguys.truelysell.utils.AppConstants;
import com.dreamguys.truelysell.utils.AppUtils;
import com.dreamguys.truelysell.utils.PreferenceStorage;
import com.dreamguys.truelysell.utils.ProgressDlg;
import com.dreamguys.truelysell.utils.RetrofitHandler;
import com.dreamguys.truelysell.utils.Utils;
import com.dreamguys.truelysell.wallet.WalletDashBoard;
import com.google.gson.Gson;


import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayStackActivity extends AppCompatActivity {

    Charge charge;
    Card card;
    String currency = "", amount = "", orderID = "", txnID = "", referenceID = "", from = "";
    Button btnPaynow;
    @BindView(R.id.cet_enter_card_number)
    EditText cetEnterCardNumber;
    @BindView(R.id.cet_expiry_date)
    EditText cetExpiryDate;
    @BindView(R.id.cet_cvv)
    EditText cetCvv;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tvPayableAmount)
    TextView tvPayableAmount;
    @BindView(R.id.tv_name)
    TextView tv_name;
    Double newcurrency = 0.0;
    String origcurrencycode = "", origamount = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitypaystack);
        ButterKnife.bind(this);
        btnPaynow = findViewById(R.id.btnPaynow);
        amount = getIntent().getStringExtra(AppConstants.CASH_AMOUNT);
        origamount = getIntent().getStringExtra(AppConstants.ORIGINAL_AMOUNT);
        orderID = getIntent().getStringExtra(AppConstants.ORDERID);
        from = getIntent().getStringExtra(AppConstants.FROM);
        origcurrencycode = getIntent().getStringExtra(AppConstants.CURRENCYCODE);
//        referenceID = getIntent().getStringExtra(AppConstants.REFID);
//        currencyconversion();

        tvPrice.setText(amount + " " + "NGN");


//        getPublicKey();
        PaystackSdk.initialize(getApplicationContext());
        PaystackSdk.setPublicKey("pk_test_eb3a128d1fcabd55b1fdb61fb479cd586c26c73b");
        currency = "NGN";

        btnPaynow.setText("Pay Now");
        tvPayableAmount.setText("Payable amount");
        tv_name.setText("Amount");
//        tvPrice.setText(String.valueOf(amount));



//                btnPaynow.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        String usrmmaail = PreferenceStorage.getKey(AppConstants.USER_EMAIL);
//                        Toast.makeText(PayStackActivity.this, usrmmaail, Toast.LENGTH_SHORT).show();
//
//                    }
//                });

        btnPaynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cetEnterCardNumber.getText().toString().isEmpty()) {
                    Toast.makeText(PayStackActivity.this, "Enter Card Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cetExpiryDate.getText().toString().isEmpty()) {
                    Toast.makeText(PayStackActivity.this, "Enter Card Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cetCvv.getText().toString().isEmpty()) {
                    Toast.makeText(PayStackActivity.this, "Enter Card Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] spiltExpiryDateStr = cetExpiryDate.getText().toString().split("/");
                int month = Integer.parseInt(spiltExpiryDateStr[0]);
                int year = Integer.parseInt(spiltExpiryDateStr[1]);

                card = new Card(cetEnterCardNumber.getText().toString().trim(), month, year, cetCvv.getText().toString().trim());
                if (card.isValid()) {
                    try {
                        performCharge();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(PayStackActivity.this, "Enter Valid card details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cetEnterCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                cardNumberPattern(editable);
            }
        });
    }

    private void currencyconversion() {

            if (AppUtils.isNetworkAvailable(this)) {

                HashMap<String, String> data = new HashMap<>();
                data.put("amount", amount);
                data.put("conversion_currency", "NGN");
                data.put("user_currency_code", PreferenceStorage.getKey(AppConstants.CURRENCYCODE));
                data.put("paytype", "paystack");

                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                apiInterface.postCurrencyconversion(data, PreferenceStorage.getKey(AppConstants.USER_TOKEN)).enqueue(new Callback<POSTCurrencyConversion>() {
                    @Override
                    public void onResponse(Call<POSTCurrencyConversion> call, Response<POSTCurrencyConversion> response) {
                        ProgressDlg.dismissProgressDialog();
                        if (response.body().getResponse().getResponseCode().equals(200)) {
                            Utils.toastMessage(PayStackActivity.this, response.body().getResponse().getResponseMessage());


                            newcurrency = response.body().getData().getCurrencyAmount();

                            updateUI();

                        } else {
                            Toast.makeText(PayStackActivity.this, response.body().getResponse().getResponseMessage(), Toast.LENGTH_LONG).show();
//                        Utils.toastMessage(PayStackActivity.this, response.body().getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<POSTCurrencyConversion> call, Throwable t) {
                        Log.i("TAG", t.getMessage());
                        ProgressDlg.dismissProgressDialog();
                        finish();
                        Toast.makeText(PayStackActivity.this, "Paystack not working}", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                ProgressDlg.dismissProgressDialog();
                Toast.makeText(PayStackActivity.this, "Enable Internet", Toast.LENGTH_LONG).show();
            }

    }

    private static final char space = ' ';

    void cardNumberPattern(Editable s) {
        int pos = 0;
        while (true) {
            if (pos >= s.length()) break;
            if (space == s.charAt(pos) && (((pos + 1) % 5) != 0 || pos + 1 == s.length())) {
                s.delete(pos, pos + 1);
            } else {
                pos++;
            }
        }

        // Insert char where needed.
        pos = 4;
        while (true) {
            if (pos >= s.length()) break;
            final char c = s.charAt(pos);
            // Only if its a digit where there should be a space we insert a space
            if ("0123456789".indexOf(c) >= 0) {
                s.insert(pos, "" + space);
            }
            pos += 5;
        }
    }

    int previousLength;

    @OnTextChanged(value = R.id.cet_expiry_date, callback = BEFORE_TEXT_CHANGED)
    void beforeExpireEtChanged() {
        previousLength = cetExpiryDate.getText().toString().length();
    }

    @OnTextChanged(R.id.cet_expiry_date)
    void autoFixAndMoveToNext() {
        int length = cetExpiryDate.getText().toString().trim().length();

        if (previousLength <= length && length < 3) {
            int month = Integer.parseInt(cetExpiryDate.getText().toString());
            if (length == 1 && month >= 2) {
                String autoFixStr = "0" + month + "/";
                cetExpiryDate.setText(autoFixStr);
                cetExpiryDate.setSelection(3);
            } else if (length == 2 && month <= 12) {
                String autoFixStr = cetExpiryDate.getText().toString() + "/";
                cetExpiryDate.setText(autoFixStr);
                cetExpiryDate.setSelection(3);
            } else if (length == 2 && month > 12) {
                cetExpiryDate.setText("1");
                cetExpiryDate.setSelection(1);
            }
        } else if (length == 5) {
            cetCvv.requestFocus(); // auto move to next edittext
        }
    }


    private void performCharge() {
        //create a Charge object
        charge = new Charge();
        //set the card to charge
        charge.setCard(card);
        //call this method if you set a plan
        //charge.setPlan("PLN_yourplan");


        charge.setEmail(PreferenceStorage.getKey(AppConstants.USER_EMAIL)); //dummy email address
        charge.setCurrency(currency);
        int amtint = (int) Math.round(Float.parseFloat(amount) * 100);
        int amtint2 =  amtint * 100;
        charge.setAmount(amtint); //test amount
        charge.setReference(PreferenceStorage.getKey(AppConstants.PAYSTACKREF));
        ProgressDlg.showProgressDialog(this, null, null);
        PaystackSdk.chargeCard(PayStackActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                ProgressDlg.dismissProgressDialog();
                Log.i("onSuccess", transaction.getReference());
                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.
                txnID = transaction.getReference();
//                paymentSuccesspaystack();
                postTopupWallet(txnID, "paystack");
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                Log.i("beforeValidate", transaction.getReference());
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
                ProgressDlg.dismissProgressDialog();
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                //handle error here
                Log.i("onError", error.getMessage());
                ProgressDlg.dismissProgressDialog();
            }
        });
    }


    private void updateUI() {

        tvPrice.setText(String.valueOf(newcurrency));
    }

    private void getPublicKey() {
        if (AppUtils.isNetworkAvailable(this)) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            apiInterface.getPublicKey().enqueue(new Callback<DAOPayStackKeys>() {
                @Override
                public void onResponse(Call<DAOPayStackKeys> call, Response<DAOPayStackKeys> response) {
                    if (response.body().getCode().equals(200)) {
                        PaystackSdk.initialize(getApplicationContext());
                        PaystackSdk.setPublicKey(response.body().getData().getKey());
                        currency = response.body().getData().getCurrency();
                    } else {
                        Utils.toastMessage(PayStackActivity.this, response.body().getMessage());
                    }
                }

                @Override
                public void onFailure(Call<DAOPayStackKeys> call, Throwable t) {
                    Log.i("TAG", t.getMessage());
                }
            });
        } else {
            Utils.toastMessage(PayStackActivity.this, "No Internet");
        }

    }


    private void paymentSuccesspaystack() {
        Utils.toastMessage(PayStackActivity.this, "Payment Success");

    }




    private void postTopupWallet(String tokenid, String type) {
        if (AppUtils.isNetworkAvailable(this)) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            apiInterface.postTopupWallet(PreferenceStorage.getKey(AppConstants.USER_TOKEN),
                    tokenid, origamount, origcurrencycode, type).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    ProgressDlg.dismissProgressDialog();
                    if (response.body().getResponseHeader().getResponseCode().equals("200")) {
                        Utils.toastMessage(PayStackActivity.this, response.message());
                        startActivity(new Intent(PayStackActivity.this, WalletDashBoard.class));

                       finish();



                    } else {
                        Toast.makeText(PayStackActivity.this, response.message(), Toast.LENGTH_LONG).show();
//                        Utils.toastMessage(PayStackActivity.this, response.body().getMessage());
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    Log.i("TAG", t.getMessage());
                    ProgressDlg.dismissProgressDialog();
                }
            });
        } else {
            ProgressDlg.dismissProgressDialog();
            Toast.makeText(PayStackActivity.this, "Enable Internet", Toast.LENGTH_LONG).show();
        }
    }

}
