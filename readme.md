# SimpleAPI Documentation

[![](https://jitpack.io/v/Aminadaven/SimpleAPI.svg)](https://jitpack.io/#Aminadaven/SimpleAPI)

## Introduction

SimpleAPI is a lightweight library that simplifies the creation of RESTful endpoints in Tomcat applications. It provides a convenient way to define RESTful endpoints using annotations and handles the request/response processing for you. This documentation provides comprehensive information on how to use SimpleAPI to build RESTful APIs in your Tomcat applications.

## Table of Contents

1. [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
2. [Annotation Usage](#annotation-usage)
    - [`@RestEndpoint`](#restendpoint)
    - [`@RestMethod`](#restmethod)
    - [`@ExposedException`](#exposedexception)
3. [Response Handling](#response-handling)
4. [Exception Handling](#exception-handling)
5. [Usage Examples](#usage-examples)
    - [Example 1: Creating a Basic Endpoint](#example-1-creating-a-basic-endpoint)
    - [Example 2: Handling GET and POST Requests](#example-2-handling-get-and-post-requests)
    - [Example 3: Exception Handling](#example-3-exception-handling)
6. [Conclusion](#conclusion)

## Getting Started

### Prerequisites

To use SimpleAPI, ensure that you have the following:

- Java Development Kit (JDK) 11 or later
- Apache Tomcat web server

### Installation

You can add SimpleAPI to your project by including the library as a dependency in your build configuration. Here's an example for Maven, assuming you have already added the JitPack repository to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.github.aminadaven</groupId>
    <artifactId>SimpleAPI</artifactId>
    <version>1.0.0</version>
</dependency>
```

And here's an example for Gradle, assuming you have already added the JitPack repository to your `build.gradle` file:

```groovy
implementation 'com.github.aminadaven:SimpleAPI:1.0.0'
```

To add the JitPack repository to your Maven or Gradle configuration, follow these steps:

1. Open your `pom.xml` (for Maven) or `build.gradle` (for Gradle) file.
2. Add the JitPack repository URL as a new repository entry:

   For Maven:

   ```xml
   <repositories>
       <repository>
           <id>jitpack.io</id>
           <url>https://jitpack.io</url>
       </repository>
   </repositories>
   ```

   For Gradle:

   ```groovy
   repositories {
       maven { url 'https://jitpack.io' }
   }
   ```


## Annotation Usage

SimpleAPI provides two annotations to define and configure RESTful endpoints: `@RestEndpoint` and `@RestMethod`.

### `@RestEndpoint`

The `@RestEndpoint` annotation allows you to specify the base path for your RESTful endpoint. 
This path will be used as the prefix for all mapped methods within the endpoint class.
You can also provide the default HTTP method to be used if not explicitly specified for individual methods.

#### Annotation Attributes

The `@RestEndpoint` annotation has the following attributes:

- `value`: This attribute and represents the base path for the RESTful endpoint. 
It specifies the path segment that will be appended to the application's context path to form the complete URL path for the endpoint. 
It should be provided as a string value.

- `defaultMethod`: This attribute is optional and represents the default HTTP method to be used if not explicitly specified for individual methods

within the endpoint. It should be provided as an enum value of the `HttpMethod` enum.

Here's an example of how to use the `@RestEndpoint` annotation:

```java
@RestEndpoint("/hello")
public class HelloEndpoint {
    // ...
}
```
```java
@RestEndpoint("/api")
public class MyEndpoint {
    // ...
}
```

In this example, we have defined a RESTful endpoint using the `@RestEndpoint` annotation. The `value` attribute is set to `"/hello"`, which represents the base path for the endpoint. The `defaultMethod` is not specified, so it defaults to `GET`.

### `@RestMethod`

The `@RestMethod` annotation is used to mark a method within a RESTful endpoint class as a RESTful endpoint method.
It should be applied to the methods within the endpoint class. 
This annotation allows you to specify the HTTP method for the annotated method.

#### Annotation Attributes

The `@RestMethod` annotation allows you to specify the HTTP method for a RESTful endpoint method. The library supports GET and POST methods.

The annotation has the following attributes:

- `value`: This attribute is optional and represents the HTTP method to be associated with the annotated method. 
If not specified, the default method defined at the class level will be used. It should be provided as an enum value of the `HttpMethod` enum.

Here's an example of how to use the `@RestMethod` annotation:

```java
@RestEndpoint("/hello")
public class HelloEndpoint {

    @RestMethod
    public String sayHello() {
        return "Hello, world!";
    }
}
```

In this example, the `sayHello` method is annotated with `@RestMethod`, indicating that it should handle the default 
HTTP method defined at the class level (`GET`). The method returns a string response, which will be automatically serialized to JSON.

#### Method Path

The name of the annotated method is automatically added to the endpoint's path. 
For example, if the annotated method is named `sayHello`, the complete path to access the method would be `/hello/sayHello`.

### `@ExposedException`

This annotation is used to mark an exception class as one that should be exposed to the client.
By default, exceptions are not exposed to the client for security reasons.
However, if you want to explicitly expose certain exceptions, you can use this annotation.

```java
@ExposedException(HttpServletResponse.SC_BAD_REQUEST)
public class CustomException extends Exception {
    // ...
}
```


## Response Handling

The library simplifies the generation of JSON responses using the Gson library.
To return a response from a RESTful endpoint method, simply return the desired object or data structure.
The library will automatically serialize it to JSON and set the appropriate response headers.

```java
@RestMethod(HttpMethod.GET)
public MyData getSomeData() {
    MyData data = // retrieve data
    return data;
}
```

## Exception Handling

The SimpleAPI library provides support for exception handling within your RESTful endpoints. 
By default, exceptions are not exposed to the client for security reasons. However, 
you can explicitly expose certain exceptions using the `@ExposedException` annotation.

To expose an exception, annotate the exception class with `@ExposedException` and specify the HTTP status code to be returned to the client.

```java
@ExposedException(HttpServletResponse.SC_BAD_REQUEST)
public class CustomException extends Exception {
    // ...
}
```

Within your RESTful endpoint methods, you can handle exceptions and return appropriate error responses.

```java
@RestMethod(HttpMethod.GET)
public String getData() {
    try {
        // Perform operation
    } catch (CustomException e) {
        // Handle exception
    }
}
```

## Usage Examples

Let's explore some usage examples to demonstrate the integration and functionality of SimpleAPI in Tomcat applications.

### Example 1: Creating a Basic Endpoint

```java
@RestEndpoint("/hello")
public class HelloEndpoint {

    @RestMethod
    public String sayHello() {
        return "Hello, world!";
    }
}
```

In this example, we have defined a RESTful endpoint using the `@RestEndpoint` annotation. The `value` attribute is set to `"/hello"`, which represents the base path for the endpoint. The `defaultMethod` is not specified, so it defaults to `GET`.

The `sayHello` method is annotated with `@RestMethod`, indicating that it should handle the default HTTP method defined at the class level (`GET`). The method returns a string response, which will be automatically serialized to JSON.

### Example 2: Handling GET and POST Requests

```java
@RestEndpoint("/hello")
public class HelloEndpoint {

    @RestMethod(HttpMethod.GET)
    public String sayHello() {
        return "Hello, world!";
    }

    @RestMethod(HttpMethod.POST)
    public String sayHelloWithName(String name) {
        return "Hello, " + name + "!";
    }
}
```

In this example, we have added a new method `sayHelloWithName` annotated with `@RestMethod(HttpMethod.POST)`.
This method takes a `name` parameter and returns a personalized greeting.
When a POST request is made to `/hello/sayHelloWithName` with a JSON payload containing the `name` field, 
the method will be invoked, and the data will be processed and saved.
By specifying the `HttpMethod.POST` in the annotation, we override the default HTTP method defined at the class level.

When a GET request is made to `/hello/sayHello`, the `sayHello` method will be invoked and return the general greeting. 
When a POST request is made to `/hello/sayHelloWithName` with a `name` parameter, 
the `sayHelloWithName` method will be invoked and return a personalized greeting.


### Example 3: Exception Handling

```java
@RestEndpoint("/api")
public class MyEndpoint {
    @RestMethod(HttpMethod.GET)
    public void performOperation() throws CustomException {
        // Perform operation that may throw CustomException
    }
}

@ExposedException(HttpServletResponse.SC_BAD_REQUEST)
public class CustomException extends Exception {
    // ...
}
```

In this example, the `performOperation` method is annotated with `@RestMethod(HttpMethod.GET)` and throws a `CustomException`. 
The `CustomException` class is marked with `@ExposedException(HttpServletResponse.SC_BAD_REQUEST)` to expose it to the 
client with the corresponding HTTP status code. If the `performOperation` method throws a `CustomException`, an error
response with the status code 400 (Bad Request) will be returned to the client.

These examples illustrate some of the basic usage scenarios of the RestEndpointProcessor library. 
You can customize and extend the functionality of your RESTful endpoints according to your specific requirements.


## Conclusion

SimpleAPI is a lightweight library that simplifies the creation of RESTful endpoints in Tomcat applications. 
By utilizing annotations such as `@RestEndpoint` and `@RestMethod`, you can easily define your endpoints and handle the request/response processing. 
With its intuitive approach and integration with Tomcat, SimpleAPI allows you to focus on building robust APIs with minimal effort.

If you have any further questions or need assistance, please refer to the documentation or reach out to the library maintainer.

**Note**: SimpleAPI currently supports JSON format for request and response handling. Handling other formats is not covered in this documentation.

---
