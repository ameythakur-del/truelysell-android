package com.dreamguys.truelysell.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hari on 13-12-2018.
 */

public class StripeDetailsModel extends BaseResponse {

    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("publishable_key")
        @Expose
        private String publishableKey;
        @SerializedName("secret_key")
        @Expose
        private String secretKey;
        @SerializedName("razorpay_apikey")
        @Expose
        private String razorpayApikey;
        @SerializedName("razorpay_secret_key")
        @Expose
        private String razorpaySecretKey;
        @SerializedName("paystack_apikey")
        @Expose
        private String paystackApikey;
        @SerializedName("paystack_secret_key")
        @Expose
        private String paystackSecretKey;
        @SerializedName("paypal_option")
        @Expose
        private String paypalOption;
        @SerializedName("stripe_option")
        @Expose
        private String stripeOption;
        @SerializedName("razor_option")
        @Expose
        private String razorOption;
        @SerializedName("paystack_option")
        @Expose
        private String paystackOption;
        @SerializedName("paystack_ref_key")
        @Expose
        private Integer paystackRefKey;
        @SerializedName("braintree_key")
        @Expose
        private String braintreeKey;
        @SerializedName("email")
        @Expose
        private String email;

        public String getPublishableKey() {
            return publishableKey;
        }

        public void setPublishableKey(String publishableKey) {
            this.publishableKey = publishableKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getRazorpayApikey() {
            return razorpayApikey;
        }

        public void setRazorpayApikey(String razorpayApikey) {
            this.razorpayApikey = razorpayApikey;
        }

        public String getRazorpaySecretKey() {
            return razorpaySecretKey;
        }

        public void setRazorpaySecretKey(String razorpaySecretKey) {
            this.razorpaySecretKey = razorpaySecretKey;
        }

        public String getPaystackApikey() {
            return paystackApikey;
        }

        public void setPaystackApikey(String paystackApikey) {
            this.paystackApikey = paystackApikey;
        }

        public String getPaystackSecretKey() {
            return paystackSecretKey;
        }

        public void setPaystackSecretKey(String paystackSecretKey) {
            this.paystackSecretKey = paystackSecretKey;
        }

        public String getPaypalOption() {
            return paypalOption;
        }

        public void setPaypalOption(String paypalOption) {
            this.paypalOption = paypalOption;
        }

        public String getStripeOption() {
            return stripeOption;
        }

        public void setStripeOption(String stripeOption) {
            this.stripeOption = stripeOption;
        }

        public String getRazorOption() {
            return razorOption;
        }

        public void setRazorOption(String razorOption) {
            this.razorOption = razorOption;
        }

        public String getPaystackOption() {
            return paystackOption;
        }

        public void setPaystackOption(String paystackOption) {
            this.paystackOption = paystackOption;
        }

        public Integer getPaystackRefKey() {
            return paystackRefKey;
        }

        public void setPaystackRefKey(Integer paystackRefKey) {
            this.paystackRefKey = paystackRefKey;
        }

        public String getBraintreeKey() {
            return braintreeKey;
        }

        public void setBraintreeKey(String braintreeKey) {
            this.braintreeKey = braintreeKey;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    }
}
