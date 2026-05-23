# testmu-sdet2-hiteshjaiswal

# TechStack 
- java 
- maven
- selenium
- RestAssured
- TestNG
- ExtendReports

# framework architecture
- ui
- api
- integration
- pom designs

# planned feature
- cross browser 
- retry
- remotewebdriver launch
- ci/cd pipeline integration

# folder structure
- main
- helper
- base
- constant
- listener
- pages
- report
- utils

- resource/
- testdata
- project.properties
- 
- test/
- ui
- api
- integration

# Run Structure
- the xml file will be set as env variable in the pom
- can be run with comman mvn clean test -DsuitexmlFile=<filename>
- will try to create a selenium grid for this purpose and provide remote url to launch everything parallely in the pipeline
- will have a json server launch at the time on installing this project can be used to run the api test as well
  
