
# DeltaCimApp

## QUICK START CONFIGURATION:

Go to the release section of this proyect, download the lastest version, and unzip the main file (cim-X.X.X.zip). The output folder has the following structure:

* Certificates: This folder contains an empty Keystore, by default its password is "changeit". In this Keystore either an encription certificate and an identity certificate for mutual authentication must be persisted.
* shapes: The file within this folder must contain the shacl shapes associated to the ontology of DELTA. In order the CIM to correctly function the file shall be named *delta-shapes.ttl*
* cim-X.X.X.jar: This is the compiled version of the CIM software.
* modules: this folder contains the interoperability modules that allow integrating non-Delta local infrastructures with the DELTA platform, so they can exchange data transparently with the rest of the components in this platform.

Finally, to run the CIM, type down:

```` java -jar cim-1.0.0.jar --server.port=8080 --certificate.password=changeit````

**WARNING**: The argument --server.port must always be specified for the correct functioning of the CIM.

**WARNING**: Optionally, if CIM is already correctly configures the flag --xmpp.autoconnection=true can be used for the CIM connect to the Peer-to-Peer network automatically without requiring the user to manually connect it.

