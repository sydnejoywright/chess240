# ♕ BYU CS 240 Chess
AAAAAAAAAAAAAAAAAAAAALSDFL
This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Chess Server Design

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3VgdQ0Tw52EyhygAzXycA2YfMdpOFbI9mAAFicAGZuqW+qN1MByeMpgBRKDeNPABC6jhheRzvoDldaUjyKNUNyFYAOFRdFYn3c4C1Ak8MHKC9BySfp71UR97lfd9oHKPRbUhNEMUA5cmTdNc5QgiA-UoN8P3goFD1OZCwHKABWAcb0wlkcOfPpGIImBYQ4NQdWIAhEhgCBNxgSgP0xcizE4UwvF8fwAmgdhyRgAAZCBoiSAI0gyLJuzyItihLao6iaVoDHUBI0CHUVRlmF43g4A5ixBQoDzTHp3JQTz9FeJYAQQw9rPA8oECMnlYTgSFxIwJAv1UbF1w1MM3XKEokE3SwUpQNKCEy2ly1HMKFiWE0VyoyMORgT1vSDKqRw8mAvPqiizQjWU4pgWMZ0bdBHWdZNorTQzjOzXNYQAWTkEBUkxGBmNTajqBLHoAHIQv20p9v26Z9unWd0GO07fJskE2Msjj+3Q0sphClYvku8b5wmSY7vAztAvKOaeQWhAwFhAA5CAVrANaNq2xChpKUoLwARl4qYsIEqthLTHxgiDaAkAALzK-72we48ntQpxMYw7H+KfPH8IJom-RJ8nlhgAGnUTaaWLTGp6KgfHYXxxHAp21GYG416eiZh8Wbwj9yjEiSoCklzZPkxToGU3mqc7diuJ4xm72Z3ChLZ9XxNUSTsGkpI5IUtnDaXdAOHU7w-ECLwUHQAyjN8ZhTPSTJMHYtk-PKCppBffSX3qF9mhaJzVBc7pvrnY3BdTcoc4m6WUfBGAEvsUOAyDK60GymjcsalkyQpA1q7rH6GsokDCktGBuV5G4tHkIUwiLtB+tXUD1wVJUVWA81p5o8obk695tHrwHQOB8uQ99cG8xmmWSwr0OD-KMgfHDPPHpyDiYBe3ioqF4+01P-e1FzQug05X4fEhG+NM750wZm9S2ytrb4xXhzVIXMKZGxLijE+e8wDn1kqLX+2x-5iD8ibWmctzZgJxirG2atRL20ds7XWbslKUy9j7TSgRUoGWhDAAA4qOVk4dzJRyejHe6cc2HJzTvYUc2ca4-TzgFGa38-SQFziXWK-MILIFiBw+87cxpzk3soxu3dSQwE3DyDg6i1CaNrl3AaFoWoD1tKI0Yw9gCjxgOPSeTVS7yhgIqZUbiQIeIgkYzgpjWSRGwKoRIBhJoC2kULEG0I5LBKRqxIBp4ACQj8Lb2LUM+Co-QskAElpDPnRr2S8Z4nhmUyAaCsf0ng6AQKABs1TRyfSeFkqGo4-p7BgI0Z+20kL4IxljcB2ESFQJgITaccCeZ9ORiklCBCFZK1GZA225DNbaxkq7fWUAPaqW9p4X2WlsD-2wNweAupMjsNHCkCOFk778N2mmOyDQRFiJgbXIc7TRx8ximcWJLiJEKKPv48opU9SmNhN87q48dHOgJHlZuFyvSZFMYUqFNVAUd20b4xevcWrRkhQU6Q0xYW4sGkosuRFTHkplmXGlOVt4yORRC0cB8knUzArLRWfRiXFNKWeI2uCBnAIfoQnlfLyglLKQgkFt9TxoRvEQq2glxlERgCRWCYBKaIPmffIZSRlUQNVWsyZxMtbcx1XKvVZtXpGpWSashGsHZaydjrbZ7tKb7IYX7AIlgUDKggMkGAAApCAPJrmjECPUxpvCHlIOeVUSkDkWhZPEdi9AQ4znAH9VAOAEAEpQFmMS35-l-kF1EtOeR6BPINNzfmwtxbRyFOxIorlZcYIYjDTyUxlagzVrQLWnNlAG3QCbaMFtUSUAFAXu6QxxjIXjyHfWgtY6YDEsseGAofdAkmNHA6WdR4Z5eLnlOgoO9u1oFMQfWEm5fitpmiKtJiqLbENWWQ9VmqyKyqFk+hZqSDX2txqrESZrOYWvgbMygf774AavMMt9jqRJfh-H+YAAEf39PlQs+WCGVWsydRQ11VCPW0KNsYI8s7yg0tndYwuNoqh1soBi7q2aV2FrhdE8twJyiXsjYYA+okczhgfS-GDccXrNB6Eub1pg5OHMYQELwOauxelgMAbAZzCDxBktwyO0cE3lGkAGikKBO1wWFTEitxnuCZHM4fMTlL1TlHErZlAjI9AGFhJxwwTKAWudMx5-QZnP4Qw5eJmAwmqKQhsBEwwFRJOlig1ACLd6sGQg1WobAcXKjyyk6QkSzrKHur1mzci9CgA
