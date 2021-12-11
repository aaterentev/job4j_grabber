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

    public static void main(String[] args)  {
        String baseUrl = "https://www.sql.ru/forum/job-offers";
        SqlRuParse parse = new SqlRuParse(new SqlRuDateTimeParser());
        List<Post> posts = parse.list(baseUrl);
        posts.forEach(System.out::println);
        System.out.println(posts.size());
    }

    @Override
    public List<Post> list(String link) {
        List<Post> postList = new ArrayList<>();
        String url = link;
        int i = 1;
        int j = 2;
        do {
            try {
                Document doc = Jsoup.connect(url).get();
                Elements row = doc.select(".postslisttopic");
                for (Element td : row) {
                    Element href = td.child(0);
                    Post p = detail(href.attr("href"));
                    postList.add(p);
                }
                if (i == 1) {
                    row = doc.select(".sort_options");
                    j = row.stream().flatMap(x -> x.getElementsByAttributeValueContaining(
                                            "href", link
                                    )
                                    .stream())
                            .map(x -> {
                                if (!x.text().isEmpty() && !x.text().equals("")) {
                                    return Integer.parseInt(x.text());
                                }
                                return -1;
                            })
                            .reduce(-1, (x, y) -> x > y ? x : y);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
            if (i != 1) {
                url = link + "/" + i;
            }
        } while (i < j && i < 5);
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
