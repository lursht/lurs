package lurs.cview.home;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

/**
 * 底部菜单实体类
 * <p>
 * Created by lurensheng on 2018/4/25 0025.
 */

public class BottomItemEntity {

    protected int normalColor;
    protected int hoverColor;
    protected String title;
    protected int normalSrc;
    protected int hoverSrc;

    public BottomItemEntity(String title, @ColorRes int normalColor, @ColorRes int hoverColor,
                            @DrawableRes int normalSrc, @DrawableRes int hoverSrc) {
        this.normalColor = normalColor;
        this.hoverColor = hoverColor;
        this.title = title;
        this.normalSrc = normalSrc;
        this.hoverSrc = hoverSrc;
    }
}
