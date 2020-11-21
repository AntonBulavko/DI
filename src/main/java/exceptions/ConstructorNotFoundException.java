package exceptions;

public class ConstructorNotFoundException extends RuntimeException {

    public ConstructorNotFoundException() {
        super("There are no default constructor");
    }
}
