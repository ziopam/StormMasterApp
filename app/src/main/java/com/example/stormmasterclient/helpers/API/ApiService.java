package com.example.stormmasterclient.helpers.API;

import com.example.stormmasterclient.helpers.RoomDatabase.BrainstormEntity;
import com.google.gson.JsonElement;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Interface for API calls.
 */
public interface ApiService {

    /**
     * Registers a new user.
     *
     * @param body The request body containing the user registration data.
     * @return A call object for the registration request.
     */
    @POST("api/auth/users/")
    Call<JsonElement> userRegistration(@Body RequestBody body);

    /**
     * Logs in an existing user.
     *
     * @param body The request body containing the user login data.
     * @return A call object for the login request.
     */
    @POST("api/auth/token/login/")
    Call<JsonElement> userLogin(@Body RequestBody body);

    /**
     * Logs out the current user.
     *
     * @param token The authorization token of the user.
     * @return A call object for the logout request.
     */
    @POST("api/auth/token/logout/")
    Call<JsonElement> userLogout(@Header("Authorization") String token);

    /**
     * Changes the password of the current user.
     *
     * @param token The authorization token of the user.
     * @param body The request body containing the old and new passwords data.
     * @return A call object for the password change request.
     */
    @POST("api/auth/users/set_password/")
    Call<JsonElement> userChangePassword(@Header("Authorization") String token, @Body RequestBody body);

    /**
     * Retrieves the brainstorms of the current user.
     *
     * @param token The authorization token of the user.
     * @return A call object for the request to get user brainstorms.
     */
    @GET("api/get_user_brainstorms/")
    Call<List<BrainstormEntity>> getUserBrainstorms(@Header("Authorization") String token);

    /**
     * Deletes a specific brainstorm of the current user if they're creator of it.
     *
     * @param token The authorization token of the user.
     * @param id The ID of the brainstorm to be deleted.
     * @return A call object for the delete request.
     */
    @DELETE("api/delete_user_brainstorm/{id}/")
    Call<JsonElement> deleteUserBrainstorm(@Header("Authorization") String token, @Path("id") int id);
}
