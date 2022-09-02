package usa.synergy.utilities.libraries.files.json.adapters.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import org.bukkit.ChatColor;

public class ChatColorSerializer implements JsonDeserializer<ChatColor> {

    @Override
    public ChatColor deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return ChatColor.valueOf(jsonElement.getAsString());
    }
}
