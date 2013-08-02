Skye 
=
Information Lifecycle Management
-

The Skye framework is a set of tools to handle the management of both metadata and process around an information lifecycle process.  This process tracks from discovery,  through to ingestion and storage.

The aim of the platform is to then leverage the range of intelligent metadata that has been captured and allow users to quickly search and analyze data, documents and artifacts.  

Through the course of this lifecycle we will then be able to take a "slice" of the information for archiving,  manage the storage in the archive through legal storage management to destruction.

Building
-

[![Build Status](https://travis-ci.org/infobelt/skye.png?branch=master)](https://travis-ci.org/infobelt/skye)

In order to build the Skye platform you will need to have:

* Oracle JDK 7
* Maven 3.x

You should then be able to simply run:

     mvn clean install

Running
-

In order to get a basic server running simply build, then

     java -jar service/target/skye-service-0.0.1-SNAPSHOT.jar server service/skye.yml

Once the server is running you should be able to visit [http://localhost:8080/explore/index.html](http://localhost:8080/explore/index.html) to see the REST endpoints for the running instance.

![screen](https://raw.github.com/infobelt/skye/master/screenshot.png)

Getting Involved
-

The Skye framework is open source and licensed under an Apache 2 license.  We are always looking for people to get involved,  if you are interested please contact info@infobelt.com or take a look at the issues, fork and submit a pull request.

