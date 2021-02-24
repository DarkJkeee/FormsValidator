package form.library.interfaces;

public interface ValidationError {
    String getMessage();
    String getPath();
    Object getFailedValue();
}
