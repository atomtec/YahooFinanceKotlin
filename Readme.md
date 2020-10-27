

## YAhooFinance Demo

This app demostrate the usage of YahooFinance API via the finance SDK

## Architecture

This is written in Kotlin using MVVM pattern and uses Room Library for Persistance 

The UI instantiates the ViewModel which calls the repository to refresh and add stock .
This is a model driven UI , data is synced to the DB and updated via live data on the UI
When the app is launched and DB has stocks then it will immedately refresh it and start a sync 
for checking every 15 seconds .
Once the app is the in the background syncing stops 



## Demo

![YahooFinance Demo](demo/demo.gif)
