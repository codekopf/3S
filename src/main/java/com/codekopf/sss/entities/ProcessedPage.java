package com.codekopf.sss.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ProcessedPage that = (ProcessedPage) o;
        return new EqualsBuilder().append(this.pageURL, that.pageURL).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.pageURL).toHashCode();
    }

    // Custom toString() method over Lombok's generated string for clarity properties
    @Override
    public String toString() {
        return "Processed page : "
                + "status = " + this.pageProcessingStatus + ", "
                + "page = " + this.pageURL + ", "
                + "parent = " + this.parentURL + ", "
                + "words = " + this.wordCount;
    }

}
