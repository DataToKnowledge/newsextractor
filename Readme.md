# News Extractor
----------------


## requirements

### Goose
In order to extract main article from web pages we use [Gravity Lab Goose](). It requires
as dependency [ImageMagic](http://www.imagemagick.org/) that should be installed as dependency.

## Setup Testing Environment

### Docker MongoDB

1. the project require an instance of MongoDB with the following configurations

```
  mongo {
    host = "mongodb"
    port = 27017
    dbName = "wheretolive"
    crawledNews = "crawledNews"
    controllers = "crawledWebSites"
  }
```

2. To run a mongo instance for testing we use a Docker file located in the project
[docker-images](https://bitbucket.org/datatoknowledge/docker-images) on bitbucket. Check the mongo docker image
for a detailed analysis. You need to build the wheretolive/mongodb images and then run the folliwing commands

    docker run -d -p 27017:27017 --name mongodb wheretolive/mongodb mongod --smallfiles

3. After, it requires that the file webControllers.bson is loaded into a database named wheretolive

    mongorestore --collection crawledWebSites --db wheretolive webControllers.bson


4. create an entry in /etc/hosts

        <ip_mongodb> mongodb

    such as
        127.0.0.1 mongodb

    This is because the application uses a symbolic name to connect to the mongodb istance.

after that we can package the applications



### Package application

1. To package the application we use [SBT Native Package](http://www.scala-sbt.org/sbt-native-packager/index.html).
In particular, now we used [JavaAppPackaging](http://www.scala-sbt.org/sbt-native-packager/archetypes/), but we
are going to explore also other packaging techniques based on [Docker, Native](http://www.scala-sbt.org/sbt-native-packager/gettingstarted.html#id4).

Currently we are packaging the app as JavaApplication but we will explore also [Akka Micro Kernel](http://www.scala-sbt.org/sbt-native-packager/gettingstarted.html).

2. To test the application we use the [Heroku Toolbet](https://toolbelt.heroku.com/) that should be installed.
  With this toolbet is deployed foreman which can be used to test the execution. The main script to be ran should be
  defined in the Procfile file.

 if the app works do the following commands:

    sbt compile stage
    foreman start worker


## Deploy environment

### Pre-Step only if the app is not already deployed

1. create the empty application **newsextractor** using [dokku-alt](https://github.com/dokku-alt/dokku-alt#create-only-application).

    dokku create newsextractor

    or remotely

    ssh -t dokku@datatoknowledge.it create newsextractor

2. use the [link plugin](https://github.com/rlaneve/dokku-link) to create a link to mongodb in the app to be deployed.

    - run the command
        dokku link:create newsextractor mongodb mongodb

    this is equivalent of placing **--link mongodb:mongodb** in a docker run.

    - check if the app has the link

        dokku link newsextractor


### deploy steps

1. add the remote dokku branch with the name of the app newsextractor

    git remote add dokku dokku@datatoknowledge.it:newsextractor

2. push the local branch to the remote dokku master. Let suppose that the local branch is develop the command should be

    git push dokku develop:master

3. check that the given url for the app is online. Basing on the example it should be dtk.datatoknowlde.it