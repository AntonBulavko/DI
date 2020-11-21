package injection;

public interface Provider<T> {

    T getInstance() throws NoSuchMethodException;

}
