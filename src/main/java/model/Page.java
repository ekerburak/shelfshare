package model;

import javafx.scene.paint.Color;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Page {
    public static final int MODE_HIGHLIGHT = 0;
    public static final int MODE_LINE = 1;

    private final int pageNumber;
    private final String image;
    private ArrayList<ArrayList<Integer>> highlightCoordinates;
    private ArrayList<ArrayList<Integer>> lineCoordinates;
    private ArrayList<String> highlightColorStrings;
    private ArrayList<String> lineColorStrings;

    public Page(String image, int pageNumber) {
        this.image = image;
        this.pageNumber = pageNumber;
        highlightCoordinates = new ArrayList<ArrayList<Integer>>();
        lineCoordinates = new ArrayList<ArrayList<Integer>>();
        //colors in serialized format
        highlightColorStrings = new ArrayList<String>();
        lineColorStrings = new ArrayList<String>();
    }

    protected Page(Document mongoPage, int pageNumber) {
        this.image = mongoPage.getString("image");
        this.pageNumber = pageNumber;

        List<?> highlightCoordinates = mongoPage.getList("highlightCoordinates", List.class);
        List<?> highlightColorStrings = mongoPage.getList("highlightColorStrings", List.class);
        List<?> lineCoordinates = mongoPage.getList("lineCoordinates", List.class);
        List<?> lineColorStrings = mongoPage.getList("lineColorStrings", List.class);

        this.highlightCoordinates = convertMongoCoordinatesToCoordinates(highlightCoordinates);
        this.highlightColorStrings = convertMongoColorStringsToColorStrings(highlightColorStrings);
        this.lineCoordinates = convertMongoCoordinatesToCoordinates(lineCoordinates);
        this.lineColorStrings = convertMongoColorStringsToColorStrings(lineColorStrings);
    }


    private static ArrayList<ArrayList<Integer>> convertMongoCoordinatesToCoordinates(List<?> mongoCoordinates) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        for(Object coordinate : mongoCoordinates) {
            if(!(coordinate instanceof List)) {
                throw new RuntimeException("Invalid type for coordinate");
            }
            ArrayList<Integer> coordinateToAdd = new ArrayList<Integer>();
            for(Object coordinateData : (List<?>) coordinate) {
                if(!(coordinateData instanceof Integer)) {
                    throw new RuntimeException("Invalid type for coordinate data");
                }
                coordinateToAdd.add((Integer) coordinateData);
            }
            result.add(coordinateToAdd);
        }
        return result;
    }

    private static ArrayList<String> convertMongoColorStringsToColorStrings(List<?> mongoColorStrings) {
        ArrayList<String> result = new ArrayList<String>();
        for(Object colorString : mongoColorStrings) {
            if(!(colorString instanceof String)) {
                throw new RuntimeException("Invalid type for colorString");
            }
            result.add((String) colorString);
        }
        return result;
    }


    private static String colorToString(Color color) {
        return String.format("#%02x%02x%02x%02x",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255),
                (int)(color.getOpacity() * 255));
    }

    private static Color stringToColor(String colorStr) {
        // Assuming the format is #RRGGBBAA
        int red = Integer.valueOf(colorStr.substring(1, 3), 16);
        int green = Integer.valueOf(colorStr.substring(3, 5), 16);
        int blue = Integer.valueOf(colorStr.substring(5, 7), 16);
        int alpha = Integer.valueOf(colorStr.substring(7, 9), 16);
        return Color.rgb(red, green, blue, alpha / 255.0);
    }

    public String getImage() {
        return image;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public ArrayList<ArrayList<Integer>> getHighlightCoordinates() {
        return highlightCoordinates;
    }
    public ArrayList<ArrayList<Integer>> getLineCoordinates() {
        return lineCoordinates;
    }

    protected ArrayList<String> getHighlightColorStrings() {
        return highlightColorStrings;
    }

    protected ArrayList<String> getLineColorStrings() {
        return lineColorStrings;
    }

    public ArrayList<Color> getHighlightColors() {
        ArrayList<Color> highlightColors = new ArrayList<Color>();
        for(String colorStr : highlightColorStrings) {
            highlightColors.add(stringToColor(colorStr));
        }
        return highlightColors;
    }

    public ArrayList<Color> getLineColors() {
        ArrayList<Color> lineColors = new ArrayList<Color>();
        for(String colorStr : lineColorStrings) {
            lineColors.add(stringToColor(colorStr));
        }
        return lineColors;
    }

    public void addAnnotation(int x1, int y1, int x2, int y2, Color color, int mode) {
        if(mode != MODE_HIGHLIGHT && mode != MODE_LINE) {
            throw new IllegalStateException("Invalid annotation mode: " + mode);
        }
        //x1 y1 is the upper left corner and x2 y2 is the lower right corner
        if(mode == MODE_HIGHLIGHT) {
            highlightCoordinates.add(new ArrayList<Integer>(List.of(x1, y1, x2, y2)));
            highlightColorStrings.add(colorToString(color));
        }
        lineCoordinates.add(new ArrayList<Integer>(List.of(x1, y1, x2, y2)));
        lineColorStrings.add(colorToString(color));

    }
}