package usa.synergy.utilities.assets.utilities;

import java.text.DecimalFormat;
import java.util.Random;
import org.bukkit.Location;

public class UtilMath {

  public static double PI = 3.141592;
  public static double e = 2.71;

  public static Random random = new Random();

  public static String formatNumber(int number) {
    String s = String.valueOf(number);
    if (s.length() <= 4) {
      return s;
    }
    for (int i = s.length() - 3; i > 0; i -= 3) {
      s = s.substring(0, i) + "," + s.substring(i);
    }
    return s;
  }

  public static int getPercentage(int fraction, int total) {
    int percent = (int) (Double.valueOf(fraction) / Double.valueOf(total) * 100);
    if (percent >= 100) {
      return 100;
    }
    return (int) (Double.valueOf(fraction) / Double.valueOf(total) * 100);
  }

  public static double trim(double d) {
    return trim(d, 1);
  }

  public static double trim(double d, int degree) {
    if (Double.isNaN(d) || Double.isInfinite(d)) {
      d = 0;
    }
    String format = "#.#";
    for (int i = 1; i < degree; i++) {
      format += "#";
    }
    try {
      return Double.valueOf(new DecimalFormat(format).format(d));
    } catch (NumberFormatException exception) {
      return d;
    }

  }

  public static double square(double a) {
    return a * a;
  }

  public static String getKD(int kills, int deaths) {
    double d = Double.valueOf(kills) / Double.valueOf(deaths);
    if (Double.isNaN(d) || Double.isInfinite(d)) {
      return "0.00";
    }
    return new DecimalFormat("0.00").format(d);
  }

  public static String formatDouble1DP(double d) {
    String s = new DecimalFormat("0.0").format(d);
    if (s.length() == 1 || s.length() == 2) {
      s = s + ".0";
    }
    return s;
  }

  public static String formatDouble3DP(double d) {
    String s = new DecimalFormat("0.000").format(d);
    if (s.length() == 1 || s.length() == 2) {
      s = s + ".0";
    }
    return s;
  }

  public static double getDistance2D(Location l1, Location l2) {
    double x1 = l1.getX();
    double z1 = l1.getZ();
    double x2 = l2.getX();
    double z2 = l2.getZ();
    double x = x1 - x2;
    double z = z1 - z2;
    return Math.sqrt(x * x + z * z);
  }

  public static double getX(Location l1, Location l2, double L) {
    double x1 = l1.getX();
    double x2 = l2.getX();
    double z1 = l1.getZ();
    double z2 = l2.getZ();

    double top = L * (x2 - x1);
    double bottom = Math.sqrt((x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1));

    return (top / bottom) + x2;
  }

  public static double getZ(Location l1, Location l2, double L) {
    double x1 = l1.getX();
    double x2 = l2.getX();
    double z1 = l1.getZ();
    double z2 = l2.getZ();

    double top = L * (z2 - z1);
    double bottom = Math.sqrt((x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1));

    return (top / bottom) + z2;
  }

  public static String formatDouble(double d) {
    String s = new DecimalFormat("0.0").format(d);
    if (s.length() == 1 || s.length() == 2) {
      s = s + ".0";
    }
    return s;
  }

  public static int addVariation(int i) {
    int half = (int) Math.ceil(Double.valueOf(i) / 2);
    return half + new Random().nextInt(half + (i % 2 == 0 ? 1 : 0));
  }

  public static boolean getChance(float percent) {
    return getRandom(0, 100) <= percent;
  }

  public static int getRandom(int min, int max) {
    return random.nextInt(max - min) + min;
  }

  public static long getRandom(long min, long max) {
    return random.nextLong(max - min) + min;
  }

  public static double getRandom(double min, double max) {
    return random.nextDouble(max - min) + min;
  }

}
