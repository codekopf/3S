package com.codekopf.sss.entities;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class ProcessedPage {

    final String parentURL;
    final String pageURL;

    public static ProcessedPage from(final UnprocessedPage unprocessedPage) {
        return new ProcessedPage(unprocessedPage.getParentURL(), unprocessedPage.getPageURL());
    }

    // Note:
    //  Equals and HashCode - Two objects are equal only when their pageURLs match. No other parameter is important for
    //  equality comparison.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessedPage that = (ProcessedPage) o;
        return pageURL.equals(that.pageURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageURL);
    }

    @Override
    public String toString() {
        return "Processed page : pageURL = " + pageURL + ", parentURL = " + parentURL;
    }
}
