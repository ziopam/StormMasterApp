package com.example.stormmasterclient.helpers.API;

import com.example.stormmasterclient.helpers.RoomDatabase.BrainstormEntity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    /**
     * Creates a new room for brainstorm.
     * @param token The authorization token of the user.
     * @param body The request body containing the room data.
     * @return A call object for the create room request.
     */
    @POST("api/rooms/create/")
    Call<JsonObject> createRoom(@Header("Authorization") String token, @Body RequestBody body);

    /**
     * Joins a room.
     * @param token The authorization token of the user.
     * @param body The request body containing the room data.
     * @return A call object for the join room request.
     */
    @POST("api/rooms/join/")
    Call<JsonObject> joinRoom(@Header("Authorization") String token, @Body RequestBody body);

    /**
     * Leaves a room.
     * @param token The authorization token of the user.
     * @param body The request body containing the room data.
     * @return A call object for the leave room request.
     */
    @POST("api/rooms/leave/")
    Call<JsonObject> leaveRoom(@Header("Authorization") String token, @Body RequestBody body);

    /**
     * Deletes a room.
     * @param token The authorization token of the user.
     * @param roomCode The code of the room to delete.
     * @return A call object for the delete room request.
     */
    @DELETE("api/rooms/delete/{room_code}/")
    Call<JsonObject> deleteRoom(@Header("Authorization") String token, @Path("room_code") String roomCode);

    /**
     * Starts a brainstorm.
     * @param token The authorization token of the user.
     * @param body The request body containing the room data.
     * @return A call object for the start brainstorm request.
     */
    @POST("api/rooms/start/")
    Call<JsonObject> startBrainstorm(@Header("Authorization") String token, @Body RequestBody body);
}
