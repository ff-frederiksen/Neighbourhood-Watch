# Neighborhood Watch Alarm (NWA)
NWA is an open source neighborhood security movement with an alarm system that anyone can assemble and manage locally. NWA is published under the [MIT License](https://github.com/ff-frederiksen/Neighbourhood-Watch/blob/main/LICENSE).

The project consits of two seperate entities with one working on the TTN (The Things Network) stack and the other a self-hosted Chirpstack server. 



The client-server system consists of Arduino-based **alarms** outfitted with sensors, and a Raspberry Pi **server**. The devices communicate via LoRa signals to a  LoRa Gateway which then relays messages between the devices and the server. When an intrusion is detected the server sounds out a text-message alerting the neighbors of the intrusion.

For general and installation related information, please see the [**main site**](https://neighbourhood-watch-lora.herokuapp.com/).

For system details and architecture descriptions, please see the [wiki](https://github.com/ff-frederiksen/Neighbourhood-Watch/wiki).

# Code Status

Accepted source code for the server and the website goes through a **Continues Integration** pipeline using *Github Actions*.

The current status for the different pipelines can be seen below:

Server status:

[![Java/Maven Chirpstack CI](https://github.com/ff-frederiksen/Neighbourhood-Watch/workflows/Java/Maven%20Chirpstack%20CI/badge.svg)](https://github.com/ff-frederiksen/Neighbourhood-Watch/actions?query=workflow%3A%22Java%2FMaven+Chirpstack+CI%22)

[![Java/Maven TheThingsNetwork CI](https://github.com/ff-frederiksen/Neighbourhood-Watch/workflows/Java/Maven%20TheThingsNetwork%20CI/badge.svg)](https://github.com/ff-frederiksen/Neighbourhood-Watch/actions?query=workflow%3A%22Java%2FMaven+TheThingsNetwork+CI%22)

Frontend status:

[![Node.js site/frontend CI](https://github.com/ff-frederiksen/Neighbourhood-Watch/workflows/Node.js%20site/frontend%20CI/badge.svg)](https://github.com/ff-frederiksen/Neighbourhood-Watch/actions?query=workflow%3A%22Node.js+site%2Ffrontend+CI%22)

Backend status:

[![Node.js site/backend CI](https://github.com/ff-frederiksen/Neighbourhood-Watch/workflows/Node.js%20site/backend%20CI/badge.svg)](https://github.com/ff-frederiksen/Neighbourhood-Watch/actions?query=workflow%3A%22Node.js+site%2Fbackend+CI%22)


# Current Project Team Members
* [mikkelgthang](https://github.com/mikkelgthang)
* [simoneengelbr](https://github.com/simoneengelbr)
* [ff-frederiksen](https://github.com/ff-frederiksen)
* [s184186](https://github.com/s184186)
* [SebastianLJ](http://github.com/sebastianlj)
