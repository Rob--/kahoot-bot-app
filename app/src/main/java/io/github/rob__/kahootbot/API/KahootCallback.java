package io.github.rob__.kahootbot.API;

import retrofit2.Response;

public interface KahootCallback {
    void onResponse(Response<KahootResponse> response);
    void onFailure(Throwable t);
}