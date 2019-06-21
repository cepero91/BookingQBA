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
                msg = "Un error ha ocurrido";
                bodyObj = new JSONObject(errorBody);
                if (!bodyObj.isNull("msg")){
                    msg = bodyObj.getJSONArray("msg").getString(0);
                } else if(!bodyObj.isNull("non_field_errors")){
                    msg = bodyObj.getJSONArray("non_field_errors").getString(0);
                }
            } else {
                msg = "Un error ha ocurrido";
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "Un error ha ocurrido";
        }

        return new APIError(errorCode, msg);
    }
}
