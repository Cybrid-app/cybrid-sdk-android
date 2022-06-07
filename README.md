![CircleCI](https://circleci.com/gh/Cybrid-app/cybrid-sdk-android.svg?style=svg)
[![codecov](https://codecov.io/gh/Cybrid-app/cybrid-sdk-android/branch/main/graph/badge.svg?token=LTJJFQJWEA)](https://codecov.io/gh/Cybrid-app/cybrid-sdk-android)

# cybrid-android-sdk

This project contains the SDK of Android components that interface with Cybrid's api and a Demo Application.

## SDK

### Installation

#### Requirements

- Android 5.0 (API level 21) and above
- Android Gradle Plugin 3.5.1 and above
- Gradle 5.4.1+
- AndroidX


#### Configuration

Add `cybrid-android-sdk` to your `build.gradle` dependencies.

``` 
dependencies {
  implementation 'app.cybrid:cybrid-android-sdk:0.0.3'
}
```

### Getting Started

You need to add this methods of the SDK inside into yours custom `Application` and extend to `CybridSDKEvents`

```
class App : Application(), CybridSDKEvents {

  override fun onCreate() {

    super.onCreate()
    setupCybridSDK()
  }

  fun setupCybridSDK() {
    Cybrid.instance.listener = this
  }
}
```

The `CybridSDKEvents` contains a method called `onTokenExpired` that have to be implemented inside the custom implementation of `Application`

```
override fun onTokenExpired() {

  // -- Set a new Bearer Token
}
```

This method gonna be called every time the Bearer Token is expired and the SDK needs new one.

### Set current Berear Token

To set a valid bearer token you have to set it with any time you need it

```
Cybrid.instance.setBearer("")
```

### Extra configuration

You can configre the custom tag to log events of the SDK with:

```
Cybrid.instance.tag = "XXX"
```


And each component has the posibility to configure the refresh time:

```
component.updateInterval = 2000L
```

### Components Integration

#### ListPricesView

Add `ListPricesView` inside your view

```
<app.cybrid.sdkandroid.components.ListPricesView
  android:id="@+id/listPricesView"
  android:layout_width="0dp"
  android:layout_height="wrap_content"/>
```

And then get the ListPricesViewModel with:

```
val viewModel: ListPricesViewModel by viewModels()
```

Then configure the component inside your activity or fragemnt with:

```
val cryptoList = findViewById<ListPricesView>(R.id.listPricesView)
cryptoList.setViewModel(viewModel)
cryptoList.updateInterval = 2000L
```

## Demo App

To run the demo app it's necessary add to enviroment vars into the system

```
export CybridAPI_ID = 'OYZBGymNLMcdsjECQkDfHI1lgwSDQ8eZ9N5K592FzM0'
export CybridAPI_Secret='Au5WnNc8T3w48yhKErdcQoTwj8dknrW2tTx8YiF60Io'
```

Then `Clean Project` and then `Rebuild Project`.

After this just need to run the demo app inside the emulator or physical device.