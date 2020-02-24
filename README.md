# DeltaCimApp

# QUICK START CONFIGURATION:

Go to the release section of this proyect and download the lastest version. Following unzip the files, the output folder has the next structure:

* Certificates: This folder must contain a Keystore that has the encryption certificate and the certificate for the mutual authentication.
* Shapes: This folder must contain a ttl file that is available throught this link: https://github.com/oeg-upm/DeltaCimApp/tree/master/shapes

Finally, to run the CIM, type down:

```` java -jar cim-1.0.0.jar --server.port=8080 ````

**WARNING**: The argument --server.port must always be specified for the correct fucntioning of the CIM.


