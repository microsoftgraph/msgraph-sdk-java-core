# Microsoft Graph Core SDK for Java

Get started with the Microsoft Graph Core SDK for Java by integrating the [Microsoft Graph API](https://graph.microsoft.io/en-us/getting-started) into your Java and Android application!

## Samples and usage guide

- [Middleware usage](https://github.com/microsoftgraph/msgraph-sdk-java-core/wiki)
- [Batching](https://github.com/microsoftgraph/msgraph-sdk-java-core/wiki/Batching)

## 1. Installation

### 1.1 Install via Gradle

Add the repository and a compile dependency for `microsoft-graph-core` to your project's `build.gradle`:

```gradle
repositories {
    jcenter()
}

dependencies {
    // Include the sdk as a dependency
    implementation 'com.microsoft.graph:microsoft-graph-core:1.0.2'
}
```

### 1.2 Install via Maven

Add the dependency in `dependencies` in pom.xml

```xml
<dependency>
	<groupId>com.microsoft.graph</groupId>
	<artifactId>microsoft-graph-core</artifactId>
	<version>1.0.2</version>
</dependency>
```

### 1.3 Enable ProGuard (Android)

The nature of the Graph API is such that the SDK needs quite a large set of classes to describe its functionality. You need to ensure that [ProGuard](https://developer.android.com/studio/build/shrink-code.html) is enabled on your project. Otherwise, you will incur long build times for functionality that is not necessarily relevant to your particular application. If you are still hitting the 64K method limit, you can also enable [multidexing](https://developer.android.com/studio/build/multidex.html).

## 2. Getting started

### 2.1 Register your application

Register your application by following the steps at [Register your app with the Azure AD v2.0 endpoint](https://developer.microsoft.com/en-us/graph/docs/concepts/auth_register_app_v2).

### 2.2 Create an IAuthenticationProvider object

An instance of the **HttpClients** class handles building client. To create a new instance of this class, you need to provide an instance of `ICoreAuthenticationProvider`, which can authenticate requests to Microsoft Graph.

### To get instance of HttpClients

Auth in Java app [here](https://github.com/microsoftgraph/msgraph-sdk-java-auth)

Auth in Android app [here](https://github.com/microsoftgraph/msgraph-sdk-android-auth)

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

## 6. Supported Java versions

The Microsoft Graph SDK for Java library is supported at runtime for Java 8+ and [Android API revision 15](http://source.android.com/source/build-numbers.html) and greater through [desugaring](https://developer.android.com/studio/write/java8-support.html#library-desugaring).

## 7. License

Copyright (c) Microsoft Corporation. All Rights Reserved. Licensed under the [MIT license](LICENSE).

## 8. Third-party notices

[Third-party notices](THIRD%20PARTY%20NOTICES)
