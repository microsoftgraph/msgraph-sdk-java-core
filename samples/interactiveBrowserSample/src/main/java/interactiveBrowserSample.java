import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.InteractiveBrowserCredential;
import com.azure.identity.InteractiveBrowserCredentialBuilder;
import com.microsoft.graph.authentication.*;
import okhttp3.*;
import com.microsoft.graph.httpcore.HttpClients;
import com.microsoft.graph.exceptions.*;

public class interactiveBrowserSample {
    private final static String CLIENT_ID = "fa62d88a-02fa-4893-88b3-8e566c7ad6f5";
    private final static String AUTHORITY = "https://login.microsoftonline.com/common/";
    private final static List<String> SCOPE = Stream.of("user.read").collect(Collectors.toList());

    public static void main(String args[]) throws Exception {
        getUserWithHttp();
    }

    private static void getUserWithHttp() throws AuthenticationException{
        DeviceCodeCredential deviceCodeCredential = new DeviceCodeCredentialBuilder()
                .challengeConsumer(challenge -> {
                    // lets user know of the challenge
                    System.out.println(challenge.getMessage());
                })
                .clientId(CLIENT_ID)
                .authorityHost(AUTHORITY)
                .build();

//        InteractiveBrowserCredential interactiveBrowserCredential = new InteractiveBrowserCredentialBuilder()
//                .clientId(CLIENT_ID)
//                .port(8765)
//                .build();

        TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(deviceCodeCredential,SCOPE);
        OkHttpClient httpClient = HttpClients.createDefault(tokenCredentialAuthProvider);

        Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/me/").build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                // Your processing with the response body
                System.out.println(responseBody);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

}
