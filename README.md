# Ilmo Rest API

A rest API for the data model of www.github.com/Solita/Ilmo

## Prerequisites

For development you can use the in memory database.
To use oracle you could set up a local maven repo.. A bit painful.

    mvn install:install-file -DcreateChecksum=true -Dfile=ojdbc14.jar -DartifactId=ojdbc -Dversion=1.4 -DgroupId=oracle -Dpackaging=jar -DlocalRepositoryPath=maven_repository    


You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## Creating a war for a web container

    lein ring uberwar
