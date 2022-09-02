package usa.synergy.utilities.libraries.commands.api;

import lombok.SneakyThrows;

public interface Type<T> {

  Type<Integer> INTEGER = new Type<>() {
    @Override
    public String getName() {
      return "int";
    }

    @Override
    public Integer parse(String s) {
      return Integer.parseInt(s);
    }
  };

  Type<Double> DOUBLE = new Type<>() {
    @Override
    public String getName() {
      return "double";
    }

    @Override
    public Double parse(String s) {
      return Double.parseDouble(s);
    }
  };

  Type<Boolean> BOOLEAN = new Type<>() {

    public String getName() {
      return "boolean";
    }

    @SneakyThrows
    public Boolean parse(String s) {
      switch(s.toLowerCase()) {
        case "yes":
        case "true":
          return true;
        case "no":
        case "false":
          return false;
        default:
          throw new Exception("Text " + s + "\" is not in boolean form.");
      }
    }
  };

  String getName();

  /**
   *
   * @param s the string to be parsed into an object
   * @return a non null value of the parsed string
   */
  T parse(String s);
}
