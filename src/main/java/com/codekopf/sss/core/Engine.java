package com.codekopf.sss.core;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import lombok.val;

@Log4j2
@Service
public class Engine {

    private Set<String> unprocessedLinks = new HashSet<>();
    private Set<String> processedLinks = new HashSet<>();
    private Set<String> problematicLinks = new HashSet<>();

    @Value("${domain}")
    private String domain;

    @Value("${websiteURL}")
    private String websiteURL;

    @EventListener(ApplicationReadyEvent.class)
    public void crawlSiteAfterStartup() {

        this.unprocessedLinks.add(this.websiteURL);

        while(!this.unprocessedLinks.isEmpty()) {
            val currentlyCrawledPage = this.unprocessedLinks.iterator().next();
            log.info("Processing " + currentlyCrawledPage + " page.");
            try {
                val document = Jsoup.connect(currentlyCrawledPage).get();
                val htmlAnchors = document.select("a[href]");

                val pageLinks = new HashSet<String>();

                htmlAnchors.forEach(htmlAnchor -> {
                    val link = htmlAnchor.attr("href");
                    pageLinks.add(link);
                });

                pageLinks.forEach(this::processLink);
            } catch (IOException e) {
                this.problematicLinks.add(currentlyCrawledPage);
                log.error("Issue with crawling " + currentlyCrawledPage + " page.", e);
                printProcessingCache();
            }

            this.unprocessedLinks.remove(currentlyCrawledPage);
            this.processedLinks.add(currentlyCrawledPage);
        }

        log.info("List of pages for" + this.domain + ":");
        this.processedLinks.forEach(log::info);
    }

    private void processLink(final String link) {
        val isNotInUnprocessedLinks = !this.unprocessedLinks.contains(link);
        val isNotInProcessedLinks = !this.processedLinks.contains(link);
        val isNotInProblematicLinks = !this.problematicLinks.contains(link);

        val isNotSpecialHTTPRequest = !link.contains("?");

        val doesNotContainSignHash = !link.contains("#");
        val doesNotContainCharacterCombination = !link.contains("gp/"); // This is optional

        if(isNotInUnprocessedLinks && isNotInProcessedLinks && isNotInProblematicLinks && isNotSpecialHTTPRequest && doesNotContainSignHash && doesNotContainCharacterCombination) {

            val linkStartsWithSlash = link.startsWith("/");
            val linkStartsWithDomain = link.startsWith(this.domain);
            val linkContainsDomainName = link.contains(this.domain);

            if(linkStartsWithSlash || linkStartsWithDomain || linkContainsDomainName) {
                if(linkStartsWithSlash) {
                    this.unprocessedLinks.add(this.websiteURL + link);
                } else {
                    this.unprocessedLinks.add(link);
                }
            }
        }
    }

    private void printProcessingCache(){
        log.info("List of problematic pages:");
        this.problematicLinks.forEach(log::info);

        log.info("List of unprocessed pages:");
        this.unprocessedLinks.forEach(log::info);

        log.info("List of processed pages:");
        this.processedLinks.forEach(log::info);
    }
}
