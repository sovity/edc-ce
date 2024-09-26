import org.junit.jupiter.api.Test;

import static de.sovity.edc.utils.config.utils.UrlPathUtils.urlPathJoin;
import static org.assertj.core.api.Assertions.assertThat;

class UrlPathUtilsTest {
    @Test
    void urlPathJoin_empty() {
        assertThat(urlPathJoin()).isEmpty();
        assertThat(urlPathJoin("")).isEmpty();
        assertThat(urlPathJoin("/")).isEqualTo("/");
    }

    @Test
    void urlPathJoin_relative() {
        assertThat(urlPathJoin("a")).isEqualTo("a");
        assertThat(urlPathJoin("a/")).isEqualTo("a/");
        assertThat(urlPathJoin("a", "b")).isEqualTo("a/b");
        assertThat(urlPathJoin("a/", "b")).isEqualTo("a/b");
        assertThat(urlPathJoin("a", "/b")).isEqualTo("a/b");
        assertThat(urlPathJoin("a/", "/b")).isEqualTo("a/b");
    }

    @Test
    void urlPathJoin_absolute() {
        assertThat(urlPathJoin("/a")).isEqualTo("/a");
        assertThat(urlPathJoin("/a/")).isEqualTo("/a/");
        assertThat(urlPathJoin("/a", "b")).isEqualTo("/a/b");
        assertThat(urlPathJoin("/a/", "b")).isEqualTo("/a/b");
        assertThat(urlPathJoin("/a", "/b")).isEqualTo("/a/b");
        assertThat(urlPathJoin("/a/", "/b")).isEqualTo("/a/b");
    }

    @Test
    void urlPathJoin_protocol() {
        assertThat(urlPathJoin("https://a")).isEqualTo("https://a");
        assertThat(urlPathJoin("https://a/")).isEqualTo("https://a/");
        assertThat(urlPathJoin("https://a", "b")).isEqualTo("https://a/b");
        assertThat(urlPathJoin("https://a/", "b")).isEqualTo("https://a/b");
        assertThat(urlPathJoin("https://a", "/b")).isEqualTo("https://a/b");
        assertThat(urlPathJoin("https://a/", "/b")).isEqualTo("https://a/b");
    }

    @Test
    void urlPathJoin_protocol_overrules_previous_segment() {
        assertThat(urlPathJoin("https://ignored", "https://a")).isEqualTo("https://a");
        assertThat(urlPathJoin("https://ignored", "https://a/")).isEqualTo("https://a/");
        assertThat(urlPathJoin("https://ignored", "https://a", "b")).isEqualTo("https://a/b");
        assertThat(urlPathJoin("https://ignored", "https://a/", "b")).isEqualTo("https://a/b");
        assertThat(urlPathJoin("https://ignored", "https://a", "/b")).isEqualTo("https://a/b");
        assertThat(urlPathJoin("https://ignored", "https://a/", "/b")).isEqualTo("https://a/b");
    }
}
