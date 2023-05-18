package io.aminadaven;

@RestEndpoint("/test")
public class TestClass {
    @RestMethod(HttpMethod.POST)
    public String testMethod(TestParam param) {
        System.out.println("param is: " + param.getParam());
        return "Expected Response: " + param.getParam();
    }

    @RestMethod(HttpMethod.POST)
    public int testErr() {
        return 1 / 0;
    }

    @RestMethod(HttpMethod.POST)
    public int divide(TwoInts twoInts) throws WrongParamsException {
        try {
            return twoInts.int1 / twoInts.int2;
        } catch (ArithmeticException e) {
            throw new WrongParamsException("you cant do this divide!");
        }
    }

    static class TestParam {
        private String param;

        public TestParam(String param) {
            this.param = param;
        }

        public String getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }
    }
}
