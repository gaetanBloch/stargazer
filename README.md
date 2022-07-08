[![Stargazer CI](https://github.com/gaetanBloch/stargazer/actions/workflows/github-actions.yml/badge.svg)](https://github.com/gaetanBloch/stargazer/actions)
[![codecov](https://codecov.io/gh/gaetanBloch/stargazer/branch/main/graph/badge.svg?token=Je5WFXQwKM)](https://codecov.io/gh/gaetanBloch/stargazer)
[![Mergify Status][https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/gaetanBloch/stargazer&style=flat]](https://mergify.com)

# Stargazer Project

GitHub provides a features allowing a user to [save a repository with a star](https://docs.github.com/en/get-started/exploring-projects-on-github/saving-repositories-with-stars). Those users are called *stargazers.*
![Stars](https://i.imgur.com/J5VQ599.png)
This feature is great for users to bookmark repositories. It is also quite interesting for maintainers  as they can know the number of people that are interested in their project.

The more stars a repository gets, the more popular the project is! 🚀

## Analysis
We would like to leverage those stars to find neighbours of a repository.
**We define a neighbour of a repository A as a repository B that has been starred by a same user.**
> For example, if `joe` adds a star to the repository `projectA` and `projectB`, we define those repositories `projectA` and `projectB` as being *neighbours*.
>

## API endpoint
The goal of his project is to have a Web service that can receive such a request:
```
GET api/v1/repos/<user>/<repo>/starneighbours
```
This endpoint must return the list of neighbours repositories, meaning repositories where stargazers are found in common.
The returned JSON format should look like:
```json
[
 {
   "repo": <repoA>,
    "stargazers": [<stargazer in common 1>, ..., <stargazer in common n>],
 },
 {
   "repo": <repoB>,
    "stargazers": [<stargazer in common 1>, ..., <stargazer in common n>],
 },
 ...
]
```

# Information & Documentation

* The API is deployed on Heroku : https://stargazer-gbloch.herokuapp.com/
* A CI/CD pipeline is setup with github actions for each PR/Push:
  * Checkout the code
  * Setup Java 17
  * Run UT/IT
  * Upload Code coverage to Codecov.io
  * Deploy to Heroku
* I also tried to setup Mergify as a bonus as you can see with this PR : https://github.com/gaetanBloch/stargazer/pull/1. Though it doesn't work as I don't have a subscription.

To check the API documentation navigate to `/api/doc`:

![API Documentation](https://i.imgur.com/9JZhaMl.jpg)

# Quarkus

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/stargazer-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Document your REST APIs with OpenAPI - comes with Swagger UI
- SmallRye Fault Tolerance ([guide](https://quarkus.io/guides/microprofile-fault-tolerance)): Define fault-tolerant services

## Provided Code

### RESTEasy Reactive

Easily start your Reactive RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
