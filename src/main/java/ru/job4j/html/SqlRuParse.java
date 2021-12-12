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
    private final PostParser postParser;
    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
        this.postParser = new PostParser(dateTimeParser);
    }

    public static void main(String[] args) {
        String baseUrl = "https://www.sql.ru/forum/job-offers";
        SqlRuParse parse = new SqlRuParse(new SqlRuDateTimeParser());
        List<Post> posts = parse.list(baseUrl);
        System.out.println(posts.size());
    }

    @Override
    public List<Post> list(String link) {
        List<Post> postList = new ArrayList<>();
        String url = link;
        int page = 1;
        int maxPage = page;
        do {
            try {
                Document doc = Jsoup.connect(url).get();
                Elements row = doc.select(".postslisttopic");
                for (Element td : row) {
                    Element href = td.child(0);
                    Post p = detail(href.attr("href"));
                    postList.add(p);
                }
                if (page == 1) {
                    row = doc.select(".sort_options");
                    for (Element td : row) {
                        var textFromElement = td.text();
                        if (!textFromElement.isEmpty()) {
                            String[] arrayOfPagesAndPostsData = textFromElement.split(" ");
                            maxPage = Integer.parseInt(arrayOfPagesAndPostsData[12]);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            page++;
            if (page != 1) {
                url = link + "/" + page;
            }
        } while (page <= maxPage && page <= 5);
        return postList;
    }

    @Override
    public Post detail(String link) {
        Post p = null;
        try {
            p = postParser.parseFromUrl(link);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }
}
