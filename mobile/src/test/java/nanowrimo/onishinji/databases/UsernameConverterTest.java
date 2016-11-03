package nanowrimo.onishinji.databases;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by amandine on 03/11/2016.
 */
public class UsernameConverterTest {
    @org.junit.Test
    public void convert() throws Exception {
        assertThat(UsernameConverter.convert("Sa'kagé 17"), is("sa-kage-17"));
        assertThat(UsernameConverter.convert("Bethany."), is("bethany"));
        assertThat(UsernameConverter.convert("Prism Elf"), is("prism-elf"));
        assertThat(UsernameConverter.convert(" agnetasteam "), is("agnetasteam"));
        assertThat(UsernameConverter.convert("M.R. Wallace"), is("m-r-wallace"));
        assertThat(UsernameConverter.convert("M.R... Wallace"), is("m-r-wallace"));
    }
}