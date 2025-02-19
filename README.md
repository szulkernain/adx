# ADX API Service
## Initializing a new git repo
```
git init
git branch -m master main
git add-commit -m "Initial import
git remote add origin ssh://git-codecommit.us-west-1.amazonaws.com/v1/repos/adx-api-service
git push --set-upstream origin main
```
## Tech Stack
* Java 17
* Spring Boot 3.3.0
* Hibernate
* Liquibase
* MySQL 8.0.16
* Maven
* AWS 
  * Simple Email Service (SES)
  * Route53
  * CodeCommit
  * CodeBuild
  * Elastic Compute Cloud (EC2)
  * Simple Storage Service (S3)
  * Elastic Container Registry (ECR) 
  * Identity and Access Manamgement (IAM)
  * Virtual Private Cloud (VPC)
  * DynamoDB
  * Systems Manager - Parameter Store
## AWS CodeBuild Project
* Create the CodeBuild project so that it can build and push docker file to the ECR
* Add two environment variables - AWS_DEFAULT_REGION and AWS_ACCOUNT_ID - these are used in buildspec.yml
* Run it once, it will create a role with name something like: codebuild-build-adx-api-service-service-role and the first run will fail 
  * For this role, via AWS Console, add policy: AmazonEC2ContainerRegistryFullAccess
## Local MySQL Setup
```
CREATE DATABASE adx;
CREATE USER adx@localhost IDENTIFIED WITH caching_sha2_password  BY 'adx';
GRANT ALL PRIVILEGES ON adx.* TO 'adx'@'localhost';
CREATE USER adx IDENTIFIED WITH caching_sha2_password BY 'adx';
GRANT ALL PRIVILEGES ON adx.* TO adx;
```
## Testing the Application
Standard Maven phases in the build lifecycle, in order:
- validate
- compile
- test-compile
- test
- package
- integration-test
- install
- deploy 

And important to note is that when we run on of these phases directly, i.e. mvn test , it will run all preceeding steps too.

The Failsafe Plugin is used during the integration-test and verify phases of the build lifecycle to execute the integration tests of an application

mvn test -> will run the unit tests, and not the integration tests (or rather, not the tests ending in ‘IT’).

mvn verify or mvn integration-test will run just the tests specificed in the includes section of the maven-failsafe-plugin in the pom.xml .

mvn clean install -DskipTests -> will run the standard clean install, but passing in the skip tests argument, means it will skip the tests. This is only if you wish to not run the tests during the install phase.

## Running the application
### Pre-requisite
You have this file -  src/main/resources/application-dev.properties It contains appropriate values for following properties

If any of the properties in src/main/resources/application-dev.properties does not match or suit your need then simply override it by adding it to
application-dev.properties file
```
spring.mail.username=xxxx
spring.mail.password=xxxx
# App Properties
app.jwtSecret=yyyyy
#SQL Logging
# For pretty formatting
spring.jpa.properties.hibernate.format_sql=true
# To show sql statements
logging.level.org.hibernate.SQL=DEBUG
# For prepared statements (like update, insert, delete) to display the parameters
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```


## Running the application on command line / terminal

Run `mvn -D spring-boot.run.profiles=dev spring-boot:run`

## Building and testing the application via command line
mvn clean package


## Sign up as a new user - ROLE_SUBSCRIBER will be assigned to such user since no role is specified.
```
curl -X POST http://localhost:8080/api/auth/signup \
-H 'Content-Type: application/json' \
-d '{"emailAddress":"someone@somewhere.com","password":"player1", "firstName": "Steph", "lastName":"Curry"}'
```
## Sign up as a new user with specific roles
```
curl -X POST http://localhost:8080/api/auth/signup \
-H 'Content-Type: application/json' \
-d '{"emailAddress":"hello@awesomeworld.com","password":"player2", "firstName": "Klay", "lastName": "Thompson", "roles": ["ROLE_ADMIN","ROLE_EDITOR"]}'
```
Once user signs up then he/she receives a verify account email with a token that can be used to verify the account
## Verify the newly signed up user
```
curl -v http://localhost:8080/api/auth/verifyAccount/56dd5c7ffdd54adb
```
## Sign in
```
curl -X POST http://localhost:8080/api/auth/signin \
-H 'Content-Type: application/json' \
-d '{"emailAddress":"someone@somewhere.com","password":"player1"}'
```
## Resource Server can validate the token supplied by client (like browser or curl or mobile)
```
curl -X POST http://localhost:8080/api/auth/validateToken \
-H 'Content-Type: application/json' \
-d '{"token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXRpc2hAa2FtYXRrYXIuY29tIiwiaWF0IjoxNjQ4NzA1OTYzLCJleHAiOjE2NDg3OTIzNjN9.uZuh6-IeY3LctNd2jzgp3W-VbxWnRjwW8j8SgEIOcybbAxJ2sE15H_9U4RIhxN9y-w3TJbVVsHuo_TQ1cCh_uQ"}'
```
## Initiate "forgot password"
```
curl -X POST http://localhost:8080/api/auth/password/forgot \
-H 'Content-Type: application/json' \
-d '{"emailAddress":"someone@somewhere.comm"}'
```
User will receive an email with a token that can be used to reset the password

## Reset the password
```
curl -X POST http://localhost:8080/api/auth/password/reset \
-H 'Content-Type: application/json' \
-d '{"token":"77f27121154b45ab","password":"awesomePlayer"}'
```
## Sign in with new password
```
curl -X POST http://localhost:8080/api/auth/signin \
-H 'Content-Type: application/json' \
-d '{"emailAddress":"someone@somewhere.com","password":"awesomePlayer"}'
```
## How To Build  Docker Image and Store it in AWS ECR
### 1. Authenticate with AWS ECR
```
aws ecr get-login-password --region us-west-1 | docker login --username AWS --password-stdin 123456789012.dkr.ecr.us-west-1.amazonaws.com
```
### 2. Build the Docker image
```
docker build -t adx-api-service .
```
### 3. Tag the Docker image
```
docker tag adx-api-service:latest 123456789012.dkr.ecr.us-west-1.amazonaws.com/adx-api-service:latest
```
### 4. Push the Docker image to AWS ECR
```
docker push 123456789012.dkr.ecr.us-west-1.amazonaws.com/adx-api-service:latest 
```
## Setting up MacBook
1. Download and install IntelliJ IDEA Ultimate edition
2. Get the license for it from IT (KACE ticket)
3. Request IT to install it on your computer (KACE ticket)
4. From within IDEA, install Amazon Coretto, JDK 21
5. From command line confirm java is installed by running: java -version
6. Install Homebrew
   1. mkdir ~/homebrew
   2. curl -L https://github.com/Homebrew/brew/tarball/master | tar xz --strip 1 -C ~/homebrew
   3. Edit ~/.bash_profile and add this line: export PATH=$HOME/homebrew/bin:$PATH
   4. source ~/.bash_profile
   5. Confirm brew is installed by running brew --version
7. Install Maven
   1. https://www.digitalocean.com/community/tutorials/install-maven-mac-os
   2. Edit ~/.bash_profile and add this line: export PATH=$HOME/homebrew/bin:$PATH
   3. source ~/.bash_profile
   4. Confirm maven is installed by running: mvn -version
8. Install Docker (Not yet possible due to IT restrictions…)
   1. https://docs.docker.com/desktop/setup/install/mac-install/