package ru.job4j.html;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Post {
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

    @Override
    public String toString() {
        return String.format("Post= {id: %d, title: %s, link: %s, description: %s, created: %s}",
                id, title, link, description, created.format(FORMATTER));
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

        return Objects.equals(title, p.title) && Objects.equals(link, p.link)
                && Objects.equals(description, p.description) && Objects.equals(created, p.created);
    }

    @Override
    public int hashCode() {
        int rsl = title == null ? 0 : title.hashCode();
        rsl = 31 * rsl + (link == null ? 0 : link.hashCode());
        rsl = 31 * rsl + (description == null ? 0 : description.hashCode());
        rsl = 31 * rsl + (created == null ? 0 : created.hashCode());
        return rsl;
    }
}
