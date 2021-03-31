package com.codekopf.sss.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class LinkDataStructure {

    final String parentURL;
    final String pageURL;

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
        LinkDataStructure that = (LinkDataStructure) o;
        return new EqualsBuilder().append(this.pageURL, that.pageURL).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(pageURL).toHashCode();
    }

    @Override
    public String toString() {
        return "Unprocessed link structure : pageURL = " + this.pageURL + ", parentURL = " + this.parentURL;
    }
}
