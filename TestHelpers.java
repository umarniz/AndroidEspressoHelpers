import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.OpenLinkAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jraska.falcon.FalconSpoon;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.TimeoutException;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by Umar Nizamani on 07/07/16.
 */
public class TestHelpers {

    // Check if a text field has an error text
    public static Matcher<View> hasErrorText(final int errorResource) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextView)) {
                    return false;
                }

                CharSequence error = ((TextView) view).getError();

                if (error == null) {
                    return false;
                }

                String expectedText = view.getResources().getString(errorResource);

                if (expectedText == null) {
                    return false;
                }

                return expectedText.equals(error.toString());
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    // Replace progressbars in the view with drawables so that the AsyncTask is not blocked
    public static ViewAction replaceProgressBarDrawable() {
        return actionWithAssertions(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ProgressBar.class);
            }

            @Override
            public String getDescription() {
                return "Replace the ProgressBar drawable";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                // Replace the indeterminate drawable with a static red ColorDrawable
                ProgressBar progressBar = (ProgressBar) view;
                progressBar.setIndeterminateDrawable(new ColorDrawable(0xffff0000));
                uiController.loopMainThreadUntilIdle();
            }
        });
    }

    // Take screenshot of current activity with FalconSpoon
    // This does not take an activity to take a screenshot of to make it flexible
    public static File getCurrentActivityScreenshot(ActivityTestRule activityRule, String tag) {
        Activity activity = getCurrentActivity(activityRule);
        if (activity == null) {
            Log.e("TestHelpers", "Failed to take screenshot " + tag + "." + " Null current activity");
        } else {
            return FalconSpoon.screenshot(activity, tag);
        }

        return null;
    }

    // Get currently active Activity
    public static Activity getCurrentActivity(ActivityTestRule activityRule) {
        try {
            getInstrumentation().waitForIdleSync();
            final Activity[] activity = new Activity[1];
            activityRule.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity[0] = ActivityLifecycleMonitorRegistry.getInstance()
                            .getActivitiesInStage(Stage.RESUMED).iterator().next();
                }
            });
            return activity[0];
        } catch ( Throwable throwable ) {
            throwable.printStackTrace();
        }
        return null;
    }

    // Return a boolean value to check if a view exists
    // Good for conditional testing that does not throw assertions
    public static boolean doesViewExist(int id) {
        try {
            onView(withId(id)).check(matches(isDisplayed()));
            return true;
        } catch (NoMatchingViewException e) {
            return false;
        }
    }

    // Uses Click Span Action
    public static ViewAction clickOnTextSpan(String text) {
        return actionWithAssertions(new ClickSpanAction(is(text)));
    }
}
