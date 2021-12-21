package ru.job4j.grabber;

import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Post - класс реализующий модель данных поста с сайта Sql.ru
 * @apiNote
 * id - индекс, присваиваемый хранилищем;
 * title - заголовок;
 * link - ссылка на источник;
 * description - описание;
 * created - дата создания (тип: LocalDateTime);
 * @author Alex Ter (ShaDar-ru)
 * @version 1.0
 */
public class Post {
    private static final String NL = System.lineSeparator();
    private static final String DATE_TIME_PATTERN = "d M yyyy HH:mm";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    private int id;
    private String title;
    private String link;
    private String description;
    private LocalDateTime created;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    /**
     * Метод выводит текст поста в оформленном виде
     *
     * @return читабельный вид поста
     */
    @Override
    public String toString() {
        return String.format(
                "Post:%s{%sid: %d, %stitle: %s,%slink: %s,%sdescription: %s,%screated: %s%s}",
                NL, NL, id, NL, title, NL, link, NL, description, NL,
                created.format(FORMATTER), NL);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Post p = (Post) o;

        if (id != p.id) {
            return false;
        }

        return Objects.equals(title, p.title) && Objects.equals(link, p.link)
                && Objects.equals(created, p.created);
    }

    @Override
    public int hashCode() {
        int rsl = id;
        rsl = 31 * rsl + (title == null ? 0 : title.hashCode());
        rsl = 31 * rsl + (link == null ? 0 : link.hashCode());
        rsl = 31 * rsl + (created == null ? 0 : created.hashCode());
        return rsl;
    }

    /**
     * Пример считывания 2х постов: с датой из 2х цифр и с 1 цифрой.
     *
     * @param args null
     * @throws IOException ошибка парсинга
     */
    public static void main(String[] args) throws IOException {
        PostParser postParser = new PostParser(new SqlRuDateTimeParser());
        Post p = postParser.parseFromUrl("https://www.sql.ru/forum/1325330/"
                + "lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t");
        System.out.println(p);
        Post newPost = postParser.parseFromUrl("https://www.sql.ru/forum/1340678/"
                + "ishhem-razrabotchika-v-krupnyy-amerikanskiy-vendor-moskva-ofis");
        System.out.println(newPost);
    }
}
