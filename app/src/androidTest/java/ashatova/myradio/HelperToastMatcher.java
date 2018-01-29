package ashatova.myradio;

import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.Description;
import android.support.test.espresso.Root;
import android.os.IBinder;
import android.view.WindowManager;

/**
 * This is helper class for testing Toast messages.
 * source - http://www.qaautomated.com/2016/01/how-to-test-toast-message-using-espresso.html
 */
public class HelperToastMatcher extends TypeSafeMatcher<Root> {

    @Override
    public void describeTo(Description description) {
        description.appendText("is toast");
    }

    @Override
    public boolean matchesSafely(Root root) {
        int type = root.getWindowLayoutParams().get().type;
        if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
            IBinder windowToken = root.getDecorView().getWindowToken();
            IBinder appToken = root.getDecorView().getApplicationWindowToken();
            if (windowToken == appToken) {
                return true;
            }
        }
        return false;
    }

}
