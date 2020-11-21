package exceptions;

public class BindingNotFoundException extends RuntimeException{

    public BindingNotFoundException(String className) {
        super("Binding not found for this class" + className);
    }

}
