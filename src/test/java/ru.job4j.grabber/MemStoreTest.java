package ru.job4j.grabber;

import org.junit.Test;
import ru.job4j.html.SqlRuParse;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class MemStoreTest {
    public static final Parse parse = new SqlRuParse(new SqlRuDateTimeParser());

    @Test
    public void save() throws IOException {
        MemStore memStore = new MemStore();
        Post p = parse.detail("https://www.sql.ru/forum/1341004/"
                + "backup-for-cloud-service-providers-engineer-do-200-000-rub-na-ruki");
        memStore.save(p);
        assertThat(p.getId(), is(0));
    }

    @Test
    public void getAll() throws IOException {
        MemStore memStore = new MemStore();
        Post p = parse.detail("https://www.sql.ru/forum/1341004/"
                + "backup-for-cloud-service-providers-engineer-do-200-000-rub-na-ruki");
        memStore.save(p);
        List<Post> posts = memStore.getAll();
        for (Post post : posts) {
            assertThat(post, is(p));
        }
    }

    @Test
    public void findById() throws IOException {
        MemStore memStore = new MemStore();
        Post p = parse.detail("https://www.sql.ru/forum/1341004/"
                + "backup-for-cloud-service-providers-engineer-do-200-000-rub-na-ruki");
        memStore.save(p);
        assertThat(memStore.findById(0), is(p));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testException() throws IOException {
        MemStore memStore = new MemStore();
        Post p = parse.detail("https://www.sql.ru/forum/1341004/"
                + "backup-for-cloud-service-providers-engineer-do-200-000-rub-na-ruki");
        memStore.save(p);
        assertThat(memStore.findById(1), is(p));
    }
}