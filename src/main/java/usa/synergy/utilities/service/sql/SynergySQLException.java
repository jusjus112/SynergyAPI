package usa.synergy.utilities.service.sql;

public class SynergySQLException extends Exception {

  public SynergySQLException(String message) {
    super(message);
  }

  public SynergySQLException(String message, Throwable cause) {
    super(message, cause);
  }

  public SynergySQLException(Throwable cause) {
    super(cause);
  }

  public SynergySQLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
