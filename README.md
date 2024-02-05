# reconciliation-service

**Attention! This is the legacy code for the legacy service SMEV2. This code is published as an example.**

The reconciliation service is a service that performs the tasks of reconciling payment data (accruals). The service takes data from the automated system of the Federal Treasury of Russia and compares it with the data that is available in the database of the local information system. All exchange is carried out via the SOAP protocol.

The application has the following main-level subsystems:
 * JINN Adapter - SOAP client for the crypto service (all messages must be signed and verified with GOST3410 algorithms).
 * SMEV2 Adapter - SOAP client to the main treasury system through the Interdepartmental Interaction System version 2.

SOAP interaction is implemented using SpringWS. Database stack: ojdbc8/DATA JPA/Hibernate.
