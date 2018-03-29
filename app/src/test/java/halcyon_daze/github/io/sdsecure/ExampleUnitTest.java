package halcyon_daze.github.io.sdsecure;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.fail;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void testSDParse() {
        try {
            JSONArray testArray = new JSONArray("[{\"lat\": 45.0, \"encryption\": 1, \"lng\": 56.0, \"id\": 18, \"ts\": \"2018-03-13T00:33:04\"}]");
            System.out.println(testArray);
            ArrayList<SDCard> SDList = SDCard.parseSDJSON(testArray);

            for(SDCard s : SDList) {
                System.out.println(s);
            }
        } catch(JSONException e) {
            fail("Json Exception");
        }


    }
}