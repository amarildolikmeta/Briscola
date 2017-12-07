package it.polimi.ma.group07.briscola.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * Created by amari on 07-Dec-17.
 */

public class PieChart extends View
{
    private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private float[] value_degree;
    private int[] COLORS={Color.BLUE,Color.GREEN, Color.GRAY,Color.CYAN,Color.RED};
    RectF rectf = new RectF (10, 10, 200, 200);
    int temp=0;
    public PieChart(Context context, float[] values) {

        super(context);
        value_degree=new float[values.length];
        for(int i=0;i<values.length;i++)
        {
            value_degree[i]=values[i];
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        for (int i = 0; i < value_degree.length; i++) {//values2.length; i++) {
            if (i == 0) {
                paint.setColor(COLORS[i]);
                canvas.drawArc(rectf, 0, value_degree[i], true, paint);
            }
            else
            {
                temp += (int) value_degree[i - 1];
                paint.setColor(COLORS[i]);
                canvas.drawArc(rectf, temp, value_degree[i], true, paint);
            }
        }
    }

}
