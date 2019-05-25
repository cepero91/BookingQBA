package com.infinitum.bookingqba.model.remote.errors;

import org.json.JSONObject;

import retrofit2.Response;

public class ErrorUtils {
    public static APIError parseError(final Response<?> response) {
        JSONObject bodyObj = null;
        String msg;
        int errorCode;
        errorCode = response.code();
        try {
            String errorBody = response.errorBody().string();
            if (errorBody != null) {
                bodyObj = new JSONObject(errorBody);
                msg = bodyObj.getJSONArray("msg").getString(0);
            } else {
                msg = "Unable to parse error";
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "Unable to parse error";
        }

        return new APIError(errorCode,msg);
    }
}
