package io.aminadaven;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Set;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestEndpointProcessorTest {

    public static final Gson GSON = new Gson();
    private static final int PORT = current().nextInt(1024, 65536);
    private static final String TOMCAT_URI = "http://localhost:" + PORT;
    private static Tomcat tomcat;

    @BeforeAll
    public static void setup() throws LifecycleException {
        tomcat = new Tomcat();
        System.out.println("Starting tomcat on PORT = " + PORT);
        tomcat.setPort(PORT);
        tomcat.getConnector();
        // Configure the Tomcat context and add the RestEndpointProcessor
        Context context = tomcat.addContext("", null);
        context.addServletContainerInitializer(new RestEndpointProcessor(), Set.of(TestClass.class));

        // Start the Tomcat server
        tomcat.start();
    }

    @AfterAll
    public static void teardown() throws LifecycleException, IOException {
        tomcat.stop();
        tomcat.destroy();
        cleanTomcat();
    }

    private static void cleanTomcat() throws IOException {
        File tomcatDir = new File(System.getProperty("user.dir"), "tomcat." + PORT);
        if (!tomcatDir.exists()) return;
        Files.walk(tomcatDir.toPath()).map(Path::toFile).sorted(Comparator.reverseOrder()).forEach(File::delete);
    }

    @Test
    public void testGetJson_shouldReturnString() throws IOException, InterruptedException {
        // Send an HTTP POST request to the endpoint
        String url = TOMCAT_URI + "/test/testMethod";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(new TestClass.TestParam("value"))))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        // Assert the response status code and content
        assertEquals(200, response.statusCode());
        assertEquals(GSON.toJson("Expected Response: value"), response.body());
    }

    @Test
    public void testException() throws IOException, InterruptedException {
        // Send an HTTP GET request to the endpoint
        String url = TOMCAT_URI + "/test/testErr";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        // Assert the response status code and content
        assertEquals(400, response.statusCode());
        assertEquals(GSON.toJson(new ErrorResponse("An error occurred")), response.body());
    }

    @Test
    public void testExposedException() throws IOException, InterruptedException {
        // Send an HTTP GET request to the endpoint
        String url = TOMCAT_URI + "/test/divide";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(new TwoInts(1, 0))))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        // Assert the response status code and content
        assertEquals(HttpServletResponse.SC_NOT_ACCEPTABLE, response.statusCode());
        assertEquals(GSON.toJson(new ErrorResponse("you cant do this divide!")), response.body());
    }
}
