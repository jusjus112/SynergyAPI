package usa.synergy.utilities.libraries.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import usa.synergy.utilities.libraries.commands.ArgumentDirection;

/**
 * Adds an input to the given command or argument.
 * So let's take the command /gamemode creative <user>
 * It will add the user input defined by the direction and type.
 *
 * And example for user input will be:
 * <pre>
 * &#064;CommandArgument(direction = ArgumentDirection.AFTER, type = String.class)
 * </pre>
 *
 * @see ArgumentDirection
 * @see CommandSettings
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Repeatable(CommandArguments.class)
public @interface CommandArgument {

  /**
   * Direction of the argument to put and search for
   * within the executed arguments inside the command.
   *
   * So let's take the command /gamemode creative <user>
   * You use ArgumentDirection.AFTER with a String.class type.
   *
   * @see ArgumentDirection
   * @return the direction where the input should go.
   */
  ArgumentDirection direction();

  /**
   *
   * @return
   */
  Class<?> type();

}
