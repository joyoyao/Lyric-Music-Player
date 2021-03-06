
package org.music.player;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.TextView;

/**
 * TextView that draws a divider line at the bottom.
 *
 * We draw the divider here rather than with ListView.setDivider() so we don't
 * have duplicate dividers when hiding a row for a drag.
 *
 * This also implements the Checkable interface to provide checking for
 * TabOrderActivity. CheckedTextView also provides this, but unfortunately its
 * check-mark ignores padding so it can't be used with DragListView's expansion
 * code.
 */
public class DragTextView extends TextView implements Checkable {
	private final Paint mPaint;
	private boolean mChecked;
	/**
	 * Check mark drawable to update with checked state. This drawable is set
	 * as the TextView's right compound drawable, so TextView will handle the
	 * drawing.
	 */
	private final Drawable mCheckMarkDrawable;
	/**
	 * The preferred height of the view in pixels. Set to DragListView.ROW_HEIGHT.
	 */
	private final int mHeight;

	private static final int[] CHECKED_STATE_SET = {
		android.R.attr.state_checked
	};

	public DragTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		Paint paint = new Paint();
		paint.setColor(0xff444444);
		mPaint = paint;
		Drawable[] drawables = getCompoundDrawables();
		mCheckMarkDrawable = drawables[2];

		float density = context.getResources().getDisplayMetrics().density;
		mHeight = (int)(DragListView.ROW_HEIGHT * density);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		int height = getHeight();
		if (height <= 1)
			return;
		super.onDraw(canvas);
		if (getDrawingCacheBackgroundColor() != DragListView.DRAG_COLOR && getPaddingBottom() < getHeight() / 2) {
			// only draw divider when not dragging
			float h = height - 1;
			canvas.drawLine(0, h, getWidth(), h, mPaint);
		}
	}

	@Override
	public boolean isChecked()
	{
		return mChecked;
	}

	@Override
	public void setChecked(boolean checked)
	{
		mChecked = checked;
		refreshDrawableState();
	}

	@Override
	public void toggle()
	{
		setChecked(!mChecked);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace)
	{
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (mChecked) {
			mergeDrawableStates(drawableState, CHECKED_STATE_SET);
		}
		return drawableState;
	}

	@Override
	protected void drawableStateChanged()
	{
		super.drawableStateChanged();

		if (mCheckMarkDrawable != null) {
			int[] myDrawableState = getDrawableState();
			mCheckMarkDrawable.setState(myDrawableState);
			invalidate();
		}
	}

	@Override
	public void onMeasure(int widthSpec, int heightSpec)
	{
		setMeasuredDimension(MeasureSpec.getSize(widthSpec), resolveSize(mHeight, heightSpec));
	}
}
