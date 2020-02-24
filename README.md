# DeltaCimApp

## QUICK START CONFIGURATION:

Go to the release section of this proyect, download the lastest version, and unzip the main file (cim-X.X.X.zip). The output folder has the following structure:

* Certificates: This folder contains an empty Keystore, by default its password is "changeit". In this Keystore either an encription certificate and an identity certificate for mutual authentication must be persisted.
* shapes: The file within this folder must contain the shacl shapes associated to the ontology of DELTA. In order the CIM to correctly function the file shall be named *delta-shapes.ttl*
* cim-X.X.X.jar: This is the compiled version of the CIM software.

Finally, to run the CIM, type down:

```` java -jar cim-1.0.0.jar --server.port=8080 ````

**WARNING**: The argument --server.port must always be specified for the correct functioning of the CIM.


