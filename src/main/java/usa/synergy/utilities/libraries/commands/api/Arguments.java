package usa.synergy.utilities.libraries.commands.api;

import org.apache.commons.lang3.Validate;
import usa.synergy.utilities.libraries.commands.exceptions.ArgumentException;

public final class Arguments {

    private final String[] args;
    private final int start;

    public Arguments(String[] args) {
        this.args = args;
        this.start = 0;
    }

    public Arguments(Arguments args, int start) {
        this.args = args.args;
        this.start = args.start + start;
    }

    public int length() {
        return args.length - start;
    }

    @SuppressWarnings("unchecked")
    public String get(int index) {
        if (!this.has(index)) {
            throw new ArgumentException(index);
        }
        return args[start + index];
    }

    public boolean has(int index) {
        return start + index < args.length;
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(int index, Type<T> type, T defaultValue) {
        if (this.has(index)) {
            return this.get(index, type);
        }
        return defaultValue;
    }

    public <T> T get(int index, Type<T> type) {
        try {
            return type.parse(this.get(index));
        } catch (RuntimeException e) {
            if (e instanceof ArgumentException) {
                throw e;
            }
            throw new ArgumentException(index);
        }
    }

    /**
     * Builds a string from all the arguments
     */
    public String buildString(){
        return buildString(0, length());
    }

    /**
     * Builds a string from the starting index (inclusive)
     */
    public String buildString(int startIndex){
        return buildString(startIndex, length());
    }

    /**
     * Builds a string from the starting index (inclusive)
     * and ending at the end index (exclusive)
     */
    public String buildString(int startIndex, int endIndex){
        Validate.isTrue(endIndex <= length() && startIndex < endIndex);
        StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < endIndex; i++){
            builder.append(get(i));
            if (i + 1 < endIndex)
                builder.append(' ');
        }
        return builder.toString();
    }
}
