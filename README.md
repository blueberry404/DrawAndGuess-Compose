# Draw And Guess
This game is an inspiration from [Drawize](https://www.drawize.com/play) like games where users guess the sketches of each other. It has been created using Kotlin Compose Multiplatform, runs on both Android and iOS - all code has been written in Kotlin 💜 Uses sockets for the interaction between multiple players.

BE has been hosted on Heroku, feel free to play on your devices/simulators. If you want to check the source code of backend, you can find it [here](https://github.com/blueberry404/DrawAndGuess-BE).

## Screenshots

<img width="748" alt="Screenshot 2023-11-01 at 10 29 03 PM" src="https://github.com/blueberry404/DrawAndGuess-Compose/assets/39243539/7777f53a-ae3d-4d69-8bdb-cc0f4b2edbf5">

<img width="733" alt="Screenshot 2023-11-01 at 10 34 29 PM" src="https://github.com/blueberry404/DrawAndGuess-Compose/assets/39243539/e8f2ce60-279b-463f-b8f7-40ac9f838020">

<img width="742" alt="Screenshot 2023-11-01 at 10 33 32 PM" src="https://github.com/blueberry404/DrawAndGuess-Compose/assets/39243539/ec71143c-6362-4ad2-a6b8-76d9be5454f2">


## Libraries Used
- [Decompose](https://github.com/arkivanov/Decompose) - For navigation management
- [Ktor](https://ktor.io/) - For REST calls and sockets
- [Koin](https://insert-koin.io/) - Dependency Injection
- [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings) - Saves key-value data to disk
- [Napier](https://github.com/AAkira/Napier) - For logging

## Improvements Needed
- Accurate synchronization of events between players.
- More robust error handling - since sockets have loads of cases to manage. I have focused on happy flows and handled disconnectivity issues.
- Use a design pattern like MVI

## Asset References
- Icons from [Icons8](https://icons8.com/) and [FlatIcon](https://www.flaticon.com)
- Handmade font from [Dafont](https://www.dafont.com/handmade-17.font)

