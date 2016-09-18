# MFA

The repository contains a program to calculate maximum flows in flow networks. The project implements Dinic's augmenting path algorithm and Goldberg-Tarjan's push-relabel algorithm with a comprehensive GUI and several example networks.

A documentation about all classes is included in the subfolder /doc/ and can be accessed via https://christiangebhardt.github.io/MFA/

## To run the program:
* Checkout the GitHub project (https://github.com/ChristianGebhardt/MFA)
* Install a Java runtime environment (JRE) - minimum version required: 1.7.0
* Run the EXE-file 'MFA.exe' (double click or console command 'MFA.exe')
* Alternative: Run the JAR-file 'MFA.jar' (double click or console command 'java MFA.jar')

The screen should support a minimum resolution of 1200 x 800 to display the user interface correctly.
The user interface shows some help information at the start of the program ('Getting started'). They can be reloaded with the help menu item.
The program includes an example flow network that can be loaded.

## To develop the program with Eclipse:
* Checkout the GitHub project (https://github.com/ChristianGebhardt/MFA)
* Install a Java development kit (JDK) - minimum version required: 1.7.0, recommended version: 1.8.0
* Install Eclipse - minimum version required: 4.2 (Juno), recommended version: 4.6 (Neon)
* Open the project in Eclipse: 'Open project from file system ...' (to check)
* Resource stuff, ... (to check)

## Overview over the included folders:
* src:
    + the drone is started
    + values of the patternRecognition are received, interpreted and given to the three PD-Controllers
    + values of the PD-Controllers are received, interpreted and used to calculate the speeds and directions for actuating the desired movements
    + the drone is landed and shut down
* doc: a normal implementation of a PID-Controller where the constants are set via parameters
* lib: some example values for speed settings which worked well
* examples:
    + function that detects a (n x m) chessboard in an image and returns the coordinates of the left upper corner and the right bottom corner
    + the function uses 'findChessboardCorners' of the openCV library to find the chessboard
    + the chessboard is marked in the original image and is shown in a seperate window

**_Version:_** 1.0.1
**_Author:_** Christian Gebhardt
**_Date:_** 2016-09-03
