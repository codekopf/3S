package com.codekopf.sss.entities;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.codekopf.sss.entities.PageProcessingStatus.OK;
import static com.codekopf.sss.entities.PageProcessingStatus.PROBLEMATIC;

@Getter
@AllArgsConstructor
public final class ProcessedPage {

    final String pageURL;
    final String parentURL;
    final PageProcessingStatus pageProcessingStatus;
    final int wordCount;

    public static ProcessedPage createOKPageFrom(final LinkDataStructure linkDataStructure, final int wordCount) {
        return new ProcessedPage(linkDataStructure.getPageURL(), linkDataStructure.getParentURL(), OK, wordCount);
    }

    public static ProcessedPage createProblematicPageFrom(final LinkDataStructure linkDataStructure) {
        return new ProcessedPage(linkDataStructure.getPageURL(), linkDataStructure.getParentURL(), PROBLEMATIC, 0);
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
        return "Processed page : pageProcessingStatus = " + this.pageProcessingStatus
                + ", pageURL = " + this.pageURL
                + ", parentURL = " + this.parentURL
                + ", wordCount = " + this.wordCount;
    }

}
