package io.aminadaven;

public interface BaseAPI {
    default String basePath() {
        return "/";
    }

    default HttpMethod defaultMethod() {
        return HttpMethod.GET;
    }
}
