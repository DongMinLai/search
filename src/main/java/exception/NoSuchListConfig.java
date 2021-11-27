package exception;

public class NoSuchListConfig extends RuntimeException{

    public NoSuchListConfig(String message)
    {
        super(message);
    }

}
