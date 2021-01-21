import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.azure.identity.InteractiveBrowserCredential;
import com.azure.identity.InteractiveBrowserCredentialBuilder;
import com.microsoft.graph.authentication.*;
import okhttp3.*;
import com.microsoft.graph.httpcore.HttpClients;
import com.microsoft.graph.exceptions.*;

public class interactiveBrowserMain {

    //Replace CLIENT_ID with your own client id from an adequately configured app
    //for requirements visit:
    //https://github.com/Azure/azure-sdk-for-java/wiki/Set-up-Your-Environment-for-Authentication#enable-applications-for-interactive-browser-oauth-2-flow
    private final static String CLIENT_ID = "199e4de3-dd3b-4a51-b78a-86b801246e20";

    //Set the scopes for your ms-graph request
    private final static List<String> SCOPES = Arrays.asList("User.ReadBasic.All");

    public static void main(String args[]) throws Exception {
        interactiveBrowser();
    }

    private static void interactiveBrowser() throws AuthenticationException{

        final InteractiveBrowserCredential interactiveBrowserCredential = new InteractiveBrowserCredentialBuilder()
                .clientId(CLIENT_ID)
                .redirectUrl("http://localhost:8765")
                .build();

        final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(SCOPES, interactiveBrowserCredential);
        final OkHttpClient httpClient = HttpClients.createDefault(tokenCredentialAuthProvider);

        final Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/me/").build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
