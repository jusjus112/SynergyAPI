package usa.synergy.utilities.libraries.files.json.adapters.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
public class BukkitLocationSerializer implements JsonDeserializer<Location>, JsonSerializer<Location> {

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(new LinkedHashMap<String, Object>() {{
            put("world", location.getWorld().getName());
            put("x", location.getX());
            put("y", location.getY());
            put("z", location.getZ());
            put("pitch", location.getPitch());
            put("yaw", location.getYaw());
        }});
    }

    @Override
    public Location deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject obj = jsonElement.getAsJsonObject();
        World world = Bukkit.getWorld(obj.get("world").getAsString());

        if (world == null) {
            throw new NullPointerException("Cannot find the world '" +
                obj.get("world").getAsString() + "' does it exist?");
        }

        return new Location(world, obj.get("x").getAsDouble(), obj.get("y").getAsDouble(),
                obj.get("z").getAsDouble(), obj.get("yaw").getAsFloat(), obj.get("pitch").getAsFloat());
    }
}
