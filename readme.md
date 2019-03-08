# Microsoft Graph Core SDK for Java

Get started with the Microsoft Graph Core SDK for Java by integrating the [Microsoft Graph API](https://graph.microsoft.io/en-us/getting-started) into your Java application!

## 1. Installation

### 1.1 Install via Gradle

Add the repository and a compile dependency for `microsoft-graph-core` to your project's `build.gradle`:

```gradle
repository {
    jcenter()
	jcenter{
        url 'http://oss.jfrog.org/artifactory/oss-snapshot-local'
    }
}

dependency {
    // Include the sdk as a dependency
    compile('com.microsoft.graph:microsoft-graph-core:0.1.0-SNAPSHOT')
}
```

### 1.2 Install via Maven
Add the dependency in `dependencies` in pom.xml
```dependency
<dependency>
	<groupId>com.microsoft.graph</groupId>
	<artifactId>microsoft-graph-core</artifactId>
	<version>0.1.0-SNAPSHOT</version>
</dependency>
```

Add `profiles` in `project` to download Snapshot release binary:
```
<profiles>
	<profile>
		<id>allow-snapshots</id>
		<activation>
			<activeByDefault>true</activeByDefault>
		</activation>
		<repositories>
			<repository>
				<id>snapshots-repo</id>
				<url>https://oss.sonatype.org/content/repositories/snapshots</url>
				<releases>
					<enabled>false</enabled>
				</releases>
				<snapshots>
					<enabled>true</enabled>
				</snapshots>
			</repository>
		</repositories>
	</profile>
</profiles>
```

### 1.3 Enable ProGuard (Android)
The nature of the Graph API is such that the SDK needs quite a large set of classes to describe its functionality. You need to ensure that [ProGuard](https://developer.android.com/studio/build/shrink-code.html) is enabled on your project. Otherwise, you will incur long build times for functionality that is not necessarily relevant to your particular application. If you are still hitting the 64K method limit, you can also enable [multidexing](https://developer.android.com/studio/build/multidex.html).

## 2. Getting started

### 2.1 Register your application

Register your application by following the steps at [Register your app with the Azure AD v2.0 endpoint](https://developer.microsoft.com/en-us/graph/docs/concepts/auth_register_app_v2).

### 2.2 Create an IAuthenticationProvider object

An instance of the **HttpClients** class handles building client. To create a new instance of this class, you need to provide an instance of `ICoreAuthenticationProvider`, which can authenticate requests to Microsoft Graph.

### 2.3 Get a HttpClients object
You must get a **HttpClients** object to make requests against the service.

```java
OkHttpClient client = HttpClients.createDefault(iCoreAuthenticationProvider);
```

## 3. Make requests against the service

After you have a HttpClients that is authenticated, you can begin making calls against the service. The requests against the service look like our [REST API](https://developer.microsoft.com/en-us/graph/docs/concepts/overview).

### 3.1 Get the user's details

To retrieve the user's details

```java
Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/me/").build();

client.newCall(request).enqueue(new Callback() {
	@Override
	public void onResponse(Call call, Response response) throws IOException {
		String responseBody = response.body().string();
		// Your processing with the response body 
	}
			
	@Override
	public void onFailure(Call call, IOException e) {
		e.printStackTrace();
	}
});
```

### 3.2 Get the user's drive

To retrieve the user's drive:

```java
Request request = new Request.Builder().url("https://graph.microsoft.com/v1.0/me/drive").build();

client.newCall(request).enqueue(new Callback() {
	@Override
	public void onResponse(Call call, Response response) throws IOException {
		String responseBody = response.body().string();
		// Your processing with the response body 
	}
			
	@Override
	public void onFailure(Call call, IOException e) {
		e.printStackTrace();
	}
});
```

## 4. Issues

For known issues, see [issues](https://github.com/MicrosoftGraph/msgraph-sdk-java-core/issues).

## 5. Contributions

The Microsoft Graph SDK is open for contribution. To contribute to this project, see [Contributing](https://github.com/microsoftgraph/msgraph-sdk-java-core/blob/master/CONTRIBUTING.md).

<!-- ALL-CONTRIBUTORS-LIST:START  -->
<!-- prettier-ignore -->
| [<img src="https://avatars2.githubusercontent.com/u/3197588?v=4" width="100px;"/><br /><sub><b>Deepak Agrawal</b></sub>](https://github.com/deepak2016)<br />[??](https://github.com/microsoftgraph/msgraph-sdk-java-core/commits?author=deepak2016 "Code") | [<img src="https://avatars3.githubusercontent.com/u/16473684?v=4" width="100px;"/><br /><sub><b>Nakul Sabharwal</b></sub>][??](https://github.com/microsoftgraph/msgraph-sdk-java-core/commits?author=NakulSabharwal "Code")  (https://developer.microsoft.com/graph)<br />[](#question-NakulSabharwal "Answering Questions") [](https://github.com/microsoftgraph/msgraph-sdk-android-auth/commits?author=NakulSabharwal "Code") [](https://github.com/microsoftgraph/msgraph-sdk-android-auth/wiki "Documentation") [](#review-NakulSabharwal "Reviewed Pull Requests") [](https://github.com/microsoftgraph/msgraph-sdk-android-auth/commits?author=NakulSabharwal "Tests") <br />
| :---: | :---: |
<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/kentcdodds/all-contributors) specification. Contributions of any kind are welcome!

## 6. Supported Java versions
The Microsoft Graph SDK for Java library is supported at runtime for Java 7+ and [Android API revision 15](http://source.android.com/source/build-numbers.html) and greater.

## 7. License

Copyright (c) Microsoft Corporation. All Rights Reserved. Licensed under the [MIT license](LICENSE).

## 8. Third-party notices

[Third-party notices](THIRD%20PARTY%20NOTICES)
