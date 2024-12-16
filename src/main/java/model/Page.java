package model;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Page {

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
        List<?> highlightColorStrings = mongoPage.getList("highlightColorStrings", String.class);
        List<?> lineCoordinates = mongoPage.getList("lineCoordinates", List.class);
        List<?> lineColorStrings = mongoPage.getList("lineColorStrings", String.class);

        this.highlightCoordinates = convertMongoCoordinatesToCoordinates(highlightCoordinates);
        this.highlightColorStrings = convertMongoColorStringsToColorStrings(highlightColorStrings);
        this.lineCoordinates = convertMongoCoordinatesToCoordinates(lineCoordinates);
        this.lineColorStrings = convertMongoColorStringsToColorStrings(lineColorStrings);
    }


    private static ArrayList<ArrayList<Integer>> convertMongoCoordinatesToCoordinates(List<?> mongoCoordinates) {
        if(mongoCoordinates == null) {
            return new ArrayList<>();
        }
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
        if(mongoColorStrings == null) {
            return new ArrayList<>();
        }
        ArrayList<String> result = new ArrayList<String>();
        for(Object colorString : mongoColorStrings) {
            if(!(colorString instanceof String)) {
                throw new RuntimeException("Invalid type for colorString");
            }
            result.add((String) colorString);
        }
        return result;
    }


    protected static String colorToString(Color color) {
        return String.format("#%02x%02x%02x%02x",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255),
                (int)(color.getOpacity() * 255));
    }

    protected static Color stringToColor(String colorStr) {
        // Assuming the format is #RRGGBBAA
        int red = Integer.valueOf(colorStr.substring(1, 3), 16);
        int green = Integer.valueOf(colorStr.substring(3, 5), 16);
        int blue = Integer.valueOf(colorStr.substring(5, 7), 16);
        int alpha = Integer.valueOf(colorStr.substring(7, 9), 16);
        return Color.rgb(red, green, blue, alpha / 255.0);
    }

    protected ArrayList<String> getHighlightColorStrings() {
        return highlightColorStrings;
    }

    protected ArrayList<String> getLineColorStrings() {
        return lineColorStrings;
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

    protected void onPageHighlightAdded(ArrayList<Integer> coordinate, Color color) {
        Runnable runnable = new Runnable() {
            ArrayList<Integer> localCoordinate = new ArrayList<>(coordinate);
            Color localColor = stringToColor(colorToString(color));
            @Override
            public void run() {
                highlightCoordinates.add(localCoordinate);
                highlightColorStrings.add(colorToString(localColor));
            }
        };
        Platform.runLater(runnable);
    }

    protected void onPageUnderlineAdded(ArrayList<Integer> coordinate, Color color) {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                lineCoordinates.add(coordinate);
//                lineColorStrings.add(colorToString(color));
//            }
//        };
//        Platform.runLater(runnable);
    }

    protected void onPageHighlightRemoved(ArrayList<ArrayList<Integer>> remainingCoordinates, ArrayList<Color> remainingColors) {
        Runnable runnable = new Runnable() {
            ArrayList<ArrayList<Integer>> localCoordinates = new ArrayList<>(remainingCoordinates);
            ArrayList<Color> localColors = new ArrayList<>(remainingColors);
            @Override
            public void run() {
                highlightCoordinates = localCoordinates;
                highlightColorStrings = new ArrayList<String>();
                for(Color color : localColors) {
                    highlightColorStrings.add(colorToString(color));
                }
            }
        };
        Platform.runLater(runnable);
    }

    protected void onPageUnderlineRemoved(ArrayList<ArrayList<Integer>> remainingCoordinates, ArrayList<Color> remainingColors) {
//        Platform.runLater(() -> {
//            lineCoordinates = remainingCoordinates;
//            lineColorStrings = new ArrayList<String>();
//        });
    }
}