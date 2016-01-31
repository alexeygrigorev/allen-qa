package com.kaggle.allen.wiki;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

// backlinks
///w/api.php?action=query&format=json&generator=backlinks&gbltitle=Beta%20decay&gblnamespace=0&gbldir=ascending
public class ExtracAllCk12Pages {

    private static final File missing = new File("data/ck12-missing.txt");
    private static final File redirects = new File("data/ck12-redirects.txt");
    private static final File errors = new File("data/ck12-errors.txt");
    private static final File ck12Processed = new File("ck12-processed.txt");

    private static final ThreadLocal<ObjectMapper> mapper = ThreadLocal.withInitial(ObjectMapper::new);

    public static void main(String[] args) throws Exception {
        FileUtils.touch(errors);
        FileUtils.touch(redirects);
        FileUtils.touch(ck12Processed);
        FileUtils.touch(missing);

        List<String> ck12Topics = FileUtils.readLines(new File("data/ck12-topics.txt"));
        List<String> ck12TopicsExtended = FileUtils.readLines(new File("data/ck12-extended.txt"));
        List<String> ck12Redirects = FileUtils.readLines(redirects);
        List<String> ck12Errors = FileUtils.readLines(errors);

        Set<String> processed = Sets.newHashSet(FileUtils.readLines(ck12Processed));

        Iterable<String> allTopics = Iterables.concat(ck12Topics, ck12TopicsExtended, ck12Redirects, ck12Errors);
        allTopics = Iterables.filter(allTopics, t -> !processed.contains(t));

        List<List<String>> partitions = Lists.newArrayList(Iterables.partition(allTopics, 25));

        List<String> errors = Lists.newArrayList();

        PrintWriter jsonFile = new PrintWriter("wiki-responds.json");
        jsonFile.println("[null");

        for (List<String> partition : partitions) {
            try {
                request(errors, jsonFile, partition);
                FileUtils.writeLines(ck12Processed, partition, true);
            } catch (Exception e) {
                e.printStackTrace();
//                Thread.sleep(10000);
            }
        }

        jsonFile.println("]");
        jsonFile.flush();
        jsonFile.close();

    }

    private static void request(List<String> errors, PrintWriter jsonFile, List<String> partition) throws Exception {
        String titlesParam = String.join("|", partition);
        String url = "https://en.wikipedia.org/w/api.php?action=query&format=json&"
                + "prop=revisions&rvprop=content&redirects&titles=" + URLEncoder.encode(titlesParam, "UTF-8");

        System.out.println(url);
        String wikiJson = wiki(url);
        jsonFile.print(",");
        jsonFile.println(wikiJson);

        MapWrapper map = jsonToMap(wikiJson);

        List<Map<String, ?>> qRedirects = map.getList("query.redirects");
        List<String> redirects = qRedirects.stream().map(m -> m.get("to").toString()).collect(Collectors.toList());

        List<String> missingPages = Lists.newArrayList();
        @SuppressWarnings("unchecked")
        Collection<Map<String, ?>> pages = (Collection<Map<String, ?>>) map.getMap("query.pages").values();
        for (Map<String, ?> page : pages) {
            if (page.containsKey("missing")) {
                missingPages.add(page.get("title").toString());
                continue;
            }

            // System.out.println(page);
            Map<String, ?> rev = new MapWrapper(page).getList("revisions").get(0);
            String text = rev.get("*").toString();
            String originalTitle = page.get("title").toString();
            String title = originalTitle;

            title = title.replace('/', '_');
            title = title.replace(':', '_');

            try {
                FileUtils.write(new File("data/wiki/" + title + ".wiki"), text, "UTF-8");
                FileUtils.write(ck12Processed, originalTitle + "\n", "UTF-8", true);
            } catch (Exception e) {
                errors.add(title);
                e.printStackTrace();
            }
        }

        FileUtils.writeLines(missing, missingPages, true);
        FileUtils.writeLines(ExtracAllCk12Pages.redirects, redirects, true);

        Thread.sleep(1500);
    }

    private static MapWrapper jsonToMap(String result) throws IOException, JsonParseException, JsonMappingException {
        @SuppressWarnings("unchecked")
        Map<String, ?> map = mapper.get().readValue(result, Map.class);
        return new MapWrapper(map);
    }

    private static String wiki(String baseUrl) throws IOException, MalformedURLException {
        String result = IOUtils.toString(new URL(baseUrl).openStream());
        return result;
    }

}
