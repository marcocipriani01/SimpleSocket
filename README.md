# SimpleSocket

Super-simple Socket implementation for Android

## Simple client example

```
public class ExampleClient extends SimpleClient {

    @Override
    public void onMessage(Socket from, String msg) {
        Log.i("ExampleClient", "Message from " + getAddress() + ": " + msg);
    }

    @Override
    protected void onError(Exception e) {
        Log.e("ExampleClient", e.getMessage(), e);
        try {
            disconnect();
        } catch (ConnectionException ex) {
            Log.e("ExampleClient", "Could not disconnect");
        }
    }
}
```

To connect use:

```
ExampleClient client = new ExampleClient();
try {
    client.connect("192.168.1.5", 12345);
} catch (ConnectionException e) {
    Log.e("ExampleClient", "Could not connect");
}
// ...do something
client.disconnect();
```

## Adding the library to Gradle

Add the repository to the root `build.gradle`:

```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

Then the dependency in the module `app`:

```
dependencies {
    ...
    implementation 'com.github.marcocipriani01:SimpleSocket:RELEASE'
}
```

## License

Copyright 2021 Marco Cipriani (@marcocipriani01)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at [apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0).
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.