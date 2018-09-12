# The Popular Movies Android App

The Popular Movies Android app, was made as part of Udacity's **Android Developer Nanodegree Program**. This app was created from scratch and has adaptive UI for phone and tablet devices. It displays the **_Most Popular_** and **_Top Rated_** Movies. The User has the ability to save favourite movies locally and view them even when the device is offline. After selecting a movie, they will be presented with the movie details (rating, release date, duration, etc.), trailers, reviews and the option to share the movie.

## Download:

You can download an APK build on [releases page]().


## Instructions for Developers

The app fetches movie information using The Movie Database (TMDb) API. In order to use the code, you have to enter your own API key into gradle.properties file.

```Groovy
MOVIE_DB_API_KEY="Your Api Key"
```

If you don’t already have a TMDb API key, you will need to create an account and request your own API Key - [TMDb link](link to API Key page)



## Libraries Used

* Picasso
* DataBinding 


## License

Copyright (C) 2018 Nilesh Patel

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.