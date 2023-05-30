package shareit.exception;

public class AlreadyExistsException extends BadRequestException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}