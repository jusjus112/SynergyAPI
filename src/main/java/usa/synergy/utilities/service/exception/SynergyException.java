package usa.synergy.utilities.service.exception;

public class SynergyException extends Exception {

  public SynergyException(String message) {
    super(message);
  }

  public SynergyException(String message, Throwable cause) {
    super(message, cause);
  }

  public SynergyException(Throwable cause) {
    super(cause);
  }

  public SynergyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
