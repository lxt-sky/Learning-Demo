package com.sanmen.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;


/**
 * @author lxt_bluesky
 * @date 2018/9/13
 * @description
 */
public class CircleImageView extends AppCompatImageView {
    /**
     * 位图图像渲染
     */
    private BitmapShader mBitmapShader;
    private Bitmap mBitmap;
    /**
     * 画笔
     * 用于绘制图像
     * Paint.ANTI_ALIAS_FLAG:设置绘制时抗锯齿
     */
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 画笔
     * 用于绘制边框
     */
    private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 图形变换处理
     * 3X3矩阵
     */
    private Matrix mMatrix = new Matrix();

    private RectF mRectBorder = new RectF();

    private RectF mRectBitmap = new RectF();

    /**
     * 无
     */
    public static final int TYPE_NONE = 0;
    /**
     * 圆形
     */
    public static final int TYPE_CIRCLE = 1;
    /**
     * 圆角矩形
     */
    public static final int TYPE_RADIUS = 2;


    /**
     * 边框厚度
     */
    private int mBorderWidth;
    /**
     * 边框颜色
     */
    private int mBorderColor;
    /**
     * 边框圆角
     */
    private int mRectRoundRadius;
    /**
     * 类型
     */
    private int mType;
    /**
     * 默认参数
     */
    private static final int DEFAULT_BORDER_COLOR = Color.TRANSPARENT;

    private static final int DEFAULT_BORDER_WIDTH = 0;

    private static final int DEFAULT_RECT_ROUND_RADIUS=0;

    private static final int DEFAULT_TYPE = TYPE_NONE;


    public CircleImageView(Context context) {
        this(context,null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        //获取属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        mType = typedArray.getInt(R.styleable.CircleImageView_type,DEFAULT_TYPE);
        mBorderColor = typedArray.getColor(R.styleable.CircleImageView_borderColor,DEFAULT_BORDER_COLOR);
        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_borderWidth,dip2px(DEFAULT_BORDER_WIDTH));
        mRectRoundRadius = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_rectRoundRadius,dip2px(DEFAULT_RECT_ROUND_RADIUS));

        //回收资源
        typedArray.recycle();
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = getBitmap(getDrawable());
        if (bitmap!=null&&mType!=TYPE_NONE){
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            int viewMinSize = Math.min(viewWidth,viewHeight);
            float dstWidth = mType==TYPE_CIRCLE?viewMinSize:viewWidth;
            float dstHeight = mType==TYPE_CIRCLE?viewMinSize:viewHeight;
            float halfBorderWidth = mBorderWidth/2.0f;
            float doubleBorderWidth = mBorderWidth*2;

            if(mBitmapShader==null||!bitmap.equals(mBitmap)){
                mBitmap = bitmap;
                //边缘拉伸
                mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            }
                if (mBitmapShader!=null){
                //进行图片缩放
                //设置图片缩放比例-减去描边宽度
//                mMatrix.setScale(dstWidth/bitmap.getWidth(),dstHeight/bitmap.getHeight());
                //减去2倍的描边宽度和高度,算出正确的图片缩放比例
                mMatrix.setScale((dstWidth-mBorderWidth*2)/bitmap.getWidth(),(dstHeight-mBorderWidth*2)/bitmap.getHeight());
                mBitmapShader.setLocalMatrix(mMatrix);
            }
            mPaint.setShader(mBitmapShader);
            //边框画笔设置
            //类型:描边
            mBorderPaint.setStyle(Paint.Style.STROKE);
            //描边厚度
            mBorderPaint.setStrokeWidth(mBorderWidth);
            //画笔颜色
            mBorderPaint.setColor(mBorderWidth>0?mBorderColor: Color.TRANSPARENT);

            if (mType==TYPE_CIRCLE){
                float radius = viewMinSize/2.0f;
                //绘制圆形边框,mPaint决定圆形框内内容的绘制
                //            canvas.drawCircle(radius,radius,radius,mPaint);
                //以radius-mBorderWidth/2.0f为半径,绘制,宽度为mBorderWidth的圆形描边
                canvas.drawCircle(radius,radius,radius-mBorderWidth/2.0f,mBorderPaint);
                //绘制画布平移转移,画布平移导致中心坐标改变一个mBorderWidth
                canvas.translate(mBorderWidth,mBorderWidth);
                //radius-mBorderWidth为半径,绘制圆形图片框
                canvas.drawCircle(radius-mBorderWidth,radius-mBorderWidth,radius-mBorderWidth,mPaint);
            }else if (mType==TYPE_RADIUS){
                mRectBorder.set(halfBorderWidth,halfBorderWidth,dstWidth-halfBorderWidth,dstHeight-halfBorderWidth);
                mRectBitmap.set(0.0f,0.0f,dstWidth-doubleBorderWidth,dstHeight-doubleBorderWidth);

                float borderRadius = mRectRoundRadius - halfBorderWidth > 0.0f ? mRectRoundRadius - halfBorderWidth : 0.0f;
                float bitmapRadius = mRectRoundRadius - mBorderWidth > 0.0f ? mRectRoundRadius - mBorderWidth : 0.0f;
                //绘制圆角矩形边框
                canvas.drawRoundRect(mRectBorder, borderRadius, borderRadius, mBorderPaint);
                //平移
                canvas.translate(mBorderWidth, mBorderWidth);
                //绘制圆角矩形图像
                canvas.drawRoundRect(mRectBitmap, bitmapRadius, bitmapRadius, mPaint);
            }

        }else {
            super.onDraw(canvas);
        }
    }

    private Bitmap getBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }else if (drawable instanceof ColorDrawable){
            //2x2矩阵
            Rect rect = drawable.getBounds();
            int width = rect.right-rect.left;
            int height = rect.bottom-rect.top;
            int color = ((ColorDrawable) drawable).getColor();
            //32位ARGB位图
            Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
            return bitmap;
        }else {
            return null;
        }
    }

    /**
     * 设置描边颜色
     * @param color
     */
    public void setBorderColor(@ColorInt int color){
        this.mBorderColor = color;
        invalidate();
    }

    /**
     * 设置描边宽度
     * @param width
     */
    public void setBorderWidth(int width){
        this.mBorderWidth = dip2px(width);
        invalidate();
    }

    /**
     * 设置View类型
     * @param type
     */
    public void setType(int type){
        this.mType = type;
        invalidate();
    }

    /**
     * 设置圆角,当type为TYPE_RADIUS时有效
     * @param radius
     */
    public void setRectRoundRadius(int radius){
        this.mRectRoundRadius = radius;
        invalidate();
    }

    /**
     * dp转px
     * @param dipVal
     * @return
     */
    private int dip2px(int dipVal){
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipVal*scale+0.5f);
    }
}
