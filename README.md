#  The App

This app was done as an android assesment for an undergraduate research position. It simulates  simulates the graphing of Gyroscopic and Acelerometer data. 
# App Architecture

The app is written entirely in Kotlin using Jetpack Compose as the UI. There are two primary classes as descibed below:
- MainActivity: Loads the app as well as contains a function to read in the CSV data
- MainScreen: Generates the UI and graphs.

# Switching between manual and auto mode

As of right now the app only reads CSV data and plots it on to the graph as if it were in real time. In the future, in order to add the ability to switch to auto mode, much of the the logic can still remain the same. This includes the graphing and displaying of data. In order to actually pull in the data, you might use a technology like [SQLite](https://developer.android.com/training/data-storage/sqlite) where a sensor will push the data onto a database which can be pulled from the app itself. Another option might be using the built-in android sensors that come with things like accelerometers. With this method, you can use many of the built in [Kotlin and Android features](https://developer.android.com/develop/sensors-and-location/sensors/sensors_motion).

# Future improvements
- Graphing libraries: Currently, I use the built-in [Canvas](https://developer.android.com/reference/kotlin/android/graphics/Canvas) feature of Kotlin to display the graphs. These are not very customizable and does not come with a built in x and y axis. Currently, I just have values at the top and bottom of the graph indicating the maximum and minimum values currently being displayed. In the future, I would like to use a 3rd part library like [Vico](https://patrykandpatrick.com/vico/wiki/) or [YCharts](https://github.com/codeandtheory/YCharts) that allow for more graph customizability. 
- Graph Interaction: The current graphs act like a video and don't allow for user interaction. In the future, adding something like a zoom function would be interesting.

