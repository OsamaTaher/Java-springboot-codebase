# common-user-management using routes
**Overview**

This project is a robust Spring Boot application featuring user management services 
designed to control user access dynamically. 
Each API endpoint is treated as a privilege that can be assigned to user roles, 
providing a fine-grained security model. It incorporates JWT (JSON Web Token) for secure and stateless authentication, 
alongside dynamic access checks for API security. 
Additionally, it utilizes @SendNotificationOnSuccess annotation to facilitate event generation upon the successful completion of annotated actions.

- on startup the system will extract all defined routes and save to DB.
- each role has a set of allowed privileges that can be modified as needed.
- default admin password will be printed on logs for the first time when there is no old admin account.
- for performance reasons roles and its privileges are cached in memory to avoid hitting DB when checking access.
- change role cache implementation by implementing "RoleCache" interface
- in case of multiple controllers method with same name, a WARNING will be printed in logs, you can ignore this message if you rename privileges in DB later.

#Features
* User Management: Granular control of user access via roles and privileges (routes).
* Dynamic Access Check: Runtime validation to control access to resources dynamically.
* Event Generation: @SendNotificationOnSuccess annotation for automatic event generation upon action completion.
* JWT Security: Secure and stateless authentication.

# Getting Started
**Prerequisites**
* Java Development Kit (JDK) 17 or higher
* Maven or Gradle build tool

 # technology
    - springboot: v3.2.1
    - JPA
    - AspectJ
    - JWT

# Installation
1. Clone the repository:
   `git clone <repository-url>`
   `cd <repository-directory>`
2. Run the application: `./mvnw spring-boot:run`

# Configuration
The main configuration settings are located in the application.properties file
- default active profile = **dev** 

# Event Handling
The @SendNotificationOnSuccess annotation is used to generate events on the completion of specific actions. 
Make sure to define relevant EventListeners and notification services.

# Usage
* API Endpoints: Manage users, roles, and privileges through RESTful API endpoints.
* Event Generation: Annotate methods with @SendNotificationOnSuccess to trigger events.
* Role-based Access Control: Assign roles to users and map API endpoints as privileges to enforce access control dynamically.