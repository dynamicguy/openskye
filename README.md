OpenSkye 
=
Information Lifecycle Management
-

The Open Skye framework is a set of tools to handle the management of both metadata and process around an information lifecycle process.  This process tracks from discovery,  through to ingestion and storage.

The aim of the platform is to then leverage the range of intelligent metadata that has been captured and allow users to quickly search and analyze data, documents and artifacts.  

Through the course of this lifecycle we will then be able to take a "slice" of the information for archiving,  manage the storage in the archive through legal storage management to destruction.

Building
-

[![Build Status](https://travis-ci.org/openskye/openskye.png?branch=master)](https://travis-ci.org/openskye/openskye)

In order to build the Skye platform you will need to have:

* Oracle JDK 7
* Maven 3.x

You should then be able to simply run:

     $ mvn clean install

Running
-

In order to get a basic server running simply build, then

	 $ chmod 755 assembly/target/appassembler/bin/skye-server
     $ assembly/target/appassembler/bin/skye-server assembly/target/appassembler/etc/skye.yml

If you have [Foreman](http://ddollar.github.io/foreman/) then you change just start that and it'll take care of the chmod.

Once the server is running you should be able to visit [http://localhost:5000/explore/index.html](http://localhost:8080/explore/index.html) to see the REST endpoints for the running instance.

![screen](https://raw.github.com/infobelt/skye/master/screenshot.png)

Interacting with the Server
-

With the server up you can start interacting with it using the skye command line tool.  First make sure that it is executable.

	 $ chmod 755 assembly/target/appassembler/bin/skye

Then you can login using the default username and password

	 $ assembly/target/appassembler/bin/skye login --username admin@openskye.org --password changeme
	 Logging in as admin@skye.org at http://localhost:5000/api/1/
	 Login successful, storing credentials
	 $

This will set-up a your API key in your local skye settings (~/.skye/settings.json)

Then you can use the commands to interact with the server, ie.

	$ assembly/target/appassembler/bin/skye login domains --list
	Listing domains


	-------------------------------------------
	|id                                  |name|
	-------------------------------------------
	|F06FE161-DC34-4A6F-AED7-BFF203DB4DDF|Skye|
	-------------------------------------------

	Found 1 domains
	$

Getting Involved
-

The Open Skye framework is open source and licensed under an Apache 2 license.  We are always looking for people to get involved,  if you are interested please contact info@infobelt.com or take a look at the issues, fork and submit a pull request.

