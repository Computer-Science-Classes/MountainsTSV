
public class CustomException extends Exception {
    private String errorType;

    public CustomException(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorType() {
        return errorType;
    }
}
