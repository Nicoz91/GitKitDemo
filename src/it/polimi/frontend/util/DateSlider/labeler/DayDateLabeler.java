package it.polimi.frontend.util.DateSlider.labeler;

import it.polimi.frontend.util.DateSlider.timeview.DayTimeLayoutView;
import it.polimi.frontend.util.DateSlider.timeview.TimeView;
import android.content.Context;

/**
 * A Labeler that displays days using DayTimeLayoutViews.
 */
public class DayDateLabeler extends DayLabeler {
    /**
     * The format string that specifies how to display the day. Since this class
     * uses a DayTimeLayoutView, the format string should consist of two strings
     * separated by a space.
     *
     * @param formatString
     */
    public DayDateLabeler(String formatString) {
        super(formatString);
    }

    @Override
    public TimeView createView(Context context, boolean isCenterView) {
        return new DayTimeLayoutView(context, isCenterView,30,8,0.8f);
    }
}