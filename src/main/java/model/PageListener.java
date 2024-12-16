package model;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public interface PageListener {
    void onPageHighlightAdded(ArrayList<Integer> coordinate);
    void onPageUnderlineAdded(ArrayList<Integer> coordinate);
    void onPageHighlightRemoved(ArrayList<ArrayList<Integer>> remainingCoordinates);
    void onPageUnderlineRemoved(ArrayList<ArrayList<Integer>> remainingCoordinates);
}
