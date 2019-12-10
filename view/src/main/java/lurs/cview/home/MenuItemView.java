package lurs.cview.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;

/**
 * Created by lurensheng on 2018/4/25 0025.
 */

public class MenuItemView extends RelativeLayout {

    protected TextView tvTitle;
    protected ImageView ivIcon;
    private Paint circlePaint, textPaint;
    private float numberTextSize = 10f;

    public MenuItemView(Context context) {
        super(context);
        initView();
    }

    public MenuItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MenuItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        tvTitle = new TextView(getContext());
        tvTitle.setId(View.generateViewId());
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
        LayoutParams lptv = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lptv.bottomMargin = dp2px(2);
        lptv.addRule(ALIGN_PARENT_BOTTOM);
        lptv.addRule(CENTER_HORIZONTAL);
        tvTitle.setLayoutParams(lptv);
        addView(tvTitle);
        ivIcon = new ImageView(getContext());
//        LayoutParams lpiv = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams lpiv = new LayoutParams(dp2px(40), dp2px(40));
//        lpiv.topMargin = dp2px(2);
        ivIcon.setScaleType(ImageView.ScaleType.CENTER);
        lpiv.addRule(ABOVE, tvTitle.getId());
        lpiv.addRule(CENTER_HORIZONTAL);
        ivIcon.setLayoutParams(lpiv);
        addView(ivIcon);
    }

    public Paint getCirclePaint() {
        if (circlePaint == null) {
            circlePaint = new Paint();
            circlePaint.setColor(Color.RED);
        }
        return circlePaint;
    }

    public Paint getTextPaint() {
        if (textPaint == null) {
            textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(sp2px(numberTextSize));
        }
        return textPaint;
    }

    /*@Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        float cx = ivIcon.getHeight() / 2 + ivIcon.getWidth() / 2 + radius * 2;
        float cy = ivIcon.getTop() + radius;
        canvas.drawCircle(cx, cy, radius, getCirclePaint());
        if (TextUtils.isEmpty(number)) return;
        float length = getTextPaint().measureText(number);
        Rect rect = new Rect();
        getTextPaint().getTextBounds(number, 0, number.length(), rect);
        canvas.drawText(number, cx - length / 2, cy + (rect.bottom - rect.top) / 2, getTextPaint());
    }*/

    float sp2px(float sp) {
        return this.getResources().getDisplayMetrics().scaledDensity * sp;
    }

    int dp2px(int dp) {
        return (int) (this.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
}
