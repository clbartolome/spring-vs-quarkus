# spring-vs-quarkus

Repository to compare SpringBoot applications with Quarkus applications.

Both applications are built with the minimal dependencies and code to expose a REST service and return Madrid's weather using the public free API (Open Meteo)[https://open-meteo.com/en]

The enpoint for both applications is `/weather/madrid`.

## Demo

Create demo namespace:

```sh
oc new-project spring-vs-quarkus
```

### Build Applications

- SpringBoot application

```sh
# Create build
oc new-build \
  --name weather-spring \
  openshift/ubi8-openjdk-11:1.3~https://github.com/clbartolome/spring-vs-quarkus \
  --context-dir weather-rest-spring

oc logs bc/weather-spring -f

# Validate
oc get is weather-spring
```

- Quarkus JVM application

```sh
# Package Application
mvn clean package -f weather-rest-quarkus/pom.xml

# Create JVM build
cat weather-rest-quarkus/src/main/docker/Dockerfile.jvm | oc new-build --name weather-quarkus-jvm --strategy=docker --dockerfile -

# Start build and follow up process
oc start-build weather-quarkus-jvm --from-dir weather-rest-quarkus/.
oc logs bc/weather-quarkus-jvm -f

# Validate image
oc get is weather-quarkus-jvm
```

- Quarkus Native application

```sh
# Package Application using Native Dockerfile
mvn clean package -Pnative -Dquarkus.native.container-build=true -f weather-rest-quarkus/pom.xml

# Create Native build
cat weather-rest-quarkus/src/main/docker/Dockerfile.native | oc new-build --name weather-quarkus-native --strategy=docker --dockerfile -

# Start build and follow up process
oc start-build weather-quarkus-native --from-dir weather-rest-quarkus/.
oc logs bc/weather-quarkus-native -f

# Validate image
oc get is weather-quarkus-native
```

### Deploy basic applications

Deploy applications using a basic kubernetes resources structure: DEPLOYMENT <-> SERVICE <-> ROUTE

```sh
# Deploy spring-boot app
oc process -f basic-template.yaml \
  -p APP_NAME=weather-spring \
  -p APP_RUNTIME=spring-boot \
  -p APP_GROUP=spring-boot \
  -p APP_NAMESPACE=spring-vs-quarkus \
  | oc apply -f -

# Validate
APP_ROUTE=$(oc get route weather-spring -o jsonpath='{.status.ingress[0].host}')
curl http://$APP_ROUTE/weather/madrid

# Deploy quarkus app
oc process -f basic-template.yaml \
  -p APP_NAME=weather-quarkus-jvm \
  -p APP_RUNTIME=quarkus \
  -p APP_GROUP=quarkus-jvm \
  -p APP_NAMESPACE=spring-vs-quarkus \
  | oc apply -f -

# Validate
APP_ROUTE=$(oc get route weather-quarkus-jvm -o jsonpath='{.status.ingress[0].host}')
curl http://$APP_ROUTE/weather/madrid

# Deploy quarkus native app
oc process -f basic-template.yaml \
  -p APP_NAME=weather-quarkus-native \
  -p APP_RUNTIME=quarkus \
  -p APP_GROUP=quarkus-native \
  -p APP_NAMESPACE=spring-vs-quarkus \
  | oc apply -f -

# Validate
APP_ROUTE=$(oc get route weather-quarkus-native -o jsonpath='{.status.ingress[0].host}')
curl http://$APP_ROUTE/weather/madrid
```

### Deploy Serverless applications

Deploy applications using Knative services

# Deploy quarkus app
oc process -f knative-template.yaml \
  -p APP_NAME=weather-quarkus-jvm \
  -p APP_RUNTIME=quarkus \
  -p APP_GROUP=quarkus-jvm \
  -p APP_NAMESPACE=spring-vs-quarkus \
  | oc apply -f -

# Validate
APP_ROUTE=$(kn service describe weather-quarkus-jvm-serverless -o url)
curl $APP_ROUTE/weather/madrid

# Deploy quarkus native app
oc process -f knative-template.yaml \
  -p APP_NAME=weather-quarkus-native \
  -p APP_RUNTIME=quarkus \
  -p APP_GROUP=quarkus-native \
  -p APP_NAMESPACE=spring-vs-quarkus \
  | oc apply -f -

# Validate
APP_ROUTE=$(kn service describe weather-quarkus-native-serverless -o url)
curl $APP_ROUTE/weather/madrid
```