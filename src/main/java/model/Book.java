package model;

import java.util.ArrayList;

public class Book {
    private final String ID;
    private String name;
    private final String uploaderName;
    private boolean isDownloadable;
    private final int pageCount; //pages are numbered from 0 to pageCount - 1 inclusive
    private final String discussionChatID;


    private static final int MAX_BUFFER_SIZE = 11; //must be odd-numbered for simplicity
    private static ArrayList<PageListener> pageListeners;

    //different from pageNumber in Page. this denotes the position of the current page relative to the buffer.
    private int currentPageIndex;

    private ArrayList<Page> pageBuffer;

    protected void notifyListeners() {
        for (PageListener listener : pageListeners) {
            listener.onPageModified();
        }
    }

    public String getID() {
        return ID;
    }

    public boolean getIsDownloadable() {
        return isDownloadable;
    }

    public String getName() {
        return name;
    }

    public Page getCurrentPage() {
        return pageBuffer.get(currentPageIndex);
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getDiscussionChatID() {
        return discussionChatID;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setIsDownloadable(boolean isDownloadable) {
        this.isDownloadable = isDownloadable;
        BookCollection.updateProperties(this);
    }

    public void setName(String name) {
        this.name = name;
        BookCollection.updateProperties(this);
    }

    protected Book(String ID, String name, String uploaderName, boolean isDownloadable, int pageCount, String discussionChatID) {
        this.ID = ID;
        this.name = name;
        this.uploaderName = LoggedInUser.getInstance().getUsername();
        this.isDownloadable = isDownloadable;
        this.pageCount = pageCount;
        this.discussionChatID = discussionChatID;
        pageListeners = new ArrayList<PageListener>();
        goToFirstPage();
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
        ArrayList<Page> newPages = BookCollection.getPages(ID, pageLow, pageHigh);
        pageBuffer.addAll(newPages);
        while(pageBuffer.size() > MAX_BUFFER_SIZE) {
            pageBuffer.remove(0);
            currentPageIndex--;
        }
        return true;
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
        currentPageIndex += pageHigh - pageLow + 1;

        ArrayList<Page> newPages = BookCollection.getPages(ID, pageLow, pageHigh);
        ArrayList<Page> pageBufferTemp = pageBuffer;
        pageBuffer = newPages;
        pageBuffer.addAll(pageBufferTemp);

        while(pageBuffer.size() > MAX_BUFFER_SIZE) {
            pageBuffer.remove(pageBuffer.size() - 1);
        }

        return true;
    }

    public void goToFirstPage() {
        pageBuffer = BookCollection.getPages(ID, 0, Math.min(MAX_BUFFER_SIZE - 1, pageCount - 1));
        currentPageIndex = 0;
    }

    public void addPageListener(PageListener listener) {
        pageListeners.add(listener);
    }
}
