package usa.synergy.utilities.libraries.language;

import java.util.Locale;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LanguageCountry  {

  ENGLISH("EN"),
  DUTCH("NL"),
  SPANISH("ES"),
  FRENCH("FR"),
  GERMAN("DE");

  private final String key;

  public String getName(){
    return this.toString();
  }

  @Override
  public String toString() {
    return this.key.toUpperCase(Locale.ROOT) + "_" + this.key.toLowerCase(Locale.ROOT);
  }
}
