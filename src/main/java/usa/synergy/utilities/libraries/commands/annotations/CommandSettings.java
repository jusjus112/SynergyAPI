package usa.synergy.utilities.libraries.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CommandSettings {

  /**
   * If command equals an empty string, it will behave the same
   * as an args[0] argument, as you would in a bukkit command.
   *
   * @see org.bukkit.command.Command
   * @return command to be executed.
   */
  String command();

  /**
   *
   * @return
   */
  String[] aliases() default {};

  /**
   *
   * @see org.bukkit.permissions.Permission
   * @return
   */
  String permission() default "";

}
