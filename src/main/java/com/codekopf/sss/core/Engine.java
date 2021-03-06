package com.codekopf.sss.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.codekopf.sss.entities.LinkDataStructure;
import com.codekopf.sss.entities.PageProcessingStatus;
import com.codekopf.sss.entities.ProcessedPage;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import static com.codekopf.sss.entities.PageProcessingStatus.OK;

@Log4j2
@Service
public class Engine {

    private static final int DEFAULT_PROCESSING_CACHE_PRINT = 25; // TODO: This should be program entry argument
    private static final String CSV_HEADER_STATUS = "status";
    private static final String CSV_HEADER_PAGE_URL = "page URL";
    private static final String CSV_HEADER_PARENT_URL = "parent URL";
    private static final String CSV_HEADER_WORD_COUNT = "word count";
    private static final String[] HEADERS = { CSV_HEADER_STATUS, CSV_HEADER_PAGE_URL, CSV_HEADER_PARENT_URL, CSV_HEADER_WORD_COUNT};

    // TODO Rework this to ProcessingLinkCache - new class - wrapper around these classes
    private final Set<LinkDataStructure> unprocessedLinks = new HashSet<>();
    private final Set<LinkDataStructure> processedLinks = new HashSet<>();

    private final Set<ProcessedPage> externalPages = new HashSet<>(); // TODO: I shall track this too
    private final Set<ProcessedPage> processedPages = new HashSet<>();

    private static final int ONE_SECOND_IN_MILISECONDS = 1000;
    private static final boolean RANDOM_HALF_MINUTE_SLEEP_DELAY = false;

    private static final boolean startFromCSVFile = false; // TODO: This should be program entry argument

    @Value("${domain}")
    private String domain;

    @Value("${websiteURL}")
    private String websiteURL;

    @EventListener(ApplicationReadyEvent.class)
    public void crawlSiteAfterStartup() {

        if(startFromCSVFile) {
            final String fileName = "DEFINE_CSV_FILE_NAME_HERE.csv";
            loadFromCSV(fileName);
        }
        val seed = new LinkDataStructure(this.websiteURL, this.websiteURL);
        this.unprocessedLinks.add(seed);

        while(!this.unprocessedLinks.isEmpty()) {
            val unprocessedLink = this.unprocessedLinks.iterator().next();
            log.info("Processing " + unprocessedLink.getPageURL());

            try {
                val document = Jsoup.connect(unprocessedLink.getPageURL()).get();

                // TODO: external links
                processPageLinks(unprocessedLink.getPageURL(), document);

                val wordCount = document.text().split("\\s+").length;

                val processedPage = ProcessedPage.createOKPageFrom(unprocessedLink, wordCount); // TODO Add 404 , size, number of images, redirect
                this.processedPages.add(processedPage);
                log.info(unprocessedLink + " was processed.");
            } catch (HttpStatusException e) {
                this.processedPages.add(ProcessedPage.createProblematicPageFrom(unprocessedLink)); // TODO: add info what was the problem - 3rd data structure - Problematic Page
                log.error(e.getStatusCode() + " status for " + unprocessedLink);
                printProcessingCache();
            } catch (IOException e) {
                this.processedPages.add(ProcessedPage.createProblematicPageFrom(unprocessedLink)); // TODO: add info what was the problem - 3rd data structure - Problematic Page
                log.error("Issue with crawling " + unprocessedLink, e);
                printProcessingCache();
            }

            this.unprocessedLinks.remove(unprocessedLink);
            this.processedLinks.add(unprocessedLink);

            if (this.processedPages.size() % DEFAULT_PROCESSING_CACHE_PRINT == 0) {
                printProcessingCache();
            }

            try {
                if(RANDOM_HALF_MINUTE_SLEEP_DELAY) {
                    val delay = ThreadLocalRandom.current().nextInt(1, 30 + 1) * 1000L;
                    log.info("Delay: {}s", delay / ONE_SECOND_IN_MILISECONDS);
                    Thread.sleep(delay);
                } else {
                    Thread.sleep(ONE_SECOND_IN_MILISECONDS);
                }

                // TODO: Option to select really long delay
                //Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        printProcessingCache();

        log.info("List of pages for " + this.domain + ":");
        this.processedPages.forEach(log::info);
        System.exit(0);
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

        val tempLinkDataStructure = new LinkDataStructure(parentURL, link);
        val isInUnprocessedLinks = this.unprocessedLinks.contains(tempLinkDataStructure);
        val isInProcessedLinks = this.processedLinks.contains(tempLinkDataStructure);

        if (isInProcessedLinks || isInUnprocessedLinks) { // TODO Rework
            // DO NOTHING
        } else {

            val isNotSpecialHTTPRequest = !link.contains("?");

            val doesNotContainSignHash = !link.contains("#"); // TODO: If remove suffix after # and prefix is not null, check the link if page is not in processed/unprocessed
            val doesNotContainCharacterCombination = !link.contains("gp/"); // This is optional

            if(isNotSpecialHTTPRequest && doesNotContainSignHash && doesNotContainCharacterCombination) {

                val linkStartsWithSlash = link.startsWith("/");
                val linkStartsWithDomain = link.startsWith(this.domain);
                val linkContainsDomainName = link.contains(this.domain);

                if(linkStartsWithSlash || linkStartsWithDomain || linkContainsDomainName) {
                    if(linkStartsWithSlash) {
                        log.info("Link starting with / : " + link);
                        // this.unprocessedLinks.add(this.websiteURL + link); TODO: Figure out what to do with links which start with slash
                    } else {
                        this.unprocessedLinks.add(tempLinkDataStructure);
                    }
                }
            }
        }
    }

    private void printProcessingCache() {

        log.info("List of unprocessed pages:");
        this.unprocessedLinks.forEach(log::info);

        val normalPages = new ArrayList<ProcessedPage>();
        val problematicPages = new ArrayList<ProcessedPage>();

        for(val processedPage : this.processedPages) {
            if(processedPage.getPageProcessingStatus().equals(OK)) {
                normalPages.add(processedPage);
            } else {
                problematicPages.add(processedPage);
            }
        }

        // TODO: Show how much link is in each group

        log.info("List of problematic pages:");
        problematicPages.forEach(log::info);

        log.info("List of processed pages:");
        normalPages.forEach(log::info);

        // TODO: Exit program on success

        createCSVFile();
    }

    public void createCSVFile() {
        try (final FileWriter fileWriter = new FileWriter(this.domain.replace(".", "_") + ".csv");
             final CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader(HEADERS))) {
            this.processedPages.forEach( page -> {
                try {
                    csvPrinter.printRecord(page.getPageProcessingStatus(), page.getPageURL(), page.getParentURL(), page.getWordCount());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            log.error("Could not export link records to file!", e);
        }
    }

    public void loadFromCSV(final String fileName) {
        final File file = new File(fileName);
        try (final CSVParser csvParser = CSVParser.parse(file, StandardCharsets.UTF_8, CSVFormat.DEFAULT.withFirstRecordAsHeader().withHeader(HEADERS))) {
            for (CSVRecord csvRecord : csvParser) {
                this.processedPages.add(new ProcessedPage(csvRecord.get(CSV_HEADER_PAGE_URL),
                                                          csvRecord.get(CSV_HEADER_PARENT_URL),
                                                          PageProcessingStatus.valueOf(csvRecord.get(CSV_HEADER_STATUS)),
                                                          Integer.parseInt(csvRecord.get(CSV_HEADER_WORD_COUNT))));
                this.processedLinks.add(new LinkDataStructure(csvRecord.get(CSV_HEADER_PARENT_URL), csvRecord.get(CSV_HEADER_PAGE_URL)));
            }
        } catch (IOException e) {
            System.exit(-1);
        }
    }
}
