## Openshift commands to create the project

* oc new-project dev

### robotic user
* oc create sa robot
* odam policy add-role-to-user system:authenticated robot 
* oc policy add-role-to-user view system:serviceaccount:dev:robot
* oc policy add-role-to-user edit system:serviceaccount:dev:robot

### creating from template

* oc process -f build.yml | oc create -f -

### building the project

* oc start-build bc/springboot-echo-service
* oc expose svc/springboot-echo-service 