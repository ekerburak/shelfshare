package model;

import javafx.application.Platform;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Page {

    private final int pageNumber;
    private final String image;
    private ArrayList<ArrayList<Integer>> highlightCoordinates;
    private ArrayList<ArrayList<Integer>> lineCoordinates;
    private ArrayList<StickyNote> stickyNotes;

    public Page(String image, int pageNumber) {
        this.image = image;
        this.pageNumber = pageNumber;
        highlightCoordinates = new ArrayList<ArrayList<Integer>>();
        lineCoordinates = new ArrayList<ArrayList<Integer>>();
        stickyNotes = new ArrayList<StickyNote>();
    }

    protected Page(Document mongoPage, int pageNumber) {
        this.image = mongoPage.getString("image");
        this.pageNumber = pageNumber;

        List<?> highlightCoordinates = mongoPage.getList("highlightCoordinates", List.class);
        List<?> lineCoordinates = mongoPage.getList("lineCoordinates", List.class);

        this.highlightCoordinates = convertMongoCoordinatesToCoordinates(highlightCoordinates);
        this.lineCoordinates = convertMongoCoordinatesToCoordinates(lineCoordinates);

        List<?> mongoStickyNotes = mongoPage.getList("stickyNotes", List.class);

        this.stickyNotes = convertMongoStickyNotesToStickyNotes(mongoStickyNotes);
    }

    private ArrayList<StickyNote> convertMongoStickyNotesToStickyNotes(List<?> mongoStickyNotes) {
        if(mongoStickyNotes == null) {
            return new ArrayList<>();
        }

        ArrayList<StickyNote> result = new ArrayList<StickyNote>();

        for(Object mongoStickyNote : mongoStickyNotes) {
            assert mongoStickyNote instanceof Document;

            ArrayList<Integer> coordinate = new ArrayList<Integer>();

            for(Integer coordinateData : ((Document) mongoStickyNote).getList("coordinate", Integer.class)) {
                coordinate.add((Integer) coordinateData);
            }

            result.add(new StickyNote(coordinate, ((Document) mongoStickyNote).getString("content")));
        }

        return result;
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
    public ArrayList<StickyNote> getStickyNotes() {
        return stickyNotes;
    }

    protected void onPageHighlightAdded(ArrayList<Integer> coordinate) {
        Runnable runnable = new Runnable() {
            ArrayList<Integer> localCoordinate = new ArrayList<>(coordinate);
            @Override
            public void run() {
                highlightCoordinates.add(localCoordinate);
            }
        };
        Platform.runLater(runnable);
    }

    protected void onPageUnderlineAdded(ArrayList<Integer> coordinate) {
        Runnable runnable = new Runnable() {
            ArrayList<Integer> localCoordinate = new ArrayList<>(coordinate);
            @Override
            public void run() {
                lineCoordinates.add(localCoordinate);
            }
        };
        Platform.runLater(runnable);
    }

    protected void onPageHighlightRemoved(ArrayList<ArrayList<Integer>> remainingCoordinates) {
        Runnable runnable = new Runnable() {
            ArrayList<ArrayList<Integer>> localCoordinates = new ArrayList<>(remainingCoordinates);
            @Override
            public void run() {
                highlightCoordinates = localCoordinates;
            }
        };
        Platform.runLater(runnable);
    }

    protected void onPageUnderlineRemoved(ArrayList<ArrayList<Integer>> remainingCoordinates) {
        Runnable runnable = new Runnable() {
            ArrayList<ArrayList<Integer>> localCoordinates = new ArrayList<>(remainingCoordinates);
            @Override
            public void run() {
                lineCoordinates = localCoordinates;
            }
        };
        Platform.runLater(runnable);
    }

    public void onPageStickyAdded(StickyNote stickyNote) {
        Runnable runnable = new Runnable() {
            StickyNote localStickyNote = stickyNote;
            @Override
            public void run() {
                stickyNotes.add(localStickyNote);
            }
        };
        Platform.runLater(runnable);
    }
}