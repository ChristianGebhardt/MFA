# MFA

The repository contains a program to calculate maximum flows in flow networks. The project implements Dinic's augmenting path algorithm and Goldberg-Tarjan's push-relabel algorithm with a comprehensive GUI and several example networks.

A documentation about all classes is included in the subfolder <em>doc</em> and can be accessed via the website https://christiangebhardt.github.io/MFA/.

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
* <em>src</em>: The source files of the code and ressource files for the program
    + package `de.lmu.ifi.mfa` contains the model with the implementation of the maximum flow algorithms
    + package `de.lmu.ifi.mfa_gui` contains a graphical user interface to manipulate the model and apply the maximum flow algorithms
    + class `Main.java` is the main class of the program that starts the program
    + folder <em>resources</em> contains an example flow network and icons for the program
* <em>doc</em>: The javadoc documentation of the packages and classes
* <em>lib</em>: The 'JGraphX Swing Component - Java Graph Visualization Library' for the flow network visualization
* <em>examples</em>: Some template flow networks to test the maximum flow algorithms
    + default example of the thesis (total flow: F=7)
    + TODO
    + TODO

**_Version:_** 1.0.1
**_Author:_** Christian Gebhardt
**_Date:_** 2016-09-03

***

## Further Information:

The MFA-project is part of the bachelor thesis *"Efficient Transport System Scheduling based on Maximum Flow Algorithms"* by *Christian Gebhardt* submitted at the *Institute of Informatics* at *LMU Munich* (submission date: 2016-09-27).

The thesis can be accessed via the link https://TOBEDONE.
