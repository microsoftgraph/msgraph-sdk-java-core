import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.exceptions.AuthenticationException;
import com.microsoft.graph.httpcore.HttpClients;
import okhttp3.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class deviceCodeFlowMain {

    public static void main(String[] args) throws AuthenticationException {
        deviceCodeFlow();
    }

    public static void deviceCodeFlow() throws AuthenticationException {

        List<String> scopes = Arrays.asList("user.ReadBasic.All", "User.Read");

        DeviceCodeCredential deviceCodeCred = new DeviceCodeCredentialBuilder()
                .clientId("cfb5d84c-c88f-466c-81fb-0fe9fa8da052")
                .challengeConsumer(challenge -> {System.out.println(challenge.getMessage());})
                .build();

        TokenCredentialAuthProvider tokenCredAuthProvider = new TokenCredentialAuthProvider(deviceCodeCred, scopes);
        OkHttpClient httpClient = HttpClients.createDefault(tokenCredAuthProvider);

        Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/me/").build();

        httpClient.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                System.out.println(responseBody);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
