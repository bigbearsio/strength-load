# strength-load
Load Testing for Strength project

## Prerequisite
* Java 8 `brew tap caskroom/versions; brew cask install java8`
* Note: Gattling 2.3 only works on java8. https://gatling.io/docs/2.3/quickstart/

## Install Gatling
For now, just download the whole distribution via command line

1. Download gatling zip bundle [here](https://repo1.maven.org/maven2/io/gatling/highcharts/gatling-charts-highcharts-bundle/2.3.1/gatling-charts-highcharts-bundle-2.3.1-bundle.zip)
2. extract to ~
3. Rename `gatling-charts-highcharts-bundle-2.3.1-bundle` directory to `gatling`

## How to run gatling

Copy all gatling scripts here to `~/gatling/user-files/simulations`
```
cp *.scala ~/gatling/user-files/simulations
```

Run Gatling on the simulation class.
```
# ~/gatling/bin/gatling.sh -s <classname>  
~/gatling/bin/gatling.sh -s BearStrengthSimulation                                
```
Or you can simply ~/gatling/bin/gatling.sh and it will 

After the run is finished. a link to an HTML report is printed out 

## Diagrams
[Diagram](diagram.png)
