package com.dreamguys.truelysell.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavoriteListResponse extends BaseResponse {

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

        @SerializedName("userfavorites")
        @Expose
        private List<Userfavorite> userfavorites = null;

        public List<Userfavorite> getUserfavorites() {
            return userfavorites;
        }

        public void setUserfavorites(List<Userfavorite> userfavorites) {
            this.userfavorites = userfavorites;
        }


        public class Userfavorite {

            @SerializedName("id")
            @Expose
            private String id;
            @SerializedName("user_id")
            @Expose
            private String userId;
            @SerializedName("provider_id")
            @Expose
            private String providerId;
            @SerializedName("service_id")
            @Expose
            private String serviceId;
            @SerializedName("status")
            @Expose
            private String status;
            @SerializedName("created_at")
            @Expose
            private String createdAt;
            @SerializedName("service_title")
            @Expose
            private String serviceTitle;
            @SerializedName("service_image")
            @Expose
            private String serviceImage;
            @SerializedName("service_thumb_img")
            @Expose
            private String serviceThumbImg;
            @SerializedName("service_amount")
            @Expose
            private String serviceAmount;
            @SerializedName("rating")
            @Expose
            private String rating;
            @SerializedName("category_name")
            @Expose
            private String categoryName;
            @SerializedName("category_thumb_img")
            @Expose
            private String categoryThumbImg;
            @SerializedName("subcategory_name")
            @Expose
            private String subcategoryName;
            @SerializedName("subcategory_image")
            @Expose
            private Object subcategoryImage;
            @SerializedName("provider_name")
            @Expose
            private String providerName;
            @SerializedName("provider_img")
            @Expose
            private String providerImg;
            @SerializedName("provider_mobile")
            @Expose
            private String providerMobile;
            @SerializedName("provider_country")
            @Expose
            private String providerCountry;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("profile_img")
            @Expose
            private String profileImg;
            @SerializedName("mobileno")
            @Expose
            private String mobileno;
            @SerializedName("country_code")
            @Expose
            private String countryCode;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getProviderId() {
                return providerId;
            }

            public void setProviderId(String providerId) {
                this.providerId = providerId;
            }

            public String getServiceId() {
                return serviceId;
            }

            public void setServiceId(String serviceId) {
                this.serviceId = serviceId;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public String getServiceTitle() {
                return serviceTitle;
            }

            public void setServiceTitle(String serviceTitle) {
                this.serviceTitle = serviceTitle;
            }

            public String getServiceImage() {
                return serviceImage;
            }

            public void setServiceImage(String serviceImage) {
                this.serviceImage = serviceImage;
            }

            public String getServiceThumbImg() {
                return serviceThumbImg;
            }

            public void setServiceThumbImg(String serviceThumbImg) {
                this.serviceThumbImg = serviceThumbImg;
            }

            public String getServiceAmount() {
                return serviceAmount;
            }

            public void setServiceAmount(String serviceAmount) {
                this.serviceAmount = serviceAmount;
            }

            public String getRating() {
                return rating;
            }

            public void setRating(String rating) {
                this.rating = rating;
            }

            public String getCategoryName() {
                return categoryName;
            }

            public void setCategoryName(String categoryName) {
                this.categoryName = categoryName;
            }

            public String getCategoryThumbImg() {
                return categoryThumbImg;
            }

            public void setCategoryThumbImg(String categoryThumbImg) {
                this.categoryThumbImg = categoryThumbImg;
            }

            public String getSubcategoryName() {
                return subcategoryName;
            }

            public void setSubcategoryName(String subcategoryName) {
                this.subcategoryName = subcategoryName;
            }

            public Object getSubcategoryImage() {
                return subcategoryImage;
            }

            public void setSubcategoryImage(Object subcategoryImage) {
                this.subcategoryImage = subcategoryImage;
            }

            public String getProviderName() {
                return providerName;
            }

            public void setProviderName(String providerName) {
                this.providerName = providerName;
            }

            public String getProviderImg() {
                return providerImg;
            }

            public void setProviderImg(String providerImg) {
                this.providerImg = providerImg;
            }

            public String getProviderMobile() {
                return providerMobile;
            }

            public void setProviderMobile(String providerMobile) {
                this.providerMobile = providerMobile;
            }

            public String getProviderCountry() {
                return providerCountry;
            }

            public void setProviderCountry(String providerCountry) {
                this.providerCountry = providerCountry;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getProfileImg() {
                return profileImg;
            }

            public void setProfileImg(String profileImg) {
                this.profileImg = profileImg;
            }

            public String getMobileno() {
                return mobileno;
            }

            public void setMobileno(String mobileno) {
                this.mobileno = mobileno;
            }

            public String getCountryCode() {
                return countryCode;
            }

            public void setCountryCode(String countryCode) {
                this.countryCode = countryCode;
            }

        }


    }

}