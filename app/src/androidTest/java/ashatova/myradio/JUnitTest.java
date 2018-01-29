package ashatova.myradio;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * This class implements JUnit tests of the app.
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JUnitTest {

    @Rule
    public ActivityTestRule<RadioActivity> mActivityRule =
            new ActivityTestRule<>(RadioActivity.class);

    @Before
    public void setUp() throws Exception{
        mActivityRule.getActivity();
    }

    /**
     * Test for all 3 radio stations on the main screen.
     * Push radio station - radio is playing? --> true --> OK
     * Push radio station again - is not playing? --> true --> OK
     */
    @Test
    public void aRadioIsPlaying() {
        //test radio1
        String part1 = mActivityRule.getActivity().getResources().getString(R.string.radio1Title);
        String part2 = mActivityRule.getActivity().getResources().getString(R.string.isConnected);
        onView(withId(R.id.radio1)).perform(click());

        onView(withText(part1 + part2)).inRoot(new HelperToastMatcher())
                .check(matches(withText("Radio Iskatel is connected")));

        onView(withId(R.id.radio1)).perform(click());

        part2 = mActivityRule.getActivity().getResources().getString(R.string.isStopped);

        onView(withText(part1 + part2)).inRoot(new HelperToastMatcher())
                .check(matches(withText("Radio Iskatel is stopped")));


        //test radio2
        part1 = mActivityRule.getActivity().getResources().getString(R.string.radio2Title);
        part2 = mActivityRule.getActivity().getResources().getString(R.string.isConnected);
        onView(withId(R.id.radio2)).perform(click());
        onView(withText(part1 + part2)).inRoot(new HelperToastMatcher())
                .check(matches(withText("Nashe Radio is connected")));

        onView(withId(R.id.radio2)).perform(click());

        part2 = mActivityRule.getActivity().getResources().getString(R.string.isStopped);

        onView(withText(part1 + part2)).inRoot(new HelperToastMatcher())
                .check(matches(withText("Nashe Radio is stopped")));

        //test radio3
        part1 = mActivityRule.getActivity().getResources().getString(R.string.radio3Title);
        part2 = mActivityRule.getActivity().getResources().getString(R.string.isConnected);
        onView(withId(R.id.radio3)).perform(click());
        onView(withText(part1 + part2)).inRoot(new HelperToastMatcher())
                .check(matches(withText("Piter FM is connected")));

        onView(withId(R.id.radio3)).perform(click());

        part2 = mActivityRule.getActivity().getResources().getString(R.string.isStopped);

        onView(withText(part1 + part2)).inRoot(new HelperToastMatcher())
                .check(matches(withText("Piter FM is stopped")));
    }

    /**
     * Test of AboutActivity.
     * Does the screen show information about radio stations? --> true --> OK
     */
    @Test
    public void dAboutActivityTest() {
        onView(withId(R.id.about)).perform(click());

        onView(withId(R.id.radioList)).check(matches(isDisplayed()));

    }

    /**
     * Test EditActivity.
     * Try to save not valid URL for selected radio station --> see toast "URL is not valid" --> OK
     */
    @Test
    public void editRadioTest(){
        onView(withId(R.id.about)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.radioList)).atPosition(0).perform(click());
        onView(withId(R.id.enterTitle)).perform(typeText("TestTitle"));
        onView(withId(R.id.enterUri)).perform(typeText("TestUri"));
        onView(withId(R.id.saveButton)).perform(click());

        onView(withText(R.string.urlNotValid)).inRoot(new HelperToastMatcher())
                .check(matches(withText("URL is not valid")));
    }

    /**
     * Test EditActivity.
     * Save valid radio URL and title for new station and save it.
     * Go back to main screen.
     * See new radio title on the screen? --> true --> OK
     * Push renewed radio station - radio is playing? --> true --> OK
     */
    @Test
    public void fEditAndSaveRadioTest(){
        onView(withId(R.id.about)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.radioList)).atPosition(0).perform(click());
        onView(withId(R.id.enterTitle)).perform(typeText("Detskoe Radio"));
        onView(withId(R.id.enterUri)).perform(typeText("http://ic7.101.ru:8000/v14_1?userid=0&setst=2pbp3bqk3o558dmu8qd4tfvli2&city=163146"));
        onView(withId(R.id.saveButton)).perform(click());

        Espresso.pressBack();

        onView(withId(R.id.radio1)).check(matches(withText("Detskoe Radio")));

        onView(withId(R.id.radio1)).perform(click());

        onView(withText("Detskoe Radio is connected")).inRoot(new HelperToastMatcher())
                .check(matches(withText("Detskoe Radio is connected")));

    }

    /**
     * Test gets back Radio Iskatel.
     * Do you see Radio Iskatel on the main screen? --> true --> OK
     */
    @Test
    public void gBackRadio(){
        onView(withId(R.id.about)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.radioList)).atPosition(0).perform(click());
        onView(withId(R.id.enterTitle)).perform(typeText("Radio Iskatel"));
        onView(withId(R.id.enterUri)).perform(typeText("http://iskatel.hostingradio.ru:8015/iskatel-128.mp3"));
        onView(withId(R.id.saveButton)).perform(click());

        Espresso.pressBack();

        onView(withId(R.id.radio1)).check(matches(withText("Radio Iskatel")));
    }

}




