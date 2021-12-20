package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.*;
import ru.job4j.utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {
    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) {
        String baseUrl = "https://www.sql.ru/forum/job-offers/";
        SqlRuParse parse = new SqlRuParse(new SqlRuDateTimeParser());
        List<Post> posts = parse.list(baseUrl);
        System.out.println(posts.size());
    }

    @Override
    public List<Post> list(String link) {
        List<Post> postList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            try {
                Document doc = Jsoup.connect(String.format(link + "%s", i)).get();
                Elements row = doc.select(".postslisttopic");
                for (Element td : row) {
                    Element href = td.child(0);
                    Post p = detail(href.attr("href"));
                    String title = p.getTitle().toLowerCase();
                    if (title.contains("java") && !title.contains("javascript")) {
                        postList.add(p);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return postList;
    }

    @Override
    public Post detail(String link) {
        Post post = new Post();
        post.setLink(link);
        try {
            Document doc = Jsoup.connect(link).get();
            Element first = doc.selectFirst(".msgTable");
            post.setTitle(first.select(".messageHeader").get(0).ownText());
            post.setDescription(first.select(".msgBody").get(1).ownText());
            var date = first.select(".msgFooter").get(0).ownText().substring(0, 16);
            post.setCreated(dateTimeParser.parse(date));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return post;
    }
}
