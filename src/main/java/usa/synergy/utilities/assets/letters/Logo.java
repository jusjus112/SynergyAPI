package usa.synergy.utilities.assets.letters;

import static org.bukkit.Bukkit.getServer;

import java.util.Arrays;
import org.bukkit.ChatColor;
import usa.synergy.utilities.utlities.SynergyLogger;

public class Logo {

  // Copyright 'http://www.messletters.nl/big-text/'
  private String[] logo_normal = new String[]{
      " ",
      "  ____                                                ",
      " / ___|   _   _   _ __     ___   _ __    __ _   _   _ ",
      " \\___ \\  | | | | | '_ \\   / _ \\ | '__|  / _` | | | | |",
      "  ___) | | |_| | | | | | |  __/ | |    | (_| | | |_| |",
      " |____/   \\__, | |_| |_|  \\___| |_|     \\__, |  \\__, |",
      "          |___/                         |___/   |___/ "
  };

  // Copyright 'http://www.messletters.nl/big-text/' - (Colossal)
  private static final String[] logo_colossal = new String[]{
      " ",
      " .d8888b.",
      "d88P  Y88b",
      "Y88b.",
      " \"Y888b.   888  888 88888b.   .d88b.  888d888  .d88b.  888  888",
      "    \"Y88b. 888  888 888 \"88b d8P  Y8b 888P\"   d88P\"88b 888  888",
      "      \"888 888  888 888  888 88888888 888     888  888 888  888",
      "Y88b  d88P Y88b 888 888  888 Y8b.     888     Y88b 888 Y88b 888",
      " \"Y8888P\"   \"Y88888 888  888  \"Y8888  888      \"Y88888  \"Y88888 ",
      "                888                                888      888",
      "           Y8b d88P                           Y8b d88P Y8b d88P ",
      "            \"Y88P\"                             \"Y88P\"   \"Y88P\""
  };

  // Copyright 'http://www.messletters.nl/big-text/' - (Lean)
  private String[] logo_cube = new String[]{
      " ",
      "      _/_/_/",
      "   _/             _/    _/      _/_/_/        _/_/       _/  _/_/        _/_/_/      _/    _/",
      "    _/_/         _/    _/      _/    _/    _/_/_/_/     _/_/          _/    _/      _/    _/",
      "       _/       _/    _/      _/    _/    _/           _/            _/    _/      _/    _/",
      "_/_/_/           _/_/_/      _/    _/      _/_/_/     _/              _/_/_/        _/_/_/",
      "                    _/                                                   _/            _/",
      "               _/_/                                                 _/_/          _/_/",
  };

  public static void display(){
    Arrays.stream(logo_colossal).forEach(s ->
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + s));
    System.out.println("  ");
  }

  public static void showInformation(){
    SynergyLogger.info(
        "This server runs"
    );
  }
}
