package model;

import javafx.scene.paint.Color;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public class Book {
    private ObjectId ID;
    private String name;
    private final String uploaderName;
    private boolean isDownloadable;
    private int pageCount; //pages are numbered from 0 to pageCount - 1 inclusive
    private ObjectId discussionChatID;
    private int coverImageOption;
    private final Object lock = new Object();

    private static final int MAX_BUFFER_SIZE = 11;
    private ArrayList<Page> pageBuffer;

    private ArrayList<PageListener> currentPageListeners;
    private int currentPageIndex;

    public ObjectId getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public boolean getIsDownloadable() {
        return isDownloadable;
    }

    public int getPageCount() {
        return pageCount;
    }

    public Chat getDiscussionChat() {
        return ChatCollection.getChat(discussionChatID);
    }

    public int getCoverImageOption() {
        return coverImageOption;
    }

    public void setName(String name) {
        this.name = name;
        BookCollection.updateProperties(this);
    }

    public void setCoverImageOption(int coverImageOption) {
        this.coverImageOption = coverImageOption;
        BookCollection.updateProperties(this);
    }

    /**
     * WARNING! (CALL REPAINT AFTER THIS. IT WILL NOT NOTIFY LISTENERS)
     * Attempts to navigate to the next page in the book.
     * This method checks if the current page is not the last page in the buffer
     * and advances the page index if possible. If the current page is the last
     * in the buffer but not the last in the book, it fetches additional pages
     * from the BookCollection and updates the buffer accordingly.
     *
     * @return true if the navigation to the next page was successful, or additional
     *         pages were successfully loaded into the buffer; false if already at the
     *         last page of the book.
     */
    public boolean goToNextPage() {
        synchronized (lock) {
            if (currentPageIndex < pageBuffer.size() - 1) {
                currentPageIndex++;
                return true;
            }
            int currentPageNumber = pageBuffer.get(currentPageIndex).getPageNumber();
            if(currentPageNumber == pageCount - 1) {
                return false;
            }

            int pageLow = Math.min(currentPageNumber + 1, pageCount - 1);
            int pageHigh = Math.min(currentPageNumber + MAX_BUFFER_SIZE / 2, pageCount - 1);
            currentPageIndex++;

            ArrayList<Page> newPages = BookCollection.getPages(ID, pageLow, pageHigh);
            pageBuffer.addAll(newPages);

            while(pageBuffer.size() > MAX_BUFFER_SIZE) {
                pageBuffer.remove(0);
                currentPageIndex--;
            }

            return true;
        }
    }

    /**
     * Navigates to the previous page in the buffer for the book.
     * This method adjusts the current page index to point to the previous page if possible.
     * If the current page is the first page in the buffer, it attempts to fetch previous pages
     * from the BookCollection and updates the buffer accordingly.
     *
     * @return true if the navigation to the previous page was successful, false if already
     *         at the beginning of the book and no further previous pages are available.
     */
    public boolean goToPrevPage() {
       synchronized (lock) {
           if (currentPageIndex > 0) {
               currentPageIndex--;
               return true;
           }
           int currentPageNumber = pageBuffer.get(currentPageIndex).getPageNumber();
           //already at the first page
           if(currentPageNumber == 0) {
               return false;
           }

           int pageLow = Math.max(currentPageNumber - MAX_BUFFER_SIZE / 2, 0);
           int pageHigh = Math.max(currentPageNumber - 1, 0);
           currentPageIndex += (pageHigh - pageLow + 1);
           currentPageIndex--;

           ArrayList<Page> newPages = BookCollection.getPages(ID, pageLow, pageHigh);
           ArrayList<Page> pageBufferTemp = pageBuffer;
           pageBuffer = newPages;
           pageBuffer.addAll(pageBufferTemp);

           while(pageBuffer.size() > MAX_BUFFER_SIZE) {
               pageBuffer.remove(pageBuffer.size() - 1);
           }

           return true;
       }
    }


    public void goToFirstPage() {
        synchronized (lock) {
            pageBuffer = BookCollection.getPages(ID, 0, Math.min(MAX_BUFFER_SIZE - 1, pageCount - 1));
            currentPageIndex = 0;
        }
    }

    public Page getCurrentPage() {
        synchronized (lock) {
            return pageBuffer.get(currentPageIndex);
        }
    }

    public Book(ObjectId ID, String name, String uploaderName, boolean isDownloadable, int pageCount, ObjectId discussionChatID, int coverImageOption) {
        this.ID = ID;
        this.name = name;
        this.uploaderName = uploaderName;
        this.isDownloadable = isDownloadable;
        this.pageCount = pageCount;
        this.discussionChatID = discussionChatID;
        pageBuffer = new ArrayList<>();
        this.coverImageOption = coverImageOption;
    }

    private int getPageIndexAtBuffer(int pageNumber) {
        int pageLow = pageBuffer.get(0).getPageNumber();
        int pageHigh = pageBuffer.get(pageBuffer.size() - 1).getPageNumber();
        if(pageNumber < pageLow || pageNumber > pageHigh) {
            return -1;
        }
        return pageNumber - pageLow;
    }

    //notifies the listeners only from the incoming db changes

    protected void notifyPageHighlightAdded(int pageNumber, ArrayList<Integer> coordinate) {
        int pageIndex = getPageIndexAtBuffer(pageNumber);
        if(pageIndex == -1) {
            return;
        }
        if(pageIndex == currentPageIndex) {
            for(PageListener listener : currentPageListeners) {
                System.out.println("NOTIFY PAGE HIGHLIGHT ADDED");
                listener.onPageHighlightAdded(coordinate);
            }
        }
        pageBuffer.get(pageIndex).onPageHighlightAdded(coordinate);
    }

    protected void notifyPageUnderlineAdded(int pageNumber, ArrayList<Integer> coordinate) {
        int pageIndex = getPageIndexAtBuffer(pageNumber);
        if(pageIndex == -1) {
            return;
        }
        pageBuffer.get(pageIndex).onPageUnderlineAdded(coordinate);
        if(pageIndex == currentPageIndex) {
            for(PageListener listener : currentPageListeners) {
                listener.onPageUnderlineAdded(coordinate);
            }
        }
    }

    protected void notifyPageHighlightRemoved(int pageNumber, ArrayList<ArrayList<Integer>> remainingCoordinates) {
        int pageIndex = getPageIndexAtBuffer(pageNumber);
        if(pageIndex == -1) {
            return;
        }
        pageBuffer.get(pageIndex).onPageHighlightRemoved(remainingCoordinates);
        if(pageIndex == currentPageIndex) {
            for(PageListener listener : currentPageListeners) {
                listener.onPageHighlightRemoved(remainingCoordinates);
            }
        }
    }

    protected void notifyPageUnderlineRemoved(int pageNumber, ArrayList<ArrayList<Integer>> remainingCoordinates) {
        int pageIndex = getPageIndexAtBuffer(pageNumber);
        if(pageIndex == -1) {
            return;
        }
        pageBuffer.get(pageIndex).onPageUnderlineRemoved(remainingCoordinates);
        if(pageIndex == currentPageIndex) {
            for(PageListener listener : currentPageListeners) {
                listener.onPageUnderlineRemoved(remainingCoordinates);
            }
        }
    }

    public void addCurrentPageListener(PageListener listener) {
        if(currentPageListeners == null) {
            currentPageListeners = new ArrayList<>();
        }
        currentPageListeners.add(listener);
    }

    public void addHighlightToCurrentPage(ArrayList<Integer> coordinate) {
        int currentPageNumber = getCurrentPage().getPageNumber();
        BookCollection.addHighlightToPage(ID, currentPageNumber, coordinate);
    }

    public void addUnderlineToCurrentPage(ArrayList<Integer> coordinate) {
        int currentPageNumber = getCurrentPage().getPageNumber();
        BookCollection.addUnderlineToPage(ID, currentPageNumber, coordinate);
    }

    public void removeHighlightFromCurrentPage(ArrayList<Integer> coordinate) {
        int currentPageNumber = getCurrentPage().getPageNumber();
        BookCollection.removeHighlightFromPage(ID, currentPageNumber, coordinate);
    }

    public void removeUnderlineFromCurrentPage(ArrayList<Integer> coordinate) {
        int currentPageNumber = getCurrentPage().getPageNumber();
        BookCollection.removeUnderlineFromPage(ID, currentPageNumber, coordinate);
    }

    public void startListening() {
        BookCollection.setChangeStream(this);
    }


    protected void notifyPageStickyAdded(int modifiedPageNumber, StickyNote stickyNote) {
        int pageIndex = getPageIndexAtBuffer(modifiedPageNumber);
        if(pageIndex == -1) {
            return;
        }
        pageBuffer.get(pageIndex).onPageStickyAdded(stickyNote);
        if(pageIndex == currentPageIndex) {
            for(PageListener listener : currentPageListeners) {
                listener.onPageStickyAdded(stickyNote);
            }
        }
    }

    protected void notifyPageStickyRemoved(int pageNumber, ArrayList<StickyNote> remainingStickies) {
        int pageIndex = getPageIndexAtBuffer(pageNumber);
        if(pageIndex == -1) {
            return;
        }
        pageBuffer.get(pageIndex).onPageStickyRemoved(remainingStickies);
        if(pageIndex == currentPageIndex) {
            for(PageListener listener : currentPageListeners) {
                listener.onPageStickyRemoved(remainingStickies);
            }
        }
    }

    public void addStickyToCurrentPage(StickyNote s) {
        int currentPageNumber = getCurrentPage().getPageNumber();
        BookCollection.addStickyToPage(ID, currentPageNumber, s);
    }

    public void removeStickyFromCurrentPage(StickyNote s) {
        int currentPageNumber = getCurrentPage().getPageNumber();
        BookCollection.removeStickyFromPage(ID, currentPageNumber, s);
    }
}