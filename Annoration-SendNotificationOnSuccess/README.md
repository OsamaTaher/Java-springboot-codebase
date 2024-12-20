# @SendNotificationOnSuccess Annotation
## Description:
> The @SendNotificationOnSuccess annotation is used to trigger an event upon the successful execution of a method. It provides an automated way to generate notifications or events when specific actions are completed. This annotation is useful when you want to write cleaner more abstracted code.

## Usage:

- Apply the @SendNotificationOnSuccess annotation to any method where you want to trigger an event or notification upon successful execution.
- Ensure that relevant EventListeners and notification services are properly implemented and configured to handle the event and send the notification.
- Customize the code as necessary for your application's requirements (find comments on code to show you where to add your handler).
- you can copy files in 'annotation.sendNotificationOnSuccess.aspect' package to any java project you need to use it on,
  you can find a usage with spring boot in [common-dynamic-management-services project](https://github.com/OsamaTaher/Java-springboot-codebase/tree/main/springboot/common-dynamic-management-services) .

