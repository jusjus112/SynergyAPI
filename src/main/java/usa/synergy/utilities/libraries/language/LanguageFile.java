package usa.synergy.utilities.libraries.language;

import java.util.List;
import lombok.Getter;
import usa.synergy.utilities.assets.YMLFile;

@Getter
public class LanguageFile {

    private LanguageCountry key;
    private YMLFile file;
    private String name;

    public LanguageFile(LanguageCountry key, YMLFile file){
        this.key = key;
        this.file = file;
//        this.name = key;
    }

    public String get(Language language){
        if (file.get().contains(language.getKey())) {
            return file.get().getString(language.getKey());
        }
        return null;
    }

    public List<String> getList(Language language){
        if (file.get().contains(language.getKey())) {
            return file.get().getStringList(language.getKey());
        }
        return null;
    }

}
