# A sample JDBC DAO

This project is a sample program with a JDBC DAO to access an H2 database. 

Usage :

  * Execute `StartH2` to launch a H2 server and create the database.  
  * Execute `App` to test  the DAO (see source code for DAO usage).

## Entities

Two entities are manipulated `Chien` and `Personne` both implements the  interface `Entity` to ensure that they provide access to a long id.  

## DAO

The simple DAO is defined in the generic interface `DAO<E extends Entity>`, common parts (`find`, `findAll`, `persist`, `remove`, `update`) are implemented in `AbstractDAO`. 
Concrete DAOs for each entity (`chienDAO`and `personDAO`) extends `AbstractDAO` and call methods from the superclass after setting the specific parameters.   

## Datasource

The connections to the database are managed by `DBCPDataSource`, parameters are set by system properties (see `resources/app.properties`).
