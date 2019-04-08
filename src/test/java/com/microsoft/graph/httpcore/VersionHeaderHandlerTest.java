package com.microsoft.graph.httpcore;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Ignore
public class VersionHeaderHandlerTest {

	@Test
	public void addVerisonHeaderToDefaultTest() {
		OkHttpClient client = HttpClients.createDefault(new ICoreAuthenticationProvider() {
			@Override
			public Request authenticateRequest(Request request) {
				return request.newBuilder().addHeader("Authorization", "Bearer accesstoken").build();
			}
		});

		Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users").build();
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				assertNotNull(response);
			}

			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Test
	public void addVerisonHeaderCustomTest() {
		ICoreAuthenticationProvider authProvider = new ICoreAuthenticationProvider() {
			@Override
			public Request authenticateRequest(Request request) {
				return request.newBuilder().addHeader("Authorization", "Bearer accesstoken").build();
			}
		};
		OkHttpClient client = HttpClients.custom().addInterceptor(new AuthenticationHandler(authProvider)).build();
		Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users").build();
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				assertNotNull(response);
			}

			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	@Test
	public void addVerisonHeaderToCheckDoNotOverrideTest() throws IOException {
		OkHttpClient client = HttpClients.createDefault(new ICoreAuthenticationProvider() {
			@Override
			public Request authenticateRequest(Request request) {
				return request.newBuilder().addHeader("Authorization", "Bearer accesstoken").build();
			}
		});

		Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/users").addHeader("SdkVerison", "test-header-not-override").build();
		Response response = client.newCall(request).execute();
	}
}	
