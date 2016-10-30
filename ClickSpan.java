import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

// A helper class for ClickableSpans
public class ClickSpan extends ClickableSpan {
    private final OnClickListener mListener;
    private final Context mContext;

    public ClickSpan(final Context context, final OnClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public void onClick(final View widget) {
        if (this.mListener != null) {
            this.mListener.onClick();
        }
    }

    @Override
    public void updateDrawState(final TextPaint ds) {
        ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        ds.setUnderlineText(true);
    }

    public interface OnClickListener {
        void onClick();
    }
}
