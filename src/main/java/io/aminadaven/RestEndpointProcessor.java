package io.aminadaven;

import com.google.gson.Gson;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HandlesTypes;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

@HandlesTypes(RestEndpoint.class)
public class RestEndpointProcessor implements ServletContainerInitializer {
    private static final Gson gson = new Gson();

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
        if (classes == null)
            return;
        for (Class<?> clazz : classes) {
            RestEndpoint annotation = clazz.getAnnotation(RestEndpoint.class);
            if (annotation == null) continue;
            try {
                registerServlet(clazz, annotation, servletContext);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ServletException(e.getCause());
            }
        }
    }

    private void registerServlet(Class<?> clazz, RestEndpoint annotation, ServletContext servletContext) throws Exception {
        String basePath = annotation.value();
        HttpMethod defaultMethod = annotation.defaultMethod();
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object instance = constructor.newInstance();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            RestMethod methodAnnotation = method.getAnnotation(RestMethod.class);
            if (methodAnnotation == null) continue;
            String methodPath = method.getName();
            String fullPath = basePath + "/" + methodPath;
            HttpMethod methodHttpMethod = methodAnnotation.value();
            HttpMethod finalHttpMethod = (methodHttpMethod != HttpMethod.UNDEFINED) ? methodHttpMethod : defaultMethod;

            HttpServlet servlet = createDynamicServlet(instance, method, finalHttpMethod);
            servletContext.addServlet(clazz.getSimpleName() + "#" + method.getName(), servlet).addMapping(fullPath);
        }
    }

    private HttpServlet createDynamicServlet(Object instance, Method method, HttpMethod httpMethod) {
        return new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
                if (httpMethod != HttpMethod.GET) return;
                Map<String, String[]> parameterMap = request.getParameterMap();
                String data = gson.toJson(parameterMap);
                invokeEndpointMethod(instance, method, data, response);
            }

            @Override
            protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
                if (httpMethod != HttpMethod.POST) return;
                String data = new String(request.getInputStream().readAllBytes());
                invokeEndpointMethod(instance, method, data, response);
            }

            private void invokeEndpointMethod(Object instance, Method method, String data, HttpServletResponse response) throws IOException {
                Object result;
                int status;
                try {
                    if (method.getParameterCount() > 0) {
                        Object param = gson.fromJson(data, method.getParameterTypes()[0]);
                        result = method.invoke(instance, param);
                    } else {
                        result = method.invoke(instance);
                    }
                    status = HttpServletResponse.SC_OK;
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    if (cause.getClass().isAnnotationPresent(ExposedException.class)) {
                        result = new ErrorResponse(cause.getMessage());
                        status = cause.getClass().getAnnotation(ExposedException.class).value();
                    } else {
                        cause.printStackTrace();
                        result = new ErrorResponse("An error occurred");
                        status = HttpServletResponse.SC_BAD_REQUEST;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result = new ErrorResponse("Internal Server error");
                    status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                }

                // Set the response content type to JSON
                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(result));
                response.setStatus(status);
            }
        };
    }
}
