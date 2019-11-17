package jso.kpl.traveller.ui.RouteView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import jso.kpl.traveller.R;

public class RouteLine extends View
{
    private int lineColor;
    private int lineType;
    private boolean dash;

    private PointF[] leftTobottom;
    private PointF[] leftToTop;
    private PointF[] rightToBottom;
    private PointF[] rightToTop;
    private PointF[] horizontal_line;

    private Paint paint;

    public static String TAG = "Demo.RouteLine";


    public RouteLine(Context context)
    {
        super(context);
    }

    public RouteLine(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public RouteLine(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        paint  = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        //선의 굵기를 세팅하는 곳. px 단위인데 dp 단위로 바꿔서 통일시켜야함.
        paint.setStrokeWidth(8);
        paint.setColor(getLineColor());

        // dash선 적용
        if(getDash())
        {
            paint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
        }

        //좌표 초기화
        pointFinit(canvas);

        switch(this.lineType)
        {
            case 0 :
                canvas.drawPath(drawCurve(leftTobottom[0],leftTobottom[1], leftTobottom[2]), paint);
                break;
            case 1:
                canvas.drawPath(drawCurve(leftToTop[0],leftToTop[1], leftToTop[2]), paint);
                break;
            case 2:
                canvas.drawPath(drawCurve(rightToTop[0],rightToTop[1], rightToTop[2]), paint);
                break;
            case 3:
                canvas.drawPath(drawCurve(rightToBottom[0],rightToBottom[1], rightToBottom[2]), paint);
                break;
            case 4:
                canvas.drawPath(drawCurve(horizontal_line[0],horizontal_line[1], horizontal_line[2]), paint);
                break;
        }
    }

    /**
        곡선을 그리기 위하여 필요한 좌표를 초기화.
        startPoint~ : 시작 좌표
        centerP : 중간 좌표(여기를 기점으로 곡선이 꺾임)
        endPoint~ : 끝 좌표
     */
    private void pointFinit(Canvas canvas)
    {
        int w=canvas.getWidth();
        int h=canvas.getHeight();
        int w_2= (w / 2);
        int h_2= (h / 2);
        PointF startPoint1 = new PointF(0, h_2);
        PointF centerP = new PointF(w_2, h_2);
        PointF endPoint1 = new PointF(w_2, h);

        leftTobottom[0] = startPoint1;
        leftTobottom[1] = centerP;
        leftTobottom[2] = endPoint1;

        PointF endPoint2 = new PointF(w_2, 0);
        leftToTop[0] = startPoint1;
        leftToTop[1] = centerP;
        leftToTop[2] = endPoint2;

        PointF startPoint3 = new PointF(w, h_2);
        rightToBottom[0] = startPoint3;
        rightToBottom[1] = centerP;
        rightToBottom[2] = endPoint1;

        rightToTop[0] = endPoint2;
        rightToTop[1] = centerP;
        rightToTop[2] = startPoint3;

        horizontal_line[0] = startPoint1;
        horizontal_line[1] = centerP;
        horizontal_line[2] = startPoint3;
    }

    private void init(AttributeSet attrs)
    {
        leftTobottom = new PointF[3];
        leftToTop = new PointF[3];
        rightToBottom = new PointF[3];
        rightToTop = new PointF[3];
        horizontal_line = new PointF[3];

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.RouteLine,0,0);
        try
        {
            this.lineColor = a.getColor(R.styleable.RouteLine_lineColor,0xffc0c0c0);
            this.lineType = a.getInteger(R.styleable.RouteLine_lineType, 0);
            this.dash = a.getBoolean(R.styleable.RouteLine_dash, false);
        }
        finally
        {
        }
    }

    public boolean getDash(){
        return dash;
    }

    public void setDash(boolean _dash)
    {
        this.dash = _dash;
        invalidate();
        requestLayout();
    }

    public int getLineColor()
    {
        return lineColor;
    }

    public void setLineColor(int color)
    {
        this.lineColor = color;
        invalidate();
        requestLayout();
    }

    public int getLineType()
    {
        return lineType;
    }

    public void setLineType(int type)
    {
        this.lineType = type;
        invalidate();
        requestLayout();
    }


    private Path drawCurve(PointF mPointa, PointF cp, PointF mPointb) {
        Path myPath = new Path();
        myPath.moveTo(mPointa.x, mPointa.y);
        myPath.cubicTo(mPointa.x, mPointa.y , cp.x, cp.y, mPointb.x, mPointb.y);
        return myPath;
    }

}
