package com.dreamguys.truelysell.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckBlockResponse extends BaseResponse {

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

        @SerializedName("userblock")
        @Expose
        private Boolean userblock;

        public Boolean getUserblock() {
            return userblock;
        }

        public void setUserblock(Boolean userblock) {
            this.userblock = userblock;
        }

    }
}