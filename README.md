# LLC

This is LLC Restful Service that distributes payouts to shareholders based on defined set of split rules.

## Technical

### Pre Requirements
- Java 15
- Postgres 12

### Database

#### Creating a database - SQL
```sql
CREATE USER llc WITH PASSWORD 'llc';

CREATE DATABASE llc;

ALTER USER llc WITH SUPERUSER;
ALTER DATABASE llc OWNER TO llc;
GRANT ALL PRIVILEGES ON DATABASE llc TO llc;
```

### Docker
#### Run Docker Dockerfile
```shell script
docker image build -t llc-db .
docker image build -t llc-server .
docker-compose up
```

### Running application (one of the following)

#### Run Options
##### As Maven
- Navigate to `<repo-folder>/llc`
- Compile project `mvn clean install`
- Navigate to `<repo-folder>/llc/challange-llc/challange-llc-server`
- run: `mvn spring-boot:run -Dspring-boot.run.profiles=${springProfile}`

##### As Java Application    
- Navigate to `<repo-folder>/challange-llc/challange-llc-server`
- Find `Application.java`
- Run with a profile

##### As Jar
- Navigate to `<repo-folder>/llc`
- Compile project `mvn clean install`
- Navigate to `<repo-folder>/llc/challange-llc/challange-llc-server`
- run: `java -jar -Dspring.profiles.active=${springProfile} server.jar`

###### Lombok

This is a plugin to help avoid boiler plate in the code. 

Site: https://projectlombok.org/

Git: https://github.com/mplushnikov/lombok-intellij-plugin#installation


### Using the Application
- Download [Postman](https://www.getpostman.com/);
- Import Postman collections from `<repo-folder>/challange-llc/misc/doc/api/LLC.postman_collection.json`;
- Configure your hostname for where the service is running;
- Use the Valid and Invalid calls to use the system.

### Api Documentation
The Api documentation is done using swagger. Access http://localhost:8885/llc/swagger-ui/ for trying out.


## Architecture
### Layering
The application is organized as a modularized N-Tier layering. This means that each layer is, at a degree, independent of the lower 
layer, each concept is packed and modularized into a specific maven module structure. 

| Layer | Description |
|:---:| ------------- |
| **Domain** | Responsible for all definitions of the persistence level. Configurations for the entities and data management is defined in this level. |
| **Service** | Responsible for handling all business specific rules that matters for the application. _See note below for extended definition._ |
| **Controller** | Responsible for grouping intermediate classes, responsible for handling View requests of actions with a Restful Implementation. |
| **Dto** | Responsible for all payload (API contract definition) that wild be used in the controller for the exchange of request/response.  |
| **Server** | Responsible for the spring boot configuration required to start the environment. Contains mainly definitions of the application. |

#### Service Layer
The service layer is represented by two modules:
- generic service: Responsible for handling all service data retrieval and conversions for the Database and API levels
- distributor: Responsible solely for calculating the distributions of payouts.

##### Distributor Module
This module was build as a self-contained library that could be extracted and modified at any time without any knowledge 
of the system using it and providing all that is required to the caller to know how to manage the distribution computed.

The implementation is simple and focused on the required use case. However a few problems can be considered:
* Rules depends on business requirements and follow a program format. This makes logic less maintainable by owners (Business Owners) and more in the developers realm
* Class coding has limitations for permutations and could lead to major bugs. Instead of this another option could be using:
    * BPM based processors like Activiti or Camunda which are rule based workflows and could be easily edited and maintained by owners
    * Spring State Machine which is aligned with the BPM based processors, where is still maintained mainly by developers in the code base
* Another downside is that the implement relies heavy in loaded objects, this could generate a major memory footprint where
  eventually the Garbage Collector will have more failures and the hosting machine will require more scaling (vertical and horizontal)
  to handle the processing

### Componentization
In the maven module `generic`, it contains a list of components created as identifiable pieces to be reusable.

#### Component :: Domain
Rationale: Centralize persistence level configurations that should be followed by Domain modules in applications 

This module contains:
* Abstract Entity implementation that all tables require in the application such as 
    * UUID : External identifier that can be shared with caller
    * Create Date : Immutable time that row was created
    * Updated Date : Mutable time that row was changed
    * Version : In order prevent the lost update and ensure that data integrity (Mostly used on Optimistic Locking) where the oldest update is committed and newest is discarded with exception
* Changelog this contains general database changes that are required to configure properly the DB instance 

#### Component :: Exception
Rationale: This is a utility module that allows handling of exceptions and configurations for them.

This module contains:
* Enums with the capability of creating exceptions identified by the description added in the constructor.

#### Component :: Exception
Rationale: This is a utility module that allows handling of logs and configurations for them.

This module contains:
* Logback basic configuration

#### Component :: Controller
Rationale: Centralize all Rest Configuration needed in the application, so it is homogeneously configured in all applications.

This module contains:
- Spring boot Rest Controller configuration
- Swagger Documentation configuration to generate accessible api documentation
- Spring's Aspect J Exception Advisor to process HTTP errors
- Error Dto that is a generic error payload for api returns upon exceptions that are processed on the item above
- Mostly used Http Exceptions recognizable in the Exception Advisor for further mapping into Error Dto
- Mappers where Error Dto is created and a customized implementation of JsonMapper containing required configurations to process payload 

#### Component :: Server Filter
Rationale: This is a utility module to centralize all Server configurations needed in order better trace, secure or monitor all applications.

This module contains:
- Server Filter Config that adds a simple identifier that is propagated in the MDC in order to trace calls and correlate logs
- Spring Security with accessibility configuration to the exposed APIs  


### Performance and Scalability

#### Application
The main point in here is to explore the module Distributor whereas the major cost of computing exists.

As mentioned before the downside of the current implementation is the amount of data loaded in memory. A few solutions to it could be:
* Batch processing
    * Mostly the payout would be done per group that would have a specific characteristic allowing the split to process
    * Although it improves average case, there could still be spikes were a given group is too big and would require major processing
* Per unit distribution
    * Yet on the same page as the batching, the rules and required context of split could be saved for further processing. Since
    each unit (person or shareholder) is self-contained, the footprint in memory would be around the same amount of the batch size
    * The same way this approach resembles NO-SQL, if a group needs an update, there will be a cost to update all self-contained units
    so the processing is done properly. 

#### Database
The main long term issue on the data is the frequency of payouts. Depending on the amount of data that requires to be
loaded and updated in a given pace, this could create clogs in the system.

Ways to improve performance on DB could be:
* Indexing
    * This is the most basic approach to speed up reads in the DB
    * Downside is mainly in cases of reindexing and the quality of the index when doing a DB Analyze.
* Replication
	* This would mean a full duplication of the DB in a master-slave format.
	* Downside, master gets overloaded with lots of writes, therefore slows down replicas.
* Clustering a table on index `CLUSTER booking USING <index>;`
	* The good side is that this will improve the speed of readings in a table were data doesn't change too often. The
	reason is related to the fact cluster organizes data ordered by index, therefore allow B-Tree search;
	* On the other hand, cluster takes a toll on highly insertable tables, as decreases "wiggle room" for HOT (Heap-only tuples).
	It is possible also to happen Last Page Insert Latch Contention, which is a single place at the end of B-Tree, where multiple 
	queries compete against each other
* Partition: 
	* which would be a type of vertical scaling. On this we split columns of a given table into 2 or more, depending on the need.
	This allows for example search from UUID not be impacted by the availability.
	* The not so good, the data will grow independently, but for 2 different tables. Queries adds more cost on joins.
* Sharding:
	* which consists in horizontal scaling. We splint the rows of a given table in multiple nodes. This allows specific,
	information per region/node.
	* Problems from vertical partition might happen. Different region/node creates complexity on joins and aggregations as
	well as the stacked time due to network calls
	
Materialized Views
It's a table which would contain the information pulled by a query. The good part of having it is that high demanding queries that 
rely in different aggregations could be improved by having it's data stored in a table where it's content reflects the needed query.
Materialized views can be indexed and refreshed. 
This approach goes well with Command Query Responsibility Segregation (CQRS), wheredifferent actions (Create, Update, Delete) goes 
in a different flow from the Read.

On the hand, a high demanding insert scenario would force the materialized view to be refreshed with frequency which would increase the load
of the DB for each time it is refreshed. Also in a sharded environment, the materialized view wouldn't cover all data.


#### Memcached / Redis
In-memory caching could improve search for availability, avoiding hitting the DB too many times.
They could scale in the same way as the points above for the DB.

It's important to avoid state during horizontal scaling of machine depending on then. The best way is to have In-memory detached
from the running application, eg, different docker containers or EC2 machines. The biggest concern is to avoid stateful situations
were data changes in one environment and it's outdated in another. 