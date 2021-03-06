package com.dreamguys.truelysell;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dreamguys.truelysell.adapters.ChatDetailAdapterNew;
import com.dreamguys.truelysell.datamodel.CommonLangModel;
import com.dreamguys.truelysell.datamodel.DateItem;
import com.dreamguys.truelysell.datamodel.GeneralItem;
import com.dreamguys.truelysell.datamodel.LanguageModel;
import com.dreamguys.truelysell.datamodel.LanguageResponse;
import com.dreamguys.truelysell.datamodel.ListItem;
import com.dreamguys.truelysell.datamodel.Phase3.DAOChatDetails;
import com.dreamguys.truelysell.datamodel.Phase3.DAOChatSentResponse;
import com.dreamguys.truelysell.network.ApiClient;
import com.dreamguys.truelysell.network.ApiInterface;
import com.dreamguys.truelysell.utils.AppConstants;
import com.dreamguys.truelysell.utils.AppUtils;
import com.dreamguys.truelysell.utils.PreferenceStorage;
import com.dreamguys.truelysell.utils.ProgressDlg;
import com.dreamguys.truelysell.utils.RetrofitHandler;
import com.dreamguys.truelysell.viewwidgets.CircleImageView;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import retrofit2.Call;

public class ChatDetailActivity extends AppCompatActivity implements RetrofitHandler.RetrofitResHandler {

    @BindView(R.id.rv_chat_room)
    RecyclerView rvChatRoom;
    @BindView(R.id.et_message_content)
    EditText etMessageContent;
    @BindView(R.id.iv_send_message)
    ImageView ivSendMessage;
    ChatDetailAdapterNew mAdapter;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    int items = 10;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.iv_req_proc_img)
    CircleImageView ivReqProcImg;
    @BindView(R.id.ll_send_msg)
    LinearLayout llSendMsg;

    private String chatId;
    int nextPageNo = -1;
    LinearLayoutManager linearLayoutManager;
    DAOChatDetails chatDetailListData;
    DAOChatDetails.ChatHistory chatSendMessageModel;
    boolean isFirstTime;
    HashMap<String, ArrayList<DAOChatDetails.ChatHistory>> groupedHashMap;
    ArrayList<DAOChatDetails.ChatHistory> chatDetailListDataAll = new ArrayList<>();
    public LanguageModel.Common_used_texts common_used_texts = new LanguageModel().new Common_used_texts();
    @BindView(R.id.tb_toolbar)
    Toolbar mToolbar;
    public int appColor = 0;
    JSONObject jsonObject;
    public KProgressHUD kProgressHUD;
    LanguageResponse.Data.Language.ChatScreen chatScreenList;

    private OkHttpClient client;
    private boolean isConnected;
    WebSocket mWebSocket;
    WebSocketClient webSocketClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        ButterKnife.bind(this);

        if (getIntent().getStringExtra(AppConstants.chatFrom) != null && !getIntent().getStringExtra(AppConstants.chatFrom).isEmpty()) {
            chatId = getIntent().getStringExtra(AppConstants.chatFrom);
        }

        if (rvChatRoom.getAdapter() == null) {
            linearLayoutManager
                    = new LinearLayoutManager(ChatDetailActivity.this, LinearLayoutManager.VERTICAL, false);
            rvChatRoom.setLayoutManager(linearLayoutManager);
            mAdapter = new ChatDetailAdapterNew(this, new ArrayList<ListItem>());
            rvChatRoom.setAdapter(mAdapter);
        }
        getLocaleData();
        getChatDetailList(false);
        start();
    }

    private void getLocaleData() {
        try {
            String chatDataStr = PreferenceStorage.getKey(CommonLangModel.ChatScreen);
            chatScreenList = new Gson().fromJson(chatDataStr, LanguageResponse.Data.Language.ChatScreen.class);
            etMessageContent.setHint(AppUtils.cleanLangStr(ChatDetailActivity.this,
                    chatScreenList.getTxtEnterMessage().getName(), R.string.typeSomething));
            tvNoData.setText(AppUtils.cleanLangStr(ChatDetailActivity.this,
                    chatScreenList.getLblNoChat().getName(), R.string.txt_no_data));
        } catch (Exception e) {
            chatScreenList = new LanguageResponse().new Data().new Language().new ChatScreen();
        }
    }

    public void getChatDetailList(boolean isLoadMore) {
        if (AppUtils.isNetworkAvailable(this)) {
            if (!isLoadMore) {
                ProgressDlg.clearDialog();
                ProgressDlg.showProgressDialog(this, null, null);
                nextPageNo = 1;
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<DAOChatDetails> chatDetailListDataCall = apiService.postChatHistoryList(PreferenceStorage.getKey(AppConstants.USER_TOKEN), chatId);
            RetrofitHandler.executeRetrofit(this, chatDetailListDataCall, AppConstants.CHAT_DETAILLIST_DATA, this, isLoadMore);
        } else {
            AppUtils.showToast(getApplicationContext(), AppUtils.cleanLangStr(this, common_used_texts.getLg7_please_enable_i(), R.string.txt_enable_internet));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onSuccess(Object myRes, boolean isLoadMore, String responseModel) {

        switch (responseModel) {
            case AppConstants.CHAT_DETAILLIST_DATA: {
                chatDetailListData = (DAOChatDetails) myRes;

                if (chatDetailListData != null && chatDetailListData.getData() != null && chatDetailListData.getData().getChatHistory().size() > 0) {

                    chatDetailListDataAll.addAll(0, chatDetailListData.getData().getChatHistory());

                    groupDataIntoHashMap(chatDetailListDataAll);

                    ArrayList<ListItem> consolidatedList = new ArrayList<>();

                    for (String date : groupedHashMap.keySet()) {
                        DateItem dateItem = new DateItem();
                        dateItem.setDate(date);
                        consolidatedList.add(dateItem);

                        for (DAOChatDetails.ChatHistory pojoOfJsonArray : groupedHashMap.get(date)) {
                            GeneralItem generalItem = new GeneralItem();
                            generalItem.setChatList(pojoOfJsonArray);
                            consolidatedList.add(generalItem);
                        }
                    }
                    mAdapter.updateRecyclerView(this, consolidatedList);

                    if (!isFirstTime) {
                        scrollToBottom();
                        isFirstTime = true;
                    } else {
                        rvChatRoom.getLayoutManager().scrollToPosition(mAdapter.getItemCount() + 10);
                    }

                } else if (isLoadMore && mAdapter.itemsData.size() > 0) {
                    rvChatRoom.setVisibility(View.VISIBLE);
                    tvNoData.setVisibility(View.GONE);
                } else {
                    rvChatRoom.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                }
            }
            break;
            case AppConstants.CHAT_SENDDETAILLIST_DATA: {
                DAOChatSentResponse chatHistory = (DAOChatSentResponse) myRes;
                DAOChatDetails.ChatHistory chatList = new DAOChatDetails.ChatHistory();
                chatList.setChatId(chatHistory.getData().getChatId());
                chatList.setMessage(chatHistory.getData().getMessage());
                chatList.setCreatedAt(chatHistory.getData().getCreatedAt());
                chatList.setUtc_date_time(chatHistory.getData().getUtcDateTime());
                chatList.setReadStatus(chatHistory.getData().getReadStatus());
                chatList.setReceiverToken(chatHistory.getData().getReceiverToken());
                chatList.setSenderToken(chatHistory.getData().getSenderToken());
                chatList.setStatus(chatHistory.getData().getStatus());
                chatDetailListDataAll.add(chatList);

                groupDataIntoHashMap(chatDetailListDataAll);

                ArrayList<ListItem> consolidatedList = new ArrayList<>();
                for (String date : groupedHashMap.keySet()) {
                    DateItem dateItem = new DateItem();
                    dateItem.setDate(date);
                    consolidatedList.add(dateItem);
                    for (DAOChatDetails.ChatHistory pojoOfJsonArray : groupedHashMap.get(date)) {
                        GeneralItem generalItem = new GeneralItem();
                        generalItem.setChatList(pojoOfJsonArray);
                        consolidatedList.add(generalItem);
                    }
                }
                //Load data
                mAdapter.updateRecyclerView(this, consolidatedList);
                rvChatRoom.getLayoutManager().scrollToPosition(mAdapter.getItemCount() - 1);
                if (mAdapter.itemsData.size() > 0) {
                    rvChatRoom.setVisibility(View.VISIBLE);
                    tvNoData.setVisibility(View.GONE);
                }

                etMessageContent.setText("");
            }
            break;
        }


    }

    private void loadChatDataFromSocket(JSONObject mJSONObject) throws JSONException {
        if (mJSONObject.get("to_res").toString().equalsIgnoreCase(PreferenceStorage.getKey(AppConstants.USER_TOKEN))
                || mJSONObject.get("from_user").toString().equalsIgnoreCase(PreferenceStorage.getKey(AppConstants.USER_TOKEN))) {
            DAOChatDetails.ChatHistory chatList = new DAOChatDetails.ChatHistory();
            chatList.setSenderToken(mJSONObject.get("from_user").toString());
            chatList.setReceiverToken(mJSONObject.get("to_res").toString());
            chatList.setCreatedAt(mJSONObject.get("chattime").toString());
            chatList.setUtc_date_time(mJSONObject.get("chattime").toString());
            chatList.setMessage(mJSONObject.get("message").toString());

            chatDetailListDataAll.add(chatList);
        }


        groupDataIntoHashMap(chatDetailListDataAll);

        ArrayList<ListItem> consolidatedList = new ArrayList<>();
        for (String date : groupedHashMap.keySet()) {
            DateItem dateItem = new DateItem();
            dateItem.setDate(date);
            consolidatedList.add(dateItem);
            for (DAOChatDetails.ChatHistory pojoOfJsonArray : groupedHashMap.get(date)) {
                GeneralItem generalItem = new GeneralItem();
                generalItem.setChatList(pojoOfJsonArray);
                consolidatedList.add(generalItem);
            }
        }
        //Load data
        mAdapter.updateRecyclerView(this, consolidatedList);
        rvChatRoom.getLayoutManager().scrollToPosition(mAdapter.getItemCount() - 1);
        if (mAdapter.itemsData.size() > 0) {
            rvChatRoom.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        }
        etMessageContent.setText("");
    }

    public void scrollToBottom() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getItemCount() > 1) {
                rvChatRoom.getLayoutManager().scrollToPosition(mAdapter.getItemCount() - 1);
            }
        }
    }

    public void failureHandling(Object myRes, boolean isLoadMore, String responseModel) {
        switch (responseModel) {
            case AppConstants.CHAT_DETAILLIST_DATA: {
                if (isLoadMore) {
                    //Remove loading item
                    mAdapter.itemsData.remove(0);
                    mAdapter.notifyItemRemoved(0);
                    mAdapter.notifyDataSetChanged();
                }
            }
            break;
        }
    }

    @Override
    public void onResponseFailure(Object myRes, boolean isLoadMore, String responseModel) {
        failureHandling(myRes, isLoadMore, responseModel);
    }

    @Override
    public void onRequestFailure(Object myRes, boolean isLoadMore, String responseModel) {
        failureHandling(myRes, isLoadMore, responseModel);
    }

    private synchronized HashMap<String, ArrayList<DAOChatDetails.ChatHistory>> groupDataIntoHashMap(ArrayList<DAOChatDetails.ChatHistory> listOfPojosOfJsonArray) {

        groupedHashMap = new LinkedHashMap<>();

        for (DAOChatDetails.ChatHistory pojoOfJsonArray : listOfPojosOfJsonArray) {
            String hashMapKey = "";

            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dates = null;
                dates = simpleDateFormat.parse(pojoOfJsonArray.getCreatedAt());
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = simpleDateFormat2.format(dates);
                hashMapKey = String.valueOf(formattedDate);
                if (groupedHashMap.containsKey(hashMapKey)) {
                    groupedHashMap.get(hashMapKey).add(pojoOfJsonArray);
                } else {
                    ArrayList<DAOChatDetails.ChatHistory> list = new ArrayList<DAOChatDetails.ChatHistory>();
                    list.add(pojoOfJsonArray);
                    groupedHashMap.put(hashMapKey, list);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return groupedHashMap;
    }


    @OnClick(R.id.iv_send_message)
    public void onViewClicked() {
//        if (mWebSocket != null && isConnected) {
        if (!etMessageContent.getText().toString().isEmpty()) {
            sendMessage();
        }
    }

    public void sendMessage() {
        String message = etMessageContent.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from_user", PreferenceStorage.getKey(AppConstants.USER_TOKEN));
            jsonObject.put("message", message);
            jsonObject.put("to_user", chatId);

            if (PreferenceStorage.getKey(AppConstants.USER_TYPE).equalsIgnoreCase("1")) {

                jsonObject.put("usertype", "provider");
            } else{
                jsonObject.put("usertype", "user");
            }

            jsonObject.put("type", "message");
            mWebSocket.send(jsonObject.toString());
            etMessageContent.setText("");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onBackClick(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSupportActionBar(mToolbar);
        if (getIntent().getStringExtra(AppConstants.chatImg) != null && !getIntent().getStringExtra(AppConstants.chatImg).isEmpty()) {
            Picasso.get()
                    .load(AppConstants.BASE_URL + getIntent().getStringExtra(AppConstants.chatImg))
                    .placeholder(R.drawable.ic_pic_view)
                    .error(R.drawable.ic_pic_view)
                    .into(ivReqProcImg);
        }
        if (getIntent().getStringExtra(AppConstants.chatUsername) != null && !getIntent().getStringExtra(AppConstants.chatUsername).isEmpty()) {
            tvUsername.setText(getIntent().getStringExtra(AppConstants.chatUsername));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceStorage.setKey(AppConstants.currentActivity, "");
    }

    private void start() {
        Request request = new Request.Builder().url(AppConstants.WEBSOCKETURL).build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        client = new OkHttpClient();
        mWebSocket = client.newWebSocket(request, listener);
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            mWebSocket = webSocket;
            isConnected = true;

            Log.i("Websocket", "Opened");

            try {
                JSONObject jsonObject = new JSONObject();
                JSONObject userJsonObject = new JSONObject();
                userJsonObject.put("id", PreferenceStorage.getKey(AppConstants.USER_ID));
                userJsonObject.put("name", PreferenceStorage.getKey(AppConstants.PNAME));

                if (PreferenceStorage.getKey(AppConstants.USER_TYPE).equalsIgnoreCase("1")) {

                    userJsonObject.put("usertype", "provider");
                } else{
                    userJsonObject.put("usertype", "user");
                }

//                userJsonObject.put("usertype", "provider");
                jsonObject.put("user", userJsonObject);
                jsonObject.put("type", "registration");
                Log.d("websocket registration", jsonObject.toString());
                mWebSocket.send(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, String message) {
            Log.d("received message", message);
            try {
                final JSONObject msgObject = new JSONObject(message);
                Log.d("received message", String.valueOf(msgObject));
                if (msgObject.has("type")) {
                    if (msgObject.has("from_user") && msgObject.has("to_res") && msgObject.has("message")) {
                        if (!msgObject.getString("from_user").equalsIgnoreCase("null")
                                && !msgObject.getString("to_res").equalsIgnoreCase("null")
                                && !msgObject.getString("message").equalsIgnoreCase("null")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        loadChatDataFromSocket(msgObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("Websocket Receiving bytes : " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Websocket Closing : " + code + " / " + reason);
            isConnected = false;
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            output("Websocket Error : " + t.getMessage());
            isConnected = false;
//            start();
        }

    }

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("message", txt);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mWebSocket.cancel();
    }
}
