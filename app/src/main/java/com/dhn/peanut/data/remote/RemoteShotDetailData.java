package com.dhn.peanut.data.remote;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dhn.peanut.data.Comment;
import com.dhn.peanut.data.base.ShotDetailDataSource;
import com.dhn.peanut.util.AuthoUtil;
import com.dhn.peanut.util.PeanutInfo;
import com.dhn.peanut.util.RequestManager;
import com.dhn.peanut.util.Requet4Comments;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DHN on 2016/6/2.
 */
public class RemoteShotDetailData implements ShotDetailDataSource {

    public static RemoteShotDetailData INSTANCE;

    public static RemoteShotDetailData getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteShotDetailData();
        }
        return INSTANCE;
    }


    @Override
    public void getComment(int shotId, final LoadShotDetailCallBack commentCallBack) {
        //发起网络请求
        String url = Comment.COMMENTS_BASE_URL + shotId + "/" + "comments";
        RequestManager.addRequest(new Requet4Comments(url, new Response.Listener<List<Comment>>() {
            @Override
            public void onResponse(List<Comment> response) {
                Log.e("remote", response.toString());
                commentCallBack.onCommentLoaded(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }), null);
    }

    @Override
    public void checkIfLike(int shotId, final LoadShotDetailCallBack loadShotDetailCallBack) {
        String url = PeanutInfo.REQUEST_ON_SHOT_URL + shotId + "/" + "like";
        final String token = AuthoUtil.getToken();

        if (token == null) {
            loadShotDetailCallBack.onFavorChecked(false);
        } else {
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            loadShotDetailCallBack.onFavorChecked(true);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loadShotDetailCallBack.onFavorChecked(false);
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put(PeanutInfo.HEAD_AUTH_FILED, PeanutInfo.HEAD_BEAR + token);
                    header.putAll(super.getHeaders());
                    return header;
                }
            };

            RequestManager.addRequest(request, null);
        }

    }

    @Override
    public void changeLike(int shotId, boolean isLike) {
        String url = PeanutInfo.REQUEST_ON_SHOT_URL + shotId + "/" + "like";
        final String token = AuthoUtil.getToken();

        if (token == null) {
            //TODO
            com.dhn.peanut.util.Log.e("先登录");
        } else {
            JsonObjectRequest request = new JsonObjectRequest(
                    isLike ? Request.Method.POST : Request.Method.DELETE,
                    url,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put(PeanutInfo.HEAD_AUTH_FILED, PeanutInfo.HEAD_BEAR + token);
                    header.putAll(super.getHeaders());
                    return header;
                }
            };

            RequestManager.addRequest(request, null);
        }
    }
}
