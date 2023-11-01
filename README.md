# Draw And Guess
This game is an inspiration from [Drawize](https://www.drawize.com/play) like games where users guess the sketches of each other. It has been created using Kotlin Compose Multiplatform, runs on both Android and iOS - all code has been written in Kotlin 💜 Uses sockets for the interaction between multiple players.

BE has been hosted on Heroku, feel free to play on your devices/simulators. If you want to check the source code of backend, you can find it [here](https://github.com/blueberry404/DrawAndGuess-BE).

## Screenshots

<img width="748" alt="Screenshot 2023-11-01 at 10 29 03 PM" src="https://github.com/blueberry404/DrawAndGuess-Compose/assets/39243539/afb78f89-4993-498d-a4cc-2fb142bac55a">

<img width="710" alt="Screenshot 2023-11-01 at 10 31 18 PM" src="https://github.com/blueberry404/DrawAndGuess-Compose/assets/39243539/6908bf5b-91eb-44eb-8ab8-07e4af2df88f">

<img width="748" alt="Screenshot 2023-11-01 at 11 04 14 PM" src="https://github.com/blueberry404/DrawAndGuess-Compose/assets/39243539/24390afb-5c18-4b45-85e9-95ddee795cdf">


## Libraries Used
- [Decompose](https://github.com/arkivanov/Decompose) - For navigation management
- [Ktor](https://ktor.io/) - For REST calls and sockets
- [Koin](https://insert-koin.io/) - Dependency Injection
- [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings) - Saves key-value data to disk
- [Napier](https://github.com/AAkira/Napier) - For logging

## Improvements Needed
- Accurate synchronization of events between players.
- Canvas drawing should have compatible mapping across different device sizes.
- More robust error handling - since sockets have loads of cases to manage. I have focused on happy flows and handled disconnectivity issues.
- Use a design pattern like MVI

## Asset References
- Icons from [Icons8](https://icons8.com/) and [FlatIcon](https://www.flaticon.com)
- Handmade font from [Dafont](https://www.dafont.com/handmade-17.font)

