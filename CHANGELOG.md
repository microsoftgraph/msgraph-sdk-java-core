# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.6.4](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.6.3...v3.6.4) (2025-07-04)


### Bug Fixes

* missing bytes when processing chunks of a file during upload ([e0dbff6](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/e0dbff6fe4b15b0cc661cf79d38152e2cb34d117))

## [3.6.3](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.6.2...v3.6.3) (2025-06-20)


### Bug Fixes

* branch name for the CI ([2226099](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/2226099ef65c0bacfa39a72f0df001efd30a99a5))
* branch name for the CI ([a38d725](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/a38d72527515918223a012c503377742939e1554))

## [3.6.2](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.6.1...v3.6.2) (2025-06-20)


### Bug Fixes

* sanity release due to pipeline migration ([91bf946](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/91bf946c98cac5872d343d61427bc80b4affbe0e))

## [3.6.1](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.6.0...v3.6.1) (2025-02-25)


### Bug Fixes

* resolve build errors for azure authentication package on android ([87c1042](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/87c1042f0f5383c4cdfd6a079d3aba643bcc607e))
* resolve build errors for azure authentication package on android ([a379813](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/a3798137e26a27c002ccdd281220f58fd0baa372))

## [3.6.0](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.5.0...v3.6.0) (2025-02-18)


### Features

* adds ChangeNotification interfaces and static methods ([aa23bd7](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/aa23bd7d9a8568cf9abc2b0f9758618bac016547))

## [3.5.0](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.4.0...v3.5.0) (2025-01-02)


### Features

* adds a parameter for isCAE enabled in authentication provider ([a202619](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/a20261919a30a4a0e767773ffdc4c06cab0242d3))


### Bug Fixes

* replaces invalid inherited doc comments for access token provider ([a202619](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/a20261919a30a4a0e767773ffdc4c06cab0242d3))

## [3.4.0](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.3.1...v3.4.0) (2024-11-19)


### Features

* add GraphClientFactory method using TokenCredential ([b75c471](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/b75c471d17436712d7e6cc8e32e606af200670e7))
* Support overriding default interceptors via request options ([667cae6](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/667cae662ec46e938d73817e79d58e84d97171e6))


### Bug Fixes

* issue where custom interceptors would fail to override default interceptors ([adf470a](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/adf470aa76e12920effbc9d8feee5721ecb68101))

## [3.3.1](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.3.0...v3.3.1) (2024-10-23)


### Bug Fixes

* release new version with updated kiota dependencies ([6e70a6e](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/6e70a6e8e83457b20baac14a508400bb1b093432))
* release new version with updated kiota dependencies ([1cfa35a](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/1cfa35ab9cd46b5a5761cbad1ef96f0c0883f8e6))

## [3.3.0](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.2.1...v3.3.0) (2024-09-30)


### Features

* support authorization handler in middleware pipeline ([e0b5675](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/e0b56758d869a8621db3125d950dbe904777c833))

## [3.2.1](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.2.0...v3.2.1) (2024-09-09)


### Bug Fixes

* errorMapping in ResponseCodes with "4XX" and "5XX" Pattern ([#1735](https://github.com/microsoftgraph/msgraph-sdk-java-core/issues/1735)) ([150ff3d](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/150ff3d8d8828e30d41f9587833838232932aa0b))

## [3.2.0](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.1.17...v3.2.0) (2024-09-03)


### Features

* Adds kiota authentication dependency into compilation classpath ([4c80395](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/4c8039518244fd6e89b36c38f6f33b788b4ec212))

## [3.1.17](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.1.16...v3.1.17) (2024-08-23)


### Bug Fixes

* release please bootstrap configuration ([67a19d3](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/67a19d3b2ea6aed2ef2e2a223eaaa37bebbf7c25))

## [3.1.16](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.1.15...v3.1.16) (2024-08-20)


### Bug Fixes

* Retain insertion order of batch request steps ([68c43c1](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/68c43c1371f77553afb2dbf9af370feaa5740c59))

## [3.1.15](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.1.14...v3.1.15) (2024-07-31)


### Bug Fixes

* deadlock for batch request content once it passes a certain size ([3c70728](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/3c707280cd331823643c9affa1b58ffbc5fa8a41))

## [3.1.14](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.1.13...v3.1.14) (2024-06-13)


### Bug Fixes

* **dependencies:** Bump Kiota dependencies ([708513c](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/708513cecbd3599b38557851952c509538e53101))

## [3.1.13](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.1.12...v3.1.13) (2024-06-04)


### Bug Fixes

* **dependencies:** Bump Kiota dependencies ([fe900b4](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/fe900b460f95a07a8276ce2c9d5ebb75472a3a1e))

## [3.1.12](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.1.11...v3.1.12) (2024-05-22)


### Bug Fixes

* disabled checking for stream available length in large file upload [#1621](https://github.com/microsoftgraph/msgraph-sdk-java-core/issues/1621) ([d12a76a](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/d12a76a863899fed44b15c65628ba2b1831965a1))

## [3.1.11](https://github.com/microsoftgraph/msgraph-sdk-java-core/compare/v3.1.10...v3.1.11) (2024-05-22)


### Bug Fixes

* Bump Kiota dependencies ([e1f2d5e](https://github.com/microsoftgraph/msgraph-sdk-java-core/commit/e1f2d5e674cd273c23a47ad5906dd64f88ba240d))

## [3.1.10] - 2024-05-09

### Changed

- Downgraded `jakarta.annotation-api` dependency to `2.1.1` for java 8 compatibility

## [3.1.9] - 2024-04-23

### Changed

- Updated kiota dependencies

## [3.1.8] - 2024-04-22

### Added

### Changed
- Changed chunkInputStream method in LargeFileUploadTask to resolve IndexOutOfBoundsException when uploading large files
- Fix Large File Upload bug where exception was thrown for completed successful uploads

## [3.1.7] - 2024-03-28

### Changed

- Updates kiota dependencies to solve for misalignments.

## [3.1.6] - 2024-02-29

### Changed

- Bumps Kiota-Java abstractions, authentication, http, and serialization components
- Kiota-Java version bumps address a bug where file upload would fail due to unknown contentLength value. [Kiota-Java #1088](https://github.com/microsoft/kiota-java/pull/1088)

## [3.1.5] - 2024-02-27

### Changed
- Bumps Kiota-Java abstractions, authentication, http, and serialization components

## [3.1.4] - 2024-02-21

- Bumps Kiota-Java abstractions, authentication, http, and serialization components
- Fixes a test in the test suite which did not respect the REST reference [#1517](https://github.com/microsoftgraph/msgraph-sdk-java-core/issues/1517)
- Fixes a bug with LargeFileUploadTask [#1517](https://github.com/microsoftgraph/msgraph-sdk-java-core/issues/1517)

### Changed

## [3.1.3] - 2024-02-14

### Changed

- Bumps Kiota-Java abstractions, authentication, http, and serialization components

## [3.1.2] - 2024-02-12

### Changed

- Fixes bug where 'Authorization' header was being added leading to long delays in writing BatchRequests. [#1483](https://github.com/microsoftgraph/msgraph-sdk-java-core/issues/1483)

## [3.1.1] - 2024-02-09

### Changed

- Fixes a bug to allow the PageIterator to iterate across all pages.

## [3.1.0] - 2024-02-07

### Changed

- Version bump for Java SDK GA release.
- Bumps Kiota-Java abstractions, authentication, http, and serialization components for Java SDK 6.1.0 release.

## [3.0.12] - 2023-12-15

### Fixed

- Fixes a bug where a null collection for allowedHosts would result in failure to initialize client. [#1411](https://github.com/microsoftgraph/msgraph-sdk-java-core/pull/1411)

## [3.0.11] - 2023-12-08

### Changed

- Parent namespace for all classes has been changed from com.microsoft.graph.* to com.microsoft.graph.core.* in order to avoid conflicts with the generated service libraries.
- This change is not backwards compatible and will require changes to your code.

## [3.0.10] - 2023-11-27

### Changed

- Removed the usage of reflection for enum deserialization and reordered RequestAdapter method arguments. [Kiota-Java #840](https://github.com/microsoft/kiota-java/issues/840)

## [3.0.9] - 2023-11-14

### Changed

- Kiota-Java has moved away from Async/Completable futures, thus Async components are no longer utilized and have been removed. Furthermore, requestAdapter methods no longer use the async suffix. [Kiota-Java #175](https://github.com/microsoft/kiota-java/issues/175)
- ApiException class now extends RuntimeException instead of Exception.

### Removed

- ServiceException class has been removed.

## [3.0.8] - 2023-08-09

### Changed

- Replaces Javax annotations in favor of Jakarta annotations.

### Removed

- Removes 'SuppressFBWarnings' annotations and dependency.

## [3.0.7] - 2023-07-20

### Added

- Adds graph-java-sdk implementation of the `UrlReplaceHandler` middleware including default replacement pairs.
- Default replacement pair: '/users/TokenToReplace' -> '/me'

## [3.0.6] - 2023-07-11

### Added

- Added the PageIterator functionality for Kiota generated service libraries.

## [3.0.5] - 2023-06-15

### Added

- Added Batch Request and Batch Request Collection functionality for Kiota generated service libraries.

## [3.0.4] - 2023-05-03

### Added

- Added LargeFileUploadTask functionality for kiota generated service libraries.

### Fixed

- Fixes formatting used in the headers added by the telemetry handler to align with the [msGraph sdk spec.](https://github.com/microsoftgraph/msgraph-sdk-design/blob/master/middleware/TelemetryHandler.md)

## [3.0.3] - 2023-04-06

### Changed

- Bumps Kiota-Java abstractions, authentication, http, and serialization components.

## [3.0.2] - 2022-10-10

### Changed

- Bumps Kiota-Java abstractions, authentication, http, and serialization components.

## [3.0.1] - 2022-09-20

### Added

- Uses [Kiota-Java](https://github.com/microsoft/kiota-java) libraries as underlying framework.
- BaseGraphRequestAdapter for use with v1 and beta service libraries.

### Changed

- Removes Request Builders.
- GraphClientFactory to handle OkHttp client creation.
- BaseClient refactored to use Kiota framework.

## [2.0.21] - 2023-11-08

### Changed

- Changed CoreHttpProvider dependency from OkHttpClient to Call.Factory (parent interface implemented by OkHttpClient). This make usage of OpenTelemetry tracing possible.
  https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/main/instrumentation/okhttp/okhttp-3.0/library/README.md

```java
  private Call.Factory createTracedClient(OpenTelemetry openTelemetry, @Nonnull final IAuthenticationProvider auth) {
    return OkHttpTelemetry.builder(openTelemetry).build().newCallFactory(createClient(auth));
  }

  private OkHttpClient createClient(@Nonnull final IAuthenticationProvider auth) {
    return HttpClients.createDefault(auth);
  }

  // then create the GraphServiceClient
    IAuthenticationProvider authenticationProvider = ...;
    GraphServiceClient
    .builder(Call.Factory.class, Request.class)
    .httpClient(createTracedClient(openTelemetry, authenticationProvider))
    .authenticationProvider(authenticationProvider)
    .buildClient();
```

## [2.0.20] - 2023-10-23

### Changed

- Updates Okhttp3 to avoid transient vulnerabilty. [#1038](https://github.com/microsoftgraph/msgraph-sdk-java-core/issues/1038)

## [2.0.19] - 2023-06-20

### Changed

- Remove explicit logging of GraphServiceException in the CoreHttpProvider class. [#885](https://github.com/microsoftgraph/msgraph-sdk-java-core/issues/885)
- Thank you to @MaHa6543 for the contribution.

## [2.0.18] - 2023-04-06

### Changed

- Fix `getRequestUrl()` and `getClient()` wrongfully being declared `@Nullable` in `BaseRequestBuilder`.

## [2.0.17] - 2023-03-20

### Changed

- Aligns default http client timeout to be 100 seconds
- Fixes NullPointerException in GraphErrorResponse#copy

## [2.0.16] - 2023-01-30

### Changed

- Removed unnecessary `net.jcip:jcip-annotations` dependency causing conflicts with Azure Identity.

## [2.0.15] - 2023-01-19

### Changed

- Switched required dependencies from implementation to api to avoid missing dependencies in some scenarios.

## [2.0.14] - 2022-10-06

### Added

### Changed

- Fixes an issue where the error code 'ErrorItemNotFound' is not accounted for. #606
- Bumps azure-core to 1.32.0 #603, #604, #605
- Bumps azure-identity to 1.6.0 #633, #635

## [2.0.13] - 2022-05-26

### Added

### Changed

- Fixed an issue where the error message would be logged twice. #514
- Bumps azure-core to 1.28.0 #503, #504, #506
- Bumps azure-identity to 1.5.1 #505, #507

## [2.0.12] - 2022-04-22

### Added

- Add ability to add custom hosts to BaseAuthenticationProvider #484

### Changed

- Bumps Azure Core to 1.27.0 #474, #473, #472
- Bumps mockito-inline to 4.5.1 #494, #493, #491
- Bumps gradle wrappers to 7.4.0 #454
- Bumps gradle to 7.1.3 in /android #477
- Bumps gradle-enterprise-gradle-plugin 3.10.0 in /android #488
- Bumps dawidd6/action-download-artifact to 2.19.0 #482
- Bumps com.github.spotbugs to 5.0.6 #442, #443, #444
- Bumps spotbugs-annotations to 4.6.0 #460, #461
- Bumps azure-identity to 1.5.0 #475, #476
- Bumps actions/cache to 3.0.0 #469
- Bumps actions/upload-artifact to 3.0.0 #479
- Bumps actions/setup-java to 3.0.0 #478
- Bumps rickstaa/action-create-tag to 1.2.2 #453
- Bumps guava to 31.1-jre #451, #450, #449
- Bumps gson to 2.9.0 #438, #439, #440

## [2.0.11] - 2022-02-04

### Added

- Removing lock on Http protocol 1.1 in preparation for Graph service support of Http2 #429

### Changed

- Bumps Azure Core to 1.24.1 #408, #409, #410
- Bumps mockito-inline to 4.3.1 #422, #423, #424
- Bumps okhttp to 4.9.3 #371, #372
- Bumps junit to 4.13.2 #391, #394
- Bumps junit-jupiter-api to 5.8.2 #379, #382
- Bumps junit-jupiter-egine to 5.8.2 #380
- Bumps junit-jupiter-params to 5.8.2 #381, #383
- Bumps gradle wrappers to 7.3.3 #426
- Bumps gradle-versions-plugin to 0.42.0 in /android #428
- Bumps gradle from 7.1.0 in /android #425
- Bumps gradle-enterprise-gradle-plugin 3.8.1 in /android #413
- Bumps dawidd6/action-download-artifact to 2.17.0 #427
- Bumps com.github.spotbugs to 5.0.5 #416
- Bumps spotbugs-annotations to 4.5.3 #407
- Bumps azure-identity to 1.4.3 #411, #412
- Bumps anton-yurchenko/git-release to 4.2 #378
- Bumps actions/cache from to 2.1.7 #375

## [2.0.10] - 2021-11-16

### Added

- Added support for cancelling requests #361

### Changed

- Fixed a bug where batching would fail for national clouds
- Bumps Azure Core from 1.20.0 to 1.22.0 #359, #360, #341, #342
- Bumps gson from 2.8.8 to 2.8.9 #356, #355
- Bumps actions/checkout from 2.3.5 to 2.4.0 #358, #349
- Upgrades CI pipeline to Java 17 #348, #330
- Bumps mockito-inline from 3.12.4 to 4.0.0 #346, #345
- Bumps action-download-artifact from 2.14.1 to 2.15.0 #344
- Bumps okhttp from 4.9.1 to 4.9.2 #339, #340
- Bumps guava from 30.1.1 to 31.0.1 #338, #337, #335, #336
- Bumps junit-jupiter-api from 5.8.0 to 5.8.1 #332, #334
- Bumps junit-jupiter-egine from 5.8.0 to 5.8.1 #333

## [2.0.9] - 2021-09-17

### Added

- Workflow for Maven preview and GitHub Release
- Workflow for build validation
- Proper handling for InterruptedException

### Changed

- Casing fixed for Odata type parsing
- Removed .azure-pipelines/**
