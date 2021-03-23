package com.codekopf.sss.entities;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class UnprocessedPage {

    final String parentURL;
    final String pageURL;

    // Note:
    //  Equals and HashCode - Two objects are equal only when their pageURLs match. No other parameter is important for
    //  equality comparison.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnprocessedPage that = (UnprocessedPage) o;
        return pageURL.equals(that.pageURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageURL);
    }

    @Override
    public String toString() {
        return "Unprocessed page : pageURL = " + pageURL + ", parentURL = " + parentURL;
    }
}
