package com.example.neatify.api

import com.example.neatify.model.DeleteResponse
import com.example.neatify.model.LoginResponse
import com.example.neatify.model.Order
import com.example.neatify.model.ServiceResponse
import com.example.neatify.model.OrderRequest
import com.example.neatify.model.OrderResponse
import com.example.neatify.model.OrderListResponse
import com.example.neatify.model.UpdateStatusRequest
import com.example.neatify.model.ServiceRequest
import com.example.neatify.model.ServiceSingleResponse
import com.example.neatify.model.User
import com.example.neatify.model.TopUpRequest
import com.example.neatify.model.WalletTransactionResponse
import okhttp3.ResponseBody
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT


interface ApiService {

    @POST("login")
    fun login(
        @Body request: Map<String, String>
    ): Call<ResponseBody>

    @POST("register")
    fun register(
        @Body request: Map<String, String>
    ): Call<ResponseBody>

    @GET("services")
    fun getServices(): Call<ServiceResponse>

    @POST("orders")
    fun createOrder(
        @Body request: OrderRequest
    ): Call<OrderResponse>

    @GET("users/{id}/orders")
    fun getUserOrders(
        @Path("id") userId: Int
    ): Call<OrderListResponse>

    @GET("orders/{id}")
    fun getOrderDetail(
        @Path("id") orderId: Int
    ): Call<OrderResponse>

    @GET("orders")
    fun getAllOrders(): Call<List<Order>>

    @PUT("orders/{id}/status")
    fun updateOrderStatus(
        @Path("id") orderId: Int,
        @Body request: UpdateStatusRequest
    ): Call<OrderResponse>

    @GET("profile/{id}")
    fun getProfile(
        @Path("id") userId: Int
    ): Call<LoginResponse>

    @POST("services")
    fun createService(
        @Body request: ServiceRequest
    ): Call<ServiceSingleResponse>

    @PUT("services/{id}")
    fun updateService(
        @Path("id") serviceId: Int,
        @Body request: ServiceRequest
    ): Call<ServiceSingleResponse>

    @DELETE("services/{id}")
    fun deleteService(
        @Path("id") serviceId: Int
    ): Call<DeleteResponse>

    @GET("users")
    fun getUsers(): Call<List<User>>

    @POST("users/{id}/topup")
    fun topUpSaldo(
        @Path("id") userId: Int,
        @Body request: TopUpRequest
    ): Call<LoginResponse>

    @GET("users/{id}/wallet-transactions")
    fun getWalletTransactions(
        @Path("id") userId: Int
    ): Call<WalletTransactionResponse>
}