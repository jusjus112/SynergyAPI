package usa.synergy.utilities.assets.utilities;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class UtilJSON {

  public static JsonObject getObjectFromLocation(Location location) {
    JsonObject locObj = new JsonObject();

    locObj.addProperty("WORLD", location.getWorld().getName());
    locObj.addProperty("X", Double.toString(location.getX()));
    locObj.addProperty("Y", Double.toString(location.getY()));
    locObj.addProperty("Z", Double.toString(location.getZ()));
    locObj.addProperty("YAW", Float.toString(location.getYaw()));
    locObj.addProperty("PITCH", Float.toString(location.getPitch()));
    return locObj;
  }

  public static Location getLocationFromObject(JsonObject object) {
    return new Location(
        Bukkit.getWorld(object.get("WORLD").getAsString()),
        object.get("X").getAsDouble(),
        object.get("Y").getAsDouble(),
        object.get("Z").getAsDouble(),
        object.get("YAW").getAsFloat(),
        object.get("PITCH").getAsFloat()
    );
  }

}
