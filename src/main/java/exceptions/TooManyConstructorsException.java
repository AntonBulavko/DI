package exceptions;

public class TooManyConstructorsException extends RuntimeException {

    public TooManyConstructorsException() {
        super("Too many constructors with @inject annotation");
    }

}
