package usa.synergy.utilities.libraries.commands.exceptions;


public class ArgumentException extends RuntimeException {

    private final String message;
    private final int index;

    public ArgumentException(String message) {
        this.message = message;
        this.index = -1;
    }

    public ArgumentException(int index) {
        this.message = null;
        this.index = index;
    }

    public String getErrorMessage() {
        return message;
    }

    public int getIndex() {
        return index;
    }
}
