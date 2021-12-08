package com.dreamguys.truelysell.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionPaymentResponse extends BaseResponse {

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

        @SerializedName("transaction_id")
        @Expose
        private String transactionId;
        @SerializedName("payment_details")
        @Expose
        private String paymentDetails;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getPaymentDetails() {
            return paymentDetails;
        }

        public void setPaymentDetails(String paymentDetails) {
            this.paymentDetails = paymentDetails;
        }

    }

}