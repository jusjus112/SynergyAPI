package usa.synergy.utilities.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@RequiredArgsConstructor
public class TinyURL {

  private final String url;

  public String generate() {
    try {
      String tinyUrl1 = "http://tinyurl.com/api-create.php?url=";
      String tinyUrlLookup = tinyUrl1 + url;
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(new URL(tinyUrlLookup).openStream()));
      return reader.readLine();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ChatColor.RED + "Something went wrong while generating tinyURL. Contact your developer.";
  }


}