package com.codekopf.sss.core;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.codekopf.sss.entities.ProcessedPage;
import com.codekopf.sss.entities.UnprocessedPage;
import lombok.extern.log4j.Log4j2;
import lombok.val;

@Log4j2
@Service
public class Engine {

    private Set<UnprocessedPage> unprocessedPages = new HashSet<>();

    private Set<ProcessedPage> externalPages = new HashSet<>(); // TODO: I shall track this too
    private Set<ProcessedPage> processedPages = new HashSet<>();
    private Set<ProcessedPage> problematicPages = new HashSet<>();

    @Value("${domain}")
    private String domain;

    @Value("${websiteURL}")
    private String websiteURL;

    @EventListener(ApplicationReadyEvent.class)
    public void crawlSiteAfterStartup() {

        val seed = new UnprocessedPage(this.websiteURL, this.websiteURL);

        this.unprocessedPages.add(seed);

        while(!this.unprocessedPages.isEmpty()) {
            val currentlyProcessedUnprocessedPage = this.unprocessedPages.iterator().next();
            log.info("Processing " + currentlyProcessedUnprocessedPage.getPageURL());

            try {
                val document = Jsoup.connect(currentlyProcessedUnprocessedPage.getPageURL()).get();
                processPageLinks(currentlyProcessedUnprocessedPage.getParentURL(), document);
                val processedPage = ProcessedPage.from(currentlyProcessedUnprocessedPage); // TODO Add 404 , size, number of images, redirect
                this.processedPages.add(processedPage);
                log.info(currentlyProcessedUnprocessedPage + " was processed.");
                // TODO: external links
            } catch (HttpStatusException e) {
                this.problematicPages.add(ProcessedPage.from(currentlyProcessedUnprocessedPage)); // TODO: add info what was the problem - 3rd data structure - Problematic Page
                log.error(e.getStatusCode() + " status for " + currentlyProcessedUnprocessedPage);
                printProcessingCache();
            } catch (IOException e) {
                this.problematicPages.add(ProcessedPage.from(currentlyProcessedUnprocessedPage)); // TODO: add info what was the problem - 3rd data structure - Problematic Page
                log.error("Issue with crawling " + currentlyProcessedUnprocessedPage, e);
                printProcessingCache();
            }

            this.unprocessedPages.remove(currentlyProcessedUnprocessedPage);
        }

        printProcessingCache();

        log.info("List of pages for " + this.domain + ":");
        this.processedPages.forEach(log::info);
    }

    private void processPageLinks(final String parentURL, final Document document) {
        val pageLinks = extractAllLinksFromDocument(document);
        pageLinks.forEach(link -> processLink(parentURL, link));
    }

    private  Set<String> extractAllLinksFromDocument(Document document) {
        val htmlAnchors = document.select("a[href]");
        val pageLinks = new HashSet<String>();
        htmlAnchors.forEach(htmlAnchor -> {
            val link = htmlAnchor.attr("href");
            pageLinks.add(link);
        });
        return pageLinks;
    }

    // TODO - This method is doing more than it should be - it is first comparing links if the pages exist - then it is extracting links
    // It should be doing only one thing. Decouple/Refactor
    private void processLink(final String parentURL, final String link) {

        val newUnprocessedPage = new UnprocessedPage(parentURL, link);
        val processedPage = new ProcessedPage(parentURL, link);

        val isNotInUnprocessedLinks = !this.unprocessedPages.contains(newUnprocessedPage);
        val isNotInProcessedLinks = !this.processedPages.contains(processedPage);
        val isNotInProblematicLinks = !this.problematicPages.contains(processedPage);

        val isNotSpecialHTTPRequest = !link.contains("?");

        val doesNotContainSignHash = !link.contains("#"); // TODO: If remove suffix after # and prefix is not null, check the link if page is not in processed/unprocessed
        val doesNotContainCharacterCombination = !link.contains("gp/"); // This is optional

        if(isNotInUnprocessedLinks && isNotInProcessedLinks && isNotInProblematicLinks && isNotSpecialHTTPRequest && doesNotContainSignHash && doesNotContainCharacterCombination) {

            val linkStartsWithSlash = link.startsWith("/");
            val linkStartsWithDomain = link.startsWith(this.domain);
            val linkContainsDomainName = link.contains(this.domain);

            if(linkStartsWithSlash || linkStartsWithDomain || linkContainsDomainName) {
                if(linkStartsWithSlash) {
                    log.info("Link starting with / : " + link);
                    // this.unprocessedLinks.add(this.websiteURL + link); TODO: Figure out what to do with links which start with slash
                } else {
                    this.unprocessedPages.add(newUnprocessedPage);
                }
            }
        }
    }

    private void printProcessingCache() {
        log.info("List of problematic pages:");
        this.problematicPages.forEach(log::info);

        log.info("List of unprocessed pages:");
        this.unprocessedPages.forEach(log::info);

        log.info("List of processed pages:");
        this.processedPages.forEach(log::info);
    }
}
