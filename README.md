# Mancala Game
Mancala Game, is the backend & frontend application that implements the traditional strategy board game most known as Kalaha/Mancala.

This repository contains the source code for the Mancala Game API service (SpringBoot), and the frontend.

## Table of Contents
- [MANCALA GAME](#mancala-game)
    * [Table of Contents](#table-of-contents)
    * [Prerequisites](#prerequisites)
    * [Technologies](#technologies)
    * [Implementation And Consumption](#implementation-and-consumption)
    * [Installation](#installation)
        * [Build From Source](#build-from-source)
        * [Running Tests](#running-tests)
        * [Running Application](#running-application)
    * [Documentation](#documentation)
    * [Related Repositories](#related-repositories)
    * [Reporting an issue](#reporting-an-issue)
    * [Contributing](#contributing)

## Prerequisites

* Java 11+

## Technologies

### Backend
* Java
* SpringBoot

### Frontend
* HTML
* CSS
* JavaScript
* jQuery

## Implementation And Consumption
To make any implementation of the Game if needed, the Developer just need to implement the interface "GameService" and make the changes as needed.
The Developer also have the possibility to create another clients and consume the API by invoking the available endpoints: 
* **POST** */game/start* : To create a new game, it's returned a JSon response with the game details
* **POST** */game/connect* : To connect to a specific existing game by providing the Player2 details and the Game ID to join
* **POST** */game/connect/random* : To connect to any existing randomly game by providing the Player2
* **POST** */game/gameplay* : To make moves


## Installation

### Build from Source
Ensure you have JDK 11 (or newer), Maven 3.5.4 (or newer) and Git installed

    java -version
    mvn -version
    git --version

First clone the repository:

    https://github.com/MozThinker/Mancala.git
    cd mancala

Install Dependecies:

    mvn clean instal -DskipTests

To build Mancala Core:

    mvn clean package

This will build the java artifact's and run the testsuite.

### Running Tests
To run tests:

    mvn clean test

### Running Application
To run the Application:
* Run the Application found in 
  

    src/main/java/com/mutombene/mancala/MancalaApplication.java
    
* Open browser and go to http://localhost:8080/ or http://localhost:8080/index.html
* Open another window of the browser for Player 2


## Documentation



## Related Repositories

| Name | URL |
| ------ | ----- |
| Mancala Game API | <https://github.com/MozThinker/Mancala/mancala-api-spec> |

## Reporting an Issue
Issues should be reported through Repository issue tracking and/or pull-request with the fix and the issue description.

## Contributing
Before contributing please make sure you are aware of the Development Best practices.