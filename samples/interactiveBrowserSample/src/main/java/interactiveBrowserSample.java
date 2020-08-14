import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.azure.identity.InteractiveBrowserCredential;
import com.azure.identity.InteractiveBrowserCredentialBuilder;
import com.microsoft.graph.authentication.*;
import okhttp3.*;
import com.microsoft.graph.httpcore.HttpClients;
import com.microsoft.graph.exceptions.*;

public class interactiveBrowserSample {

    private final static String CLIENT_ID = "199e4de3-dd3b-4a51-b78a-86b801246e20";
    private final static List<String> SCOPES = Arrays.asList("user.ReadBasic.All");

    public static void main(String args[]) throws Exception {
        interactiveBrowser();
    }

    private static void interactiveBrowser() throws AuthenticationException{

        InteractiveBrowserCredential interactiveBrowserCredential = new InteractiveBrowserCredentialBuilder()
                .clientId(CLIENT_ID)
                .port(8765)
                .build();

        TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(interactiveBrowserCredential,SCOPES);
        OkHttpClient httpClient = HttpClients.createDefault(tokenCredentialAuthProvider);

        Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/me/").build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
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
