# common-user-management
common services to manage users and roles/privileges using alreafy defined routes as security privileges with simple demo project

- on startup the system will extract all defined routes and save to DB.
- each role has a set of allowed privileges that can be modified as needed.
- default admin password will be printed on logs for the first time when there is no old admin account.
- for performance reasons roles and its privileges are cached in memory to avoid hitting DB when checking access.
- change role cache implementation by implementing "RoleCache" interface
- in case of multiple controllers method with same name, a WARNING will be printed in logs, you can ignore this message if you rename privileges in DB later.

 # technology :
    - springboot: v3.2.1
