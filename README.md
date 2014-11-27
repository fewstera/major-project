#Directories

* **app.apk** is the final applications release APK, that can be installed onto an Android device.

* **awf1_FinalReport_2014.pdf** is the final report PDF

* **icon.psd** the PSD for the icon of the system

* **documentation/** contains the source files for the all documentation created

* **implementation/InjectableMedicinesGuide** contains the source for the final application including all test packages

* **implementation/clientstore-password.txt** contains the password for the keystore

* **javadoc/** contains the JavaDoc generated from the implementations comments

* **plan/** this contains the Gantt chart used

* **prototypes/** contains the prototypes that were created, to help complete the project.

* **testResults/** this contains the rest results for the unit tests and integration tests.

#Installing the application

Enable the running of non-marketplace applications on the Android device (http://www.androidcentral.com/allow-app-installs-unknown-sources)

Transfer the APK onto the device. (http://apkinstall.com)

Launch the application on the device

#Building the application

Within the terminal:

````
cd implementation/InjectableMedicinesGuide
./gradlew assembleRelease
````

The APK can then be found within:
**implementation/InjectableMedicinesGuide/app/**

#Runnings the tests

To run all Unit and instrument tests execute the following in terminal:

````
cd implementation/InjectableMedicinesGuide
./gradlew connectedInstrumentTest
````

#Libraries and licences

Robospice Library - https://github.com/stephanenicolas/robospice - APACHE 2 LICENCE

GLYPHICONS - GLYPHICONS.com - Creative Commons Attribution 3.0 Unported (CC BY 3.0)
