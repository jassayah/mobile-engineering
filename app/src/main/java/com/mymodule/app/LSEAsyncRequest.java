package com.mymodule.app;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;


/**
 * Created by jassayah on 12/8/14.
 * LSEAsyncRequest is a Volley adapter for JSON requests that will be parsed into Java objects by Gson.
 */
public class LSEAsyncRequest<T> extends Request<T> {

    private final Gson gson = new Gson();
    private final Type type;
    private final Map<String, String> headers;
    private final Listener<T> listener;
    private final ErrorListener errorListener;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param type    Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public LSEAsyncRequest(String url, Type type, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.type = type;
        this.headers = headers;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            T t = gson.fromJson(json, type);
            return Response.success(t, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
