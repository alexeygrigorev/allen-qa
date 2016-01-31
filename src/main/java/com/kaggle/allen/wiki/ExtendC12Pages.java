package com.kaggle.allen.wiki;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

// backlinks
///w/api.php?action=query&format=json&generator=backlinks&gbltitle=Beta%20decay&gblnamespace=0&gbldir=ascending
public class ExtendC12Pages {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        List<String> ck12Topics = FileUtils.readLines(new File("data/ck12-topics.txt"));

        List<String> pages = Lists.newArrayList();

        for (String topic : ck12Topics) {
            try {
                // "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=revisions&titles="
                // + titlesParam;
                String url = "https://en.wikipedia.org/w/api.php?action=query&format=json&list=categorymembers&cmlimit=500&cmtitle="
                        + URLEncoder.encode("Category:" + topic, "UTF-8") + "&cmnamespace=0&cmtype=page";
                System.out.println(topic + " " + url);
                MapWrapper map = readJson(url);
                List<Map<String, ?>> categoryPages = map.getList("query.categorymembers");
                System.out.println(categoryPages);

                pages.addAll(Lists.transform(categoryPages, m -> m.get("title").toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        PrintWriter pw = new PrintWriter("data/ck12-extended.txt");
        pages.forEach(pw::println);
        pw.flush();
        pw.close();
    }

    private static MapWrapper readJson(String baseUrl) throws Exception {
        String result = IOUtils.toString(new URL(baseUrl).openStream());
        @SuppressWarnings("unchecked")
        Map<String, ?> map = mapper.readValue(result, Map.class);
        return new MapWrapper(map);
    }

}
