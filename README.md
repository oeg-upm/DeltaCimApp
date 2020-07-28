
# DeltaCimApp

The CIM allows local infrastructures to communicate with others though a peer-to-peer network. Besides allowing the access of remote infrastructures, the CIM implements a semantic interoperability layer, which translates heterogeneous payloads into JSON-LD modelled with the DELTA ontology by means of interoperability modules. The translation is bidirectional, therefore, when needed the CIM also translates from JSON-LD to a set of heterogeneous formats that follow different  models. This potentially leads to having systems developed with different standards communicating transparently.

Additionaly, the CIM allows to consume the data of a local infrastructure using SPARQL queries, or, consume the data from the cloud, i.e., the peer-to-peer network, with SPARQL queries. Other functionalities implemented are: validation of payloads using SHACL shapes on the fly, access control list, jwt token authentication for local infrastructures to interact with the CIM, a GUI for configuring the CIM, and a documented REST API to use the CIM.

## Monitoring capabilities:

**All the current CIM releases send data about the status of the service to a private secured monitor service, that analyses the health of the services and prevents from potential attacks**

## Running the CIM:

Go to the release section of this proyect, download the lastest version, and unzip the main file (cim-X.X.X.zip). The output folder has the following structure:

* Certificates: This folder contains an empty Keystore, by default its password is "changeit". In this Keystore either an encription certificate and an identity certificate for mutual authentication must be persisted.
* shapes: The file within this folder must contain the shacl shapes associated to the ontology of DELTA. In order the CIM to correctly function the file shall be named *delta-shapes.ttl*
* cim-X.X.X.jar: This is the compiled version of the CIM software.
* modules: this folder contains the interoperability modules that allow integrating non-Delta local infrastructures with the DELTA platform, so they can exchange data transparently with the rest of the components in this platform.

Finally, to run the CIM, type down:

``` java -jar cim-1.0.0.jar --server.port=8080```

**WARNING**: The argument --server.port must always be specified for the correct functioning of the CIM.

Other flags can be specified to modify the default behaviour of the CIM replacing the values within the characters [ and ] inclusive. For instance, the flag ```--spring.datasource.username=[USER]``` should be instantiated as ```--spring.datasource.username=toor```. The list of flags that can be configured are:

* Change the password to connecto to the certificates key store, default password is *changeit*:
	* ``` --certificate.password=[PASS]```
* When the CIM is already correctly configured, the following flag can be used for the CIM connect automatically to the Peer-to-Peer network without requiring the user to manually connect it. 
	* ``--xmpp.autoconnection=true``
* Change default username/password for the local database where the configuration of the CIM is stored, default value for username and password is *root* .
	* ```--spring.datasource.username=[USER]```
	* `` --spring.datasource.password=[PASS]``
* Change the time outs of the CIM requests, all of them are expressed in milliseconds:
	* The timeout that the CIM has to answer a local request, that will be forwarded through the peer-to-peer network in order to be solved by another CIM ```--spring.mvc.async.request-timeout=[TIMEOUT]```
	* The request timeout of the requests sent from the CIM to the local infrastructures when solving a remote request sent by another CIM```--cim.timeout.request=[TIMEOUT]```
	* The socket timeout of the requests sent from the CIM to the local infrastructures when solving a remote request sent by another CIM ```--cim.timeout.socket=[TIMEOUT]```
	* The timeout of the requests sent through the peer-to-peer ```--cim.timeout.xmpp=[TIMEOUT]```


## Configuring & using the CIM
In order to correctly configure and use the CIM, the user is kindly asked to check the [CIM's wiki]([https://github.com/oeg-upm/DeltaCimApp/wiki](https://github.com/oeg-upm/DeltaCimApp/wiki))

