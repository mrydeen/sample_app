# SmartGridz application

This repository holds the smartgridz spring boot application.  This is a Linux based application that requires a MySQL backend. 

## Requirements

As mentioned, this application requires MySQL.  It will also need a mysql user setup:

    > username: spring
    > password: xyzzy

## Building the application

This application is maven based so you will have to have maven installed.  From the root folder, issue a:

    > mvn install

to build the application.  Once it is built, you can run the application.  Before running, you need to issue the 2 commands:

    > ./install.sh
    > sudo mysql < createDb.sql

## Run the application

We will use spring-boot to run the application:

    > mvn spring-boot:run

Once the application starts up, you can use a browser and point it to:

    > http://localhost:8080

By default, the application will add a user with Administrator permissions:

    > username: admin
    > password: xyzzy


