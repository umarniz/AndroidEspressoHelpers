import android.net.Uri;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.core.deps.guava.collect.Lists;
import android.support.test.espresso.util.HumanReadables;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Matcher;

import java.util.Arrays;
import java.util.List;

import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkState;
import static android.support.test.espresso.matcher.ViewMatchers.hasLinks;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Umar Nizamani on 09/08/16.
 */

// An expresso ViewAction for clickable spans
public class ClickSpanAction implements ViewAction {

    Matcher<String> spanTextMatcher;

    ClickSpanAction (Matcher<String> spanTextMatcher) {
        if (spanTextMatcher == null)
            assert(true);

        this.spanTextMatcher = spanTextMatcher;
    }

    @Override
    public Matcher<View> getConstraints() {
        return allOf(isDisplayed(), isAssignableFrom(TextView.class));
    }

    @Override
    public String getDescription() {
        return String.format("click on text %s", spanTextMatcher);
    }

    @Override
    public void perform(UiController uiController, View view) {
        TextView textView = (TextView) view;
        String allText = textView.getText().toString();

        Spanned spanned = (Spanned) textView.getText();
        
        // Get all spans that are of tpye ClickSpan
        ClickSpan[] mSpans = spanned.getSpans(0, textView.length(), ClickSpan.class);

        List<String> allSpans = Lists.newArrayList();

        // Get all clickable spans
        for (ClickSpan span : mSpans) {
            int start = spanned.getSpanStart(span);
            checkState(start != -1, "Unable to get start of text associated with url: " + span);
            int end = spanned.getSpanEnd(span);
            checkState(end != -1, "Unable to get end of text associated with url: " + span);

            // Get the text of the ClickSpan
            String linkText = allText.substring(start, end);
            allSpans.add(linkText);

            // Check if it matches the condition
            if (spanTextMatcher.matches(linkText)) {
                span.onClick(view);
                return;
            }
        }

        // Otherwise throw an exception that the ClickSpan text was not found
        throw new PerformException.Builder()
                .withActionDescription(this.getDescription())
                .withViewDescription(HumanReadables.describe(view))
                .withCause(new RuntimeException(String.format(
                        "Link with text '%s' not found. List of links found in this view: %s\n"
                        , spanTextMatcher, allSpans)))
                .build();
    }
}
