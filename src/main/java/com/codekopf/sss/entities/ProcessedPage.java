package com.codekopf.sss.entities;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class ProcessedPage {

    final String parentURL;
    final String pageURL;
    final PageProcessingStatus pageProcessingStatus;

    public static ProcessedPage of(final UnprocessedPage unprocessedPage, final PageProcessingStatus pageProcessingStatus) {
        return new ProcessedPage(unprocessedPage.getParentURL(), unprocessedPage.getPageURL(), pageProcessingStatus);
    }

    // Note:
    //  Equals and HashCode - Two objects are equal only when their pageURLs match. No other parameter is important for
    //  equality comparison.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessedPage that = (ProcessedPage) o;
        return this.pageURL.equals(that.pageURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pageURL);
    }

    @Override
    public String toString() {
        return "Processed page : pageProcessingStatus = " + this.pageProcessingStatus + ", pageURL = " + this.pageURL + ", parentURL = " + this.parentURL;
    }

}
