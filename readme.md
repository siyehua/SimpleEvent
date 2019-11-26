# event listener
## Features
1. use simple
2. support observe in multi thread
3. support send event in multi thread
4. support callback multi thread
5. support observe multi process

## Sample usage
A sample project which provides runnable code examples that demonstrate uses of the classes in
 this project is available in "test" folder.

1. init
```java
    Event.start();
    // if you want observe in other process, you should set:
    Event.addRemoteListener(this);
```

2. observe
```java
Event.getEventImpl().addRegister(Person.class, new IEvent.EventListener() {
       @Override
       public void change(@NonNull String data) {
           Log.e("siyehua", "ui listener, callback in invoke thread: " + Thread.currentThread().getName()
                   + " data : " + data);
       }
   });
```

3. send event
```java
Event.getEventImpl().sendEvent(Person.class, "data ,from process: " + Process.myPid()
                + " thread:" + Thread.currentThread().getName());

```

## Bugs and Feedback
For bugs, feature requests, and discussion please use GitHub Issues. <br/>
For general usage questions please use the google list or StackOverflow.

## LICENSE
```
Copyright 2019 The RxAndroid authors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```