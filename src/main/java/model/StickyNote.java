package model;

import java.util.ArrayList;

public class StickyNote {
    private ArrayList<Integer> coordinate;
    private String content;
    public StickyNote(ArrayList<Integer> coordinate, String content) {
        this.coordinate = coordinate;
        this.content = content;
    }
    public ArrayList<Integer> getCoordinate() {
        return coordinate;
    }
    public String getContent() {
        return content;
    }
    public String toString() {
        return "Sticky{" +
                "coordinate=" + coordinate +
                ", content='" + content + '\'' +
                '}';
    }
}
