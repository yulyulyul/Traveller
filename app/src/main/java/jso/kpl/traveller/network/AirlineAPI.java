package jso.kpl.traveller.network;

import jso.kpl.traveller.model.ResponseResult;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AirlineAPI {

    @FormUrlEncoded
    @POST("/airline_ticket_list")
    Call<ResponseResult<String>> airlineTicketList(@Field("departure_airport") String departure_airport, @Field("entrance_airport") String entrance_airport,
                                                   @Field("departure_date") String departure_date, @Field("entrance_date") String entrance_date);
}
