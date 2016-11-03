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
    }
}