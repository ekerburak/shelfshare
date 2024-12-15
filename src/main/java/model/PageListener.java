package model;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public interface PageListener {
    void onPageHighlightAdded(ArrayList<Integer> coordinate, Color color);
    void onPageUnderlineAdded(ArrayList<Integer> coordinate, Color color);
    void onPageHighlightRemoved(ArrayList<ArrayList<Integer>> remainingCoordinates, ArrayList<Color> remainingColors);
    void onPageUnderlineRemoved(ArrayList<ArrayList<Integer>> remainingCoordinates, ArrayList<Color> remainingColors);
}
