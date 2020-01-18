package com.classwork.robot;


import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface TulingApi {

    @POST
    Observable<TulingResultBody> sendMessage(@Url String url, @Body TulingRequstBody postParmas);

}
