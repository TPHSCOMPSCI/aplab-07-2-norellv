import java.awt.*;
import java.util.*;

public class Steganography {

    public static void clearLow(Pixel p) {
        int r = (p.getRed() / 4) * 4;
        int g = (p.getGreen() / 4) * 4;
        int b = (p.getBlue() / 4) * 4;
        p.setColor(new Color(r, g, b));
    }

    public static Picture testClearLow(Picture original) {
        Picture result = new Picture(original);
        Pixel[][] grid = result.getPixels2D();
        for (Pixel[] row : grid) {
            for (Pixel px : row) {
                clearLow(px);
            }
        }
        return result;
    }

    public static void setLow(Pixel px, Color col) {
        int r = (px.getRed() / 4) * 4 + (col.getRed() / 64);
        int g = (px.getGreen() / 4) * 4 + (col.getGreen() / 64);
        int b = (px.getBlue() / 4) * 4 + (col.getBlue() / 64);
        px.setColor(new Color(r, g, b));
    }

    public static Picture testSetLow(Picture original, Color col) {
        Picture result = new Picture(original);
        Pixel[][] grid = result.getPixels2D();
        for (Pixel[] row : grid) {
            for (Pixel px : row) {
                setLow(px, col);
            }
        }
        return result;
    }

    public static Picture revealPicture(Picture hidden) {
        Picture copy = new Picture(hidden);
        Pixel[][] grid = copy.getPixels2D();
        Pixel[][] source = hidden.getPixels2D();
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                Color col = source[r][c].getColor();
                int red = (col.getRed() % 4) * 64;
                int green = (col.getGreen() % 4) * 64;
                int blue = (col.getBlue() % 4) * 64;
                grid[r][c].setColor(new Color(red, green, blue));
            }
        }
        return copy;
    }

    public static boolean canHide(Picture base, Picture secret) {
        return base.getWidth() >= secret.getWidth() && base.getHeight() >= secret.getHeight();
    }

    public static Picture hidePicture(Picture base, Picture secret, int startRow, int startCol) {
        Picture result = new Picture(base);
        Pixel[][] baseGrid = result.getPixels2D();
        Pixel[][] secretGrid = secret.getPixels2D();
        for (int r = 0; r < secretGrid.length; r++) {
            for (int c = 0; c < secretGrid[0].length; c++) {
                int baseR = startRow + r;
                int baseC = startCol + c;
                if (baseR < baseGrid.length && baseC < baseGrid[0].length) {
                    Color hiddenColor = secretGrid[r][c].getColor();
                    Color baseColor = baseGrid[baseR][baseC].getColor();
                    int newR = (baseColor.getRed() / 4 * 4) + (hiddenColor.getRed() / 64);
                    int newG = (baseColor.getGreen() / 4 * 4) + (hiddenColor.getGreen() / 64);
                    int newB = (baseColor.getBlue() / 4 * 4) + (hiddenColor.getBlue() / 64);
                    baseGrid[baseR][baseC].setColor(new Color(newR, newG, newB));
                }
            }
        }
        return result;
    }

    public static boolean isSame(Picture one, Picture two) {
        if (one.getWidth() != two.getWidth() || one.getHeight() != two.getHeight()) {
            return false;
        }
        Pixel[][] grid1 = one.getPixels2D();
        Pixel[][] grid2 = two.getPixels2D();
        for (int r = 0; r < grid1.length; r++) {
            for (int c = 0; c < grid1[0].length; c++) {
                if (!grid1[r][c].getColor().equals(grid2[r][c].getColor())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static ArrayList<Point> findDifferences(Picture one, Picture two) {
        ArrayList<Point> points = new ArrayList<>();
        if (one.getWidth() != two.getWidth() || one.getHeight() != two.getHeight()) {
            return points;
        }
        Pixel[][] grid1 = one.getPixels2D();
        Pixel[][] grid2 = two.getPixels2D();
        for (int r = 0; r < grid1.length; r++) {
            for (int c = 0; c < grid1[0].length; c++) {
                if (!grid1[r][c].getColor().equals(grid2[r][c].getColor())) {
                    points.add(new Point(c, r));
                }
            }
        }
        return points;
    }

    public static Picture showDifferentArea(Picture pic, ArrayList<Point> diffs) {
        Picture copy = new Picture(pic);
        if (diffs.isEmpty()) {
            return copy;
        }

        int minRow = Integer.MAX_VALUE, maxRow = Integer.MIN_VALUE;
        int minCol = Integer.MAX_VALUE, maxCol = Integer.MIN_VALUE;

        for (Point p : diffs) {
            int row = p.y;
            int col = p.x;
            minRow = Math.min(minRow, row);
            maxRow = Math.max(maxRow, row);
            minCol = Math.min(minCol, col);
            maxCol = Math.max(maxCol, col);
        }

        Graphics2D g = copy.createGraphics();
        g.setColor(Color.BLUE);
        g.drawRect(minCol, minRow, maxCol - minCol, maxRow - minRow);
        g.dispose();

        return copy;
    }

    public static ArrayList<Integer> encodeString(String text) {
        text = text.toUpperCase();
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        ArrayList<Integer> encoded = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            if (text.substring(i, i + 1).equals(" ")) {
                encoded.add(27);
            } else {
                encoded.add(alpha.indexOf(text.substring(i, i + 1)) + 1);
            }
        }
        encoded.add(0);
        return encoded;
    }

    public static String decodeString(ArrayList<Integer> nums) {
        String result = "";
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < nums.size(); i++) {
            if (nums.get(i) == 27) {
                result += " ";
            } else {
                result += alpha.substring(nums.get(i) - 1, nums.get(i));
            }
        }
        return result;
    }

    private static int[] getBitPairs(int num) {
        int[] bits = new int[3];
        int temp = num;
        for (int i = 0; i < 3; i++) {
            bits[i] = temp % 4;
            temp = temp / 4;
        }
        return bits;
    }

    public static void hideText(Picture img, String text) {
        ArrayList<Integer> codes = encodeString(text);
        Pixel[][] grid = img.getPixels2D();
        int index = 0;
        for (int r = 0; r < grid.length && index < codes.size(); r++) {
            for (int c = 0; c < grid[0].length && index < codes.size(); c++) {
                int val = codes.get(index);
                int[] bits = getBitPairs(val);
                Pixel px = grid[r][c];
                int newR = (px.getRed() / 4) * 4 + bits[0];
                int newG = (px.getGreen() / 4) * 4 + bits[1];
                int newB = (px.getBlue() / 4) * 4 + bits[2];
                px.setColor(new Color(newR, newG, newB));
                index++;
            }
        }
    }

    public static String revealText(Picture img) {
        ArrayList<Integer> result = new ArrayList<>();
        Pixel[][] grid = img.getPixels2D();
        for (Pixel[] row : grid) {
            for (Pixel px : row) {
                int val = (px.getBlue() & 3) + ((px.getGreen() & 3) << 2) + ((px.getRed() & 3) << 4);
                if (val == 0) {
                    return decodeString(result);
                }
                result.add(val);
            }
        }
        return decodeString(result);
    }

    public static void randomBlack(Picture pic, int w, int h) {
        Pixel[][] grid = pic.getPixels2D();
        int maxR = grid.length - w;
        int maxC = grid[0].length - h;
        if (maxR < 0 || maxC < 0) {
            return;
        }
        int startR = (int) (Math.random() * maxR);
        int startC = (int) (Math.random() * maxC);
        for (int r = startR; r < startR + h; r++) {
            for (int c = startC; c < startC + w; c++) {
                if (r < grid.length && c < grid[0].length) {
                    Pixel px = grid[r][c];
                    int avg = (px.getRed() + px.getGreen() + px.getBlue()) / 3;
                    px.setColor(new Color(avg, avg, avg));
                }
            }
        }
    }
}
