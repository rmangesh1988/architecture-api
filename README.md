# architecture-api

<b>Build :</b><br />
mvn clean install

<b>Start MySQL : </b><br />
docker-compose up -d

<b>To start the application run : </b>  
ArchitectureApiApplication.java


To get and overview/documentation of the api you can check out the swagger ui after starting the server at the below link,<br/>
http://localhost:8080/swagger-ui/
<br/>
 
For testing postman was used. 

<b>Functionality of the assignment</b> : <br/>
Idea is to find the building limit splits according to the overlapping height plateaus and the splits will have the height of the corresponding overlapping height plateau. Save all the entities.

- <u>Validation</u> : To find out gaps. We find out the building splits and then if the total area of the building splits != area of building limit, there is a gap. <br/>

- <u>Error handling</u> : A REST api is implemented with a centralized error handling using spring framework. Proper validation message is thrown with the correct http status. <br/>

- <u>Concurrency</u> : For concurrency, I have implemented a conceptual optimistic locking using versioning. A version will get out of date if concurrent updates are tried, indicating the user that he/she is working on stale data! <br/>

- <u>Testing</u> : Tests are extremely crucial for every application, but here for the purposes of assignment/demo I have added integration and unit tests for the core logic, important layers. In a real world scenario the test coverage would be no less than close to 100%. <br/>
  But the idea is tests would be in a similar fashion.

- <u>Deployment</u> : Deployment can be discussed :)
<br/>


