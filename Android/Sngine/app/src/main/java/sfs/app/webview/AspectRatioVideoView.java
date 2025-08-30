package sfs.app.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class AspectRatioVideoView extends VideoView {

    public AspectRatioVideoView(Context context) {
        super(context);
    }

    public AspectRatioVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        
        if (width > 0 && height > 0) {
            int videoWidth = getMeasuredWidth();
            int videoHeight = getMeasuredHeight();
            
            if (videoWidth > 0 && videoHeight > 0) {
                float videoAspectRatio = (float) videoWidth / videoHeight;
                float viewAspectRatio = (float) width / height;
                
                if (viewAspectRatio > videoAspectRatio) {
                    // View is wider than video - scale video height to view height, scale width proportionally
                    height = MeasureSpec.getSize(heightMeasureSpec);
                    width = (int) (height * videoAspectRatio);
                } else {
                    // View is taller than video - scale video width to view width, scale height proportionally
                    width = MeasureSpec.getSize(widthMeasureSpec);
                    height = (int) (width / videoAspectRatio);
                }
                
                setMeasuredDimension(width, height);
            }
        }
    }
}
