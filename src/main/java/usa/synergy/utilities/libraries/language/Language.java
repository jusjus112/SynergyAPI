package usa.synergy.utilities.libraries.language;

public interface Language {

    /**
     *
     * @return
     */
    Language[] register();

    /**
     *
     * @return
     */
    String getKey();

    /**
     *
     * @return
     */
    LanguageCountry country();

    /**
     *
     * @return
     */
    Object getDefault();

}
