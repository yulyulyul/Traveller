package me.jerryhanks.timelineview;

import android.content.Context;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation search_result, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under search_result.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("me.jerryhanks.timelineview.search_result", appContext.getPackageName());
    }
}
