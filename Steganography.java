import java.awt.Color;
import java.util.*;
public class Steganography {
  public static void clearLow( Pixel p )
{
    int r = (p.getRed() / 4) * 4;
    int g = (p.getGreen() / 4) * 4;
    int b = (p.getBlue() / 4) * 4;
    p.setColor(new Color(r, g, b));
}
  public static void testClearLow(Picture p ) {
      Picture copy = new Picture(p);
        Pixel[][] pixels = copy.getPixels2D();
        for (Pixel[] row : pixels) {
            for (Pixel pixel : row) {
                clearLow(pixel);
            }
        }
        return copy;
  }
  public static void setLow (Pixel p, Color c)
  {
    int r = (p.getRed() & 0b11111100) | (c.getRed() >> 6);
    int g = (p.getGreen() & 0b11111100) | (c.getGreen() >> 6);
    int b = (p.getBlue() & 0b11111100) | (c.getBlue() >> 6);
    p.setColor(new Color(r, g, b));

  }
  public static Picture testSetLow(Picture p, Color c) {
      Picture copy = new Picture(p);
      Pixel[][] pixels = copy.getPixels2D();
        for (Pixel[] row : pixels) {
            for (Pixel pixel : row) {
                setLow(pixel, c);
            }
        }
        return copy;
    }

  public static Picture revealPicture(Picture hidden i )
{
  Picture copy = new Picture(hidden) e ;
  Pixel[][] pixels = copy.getPixels2D();
  Pixel[][] source = hidden.getPixels2D();
    for (int r = 0; r < pixels.length; r++) 
      {
      for (int c = 0; c < pixels[0].length; c++ t ) {
    Color col = source[r][c].getColor();
    pixels[r][c].setColor(new Color(((pixels[r][c].getRed() & 0b00000011) << 6),((pixels[r][c].getGreen() & 0b00000011) << 6), ((pixels[r][c].getBlue() & 0b00000011) << 6)));
    }
      }
  return copy; 
}
}
