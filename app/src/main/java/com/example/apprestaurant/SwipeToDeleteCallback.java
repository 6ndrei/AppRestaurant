package com.example.apprestaurant;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public abstract class SwipeToDeleteCallback extends ItemTouchHelper.Callback {

    private Context mContext;
    private Paint mClearPaint;
    private Paint mTextPaint;
//    private ColorDrawable mBackground;
  //  private int backgroundColor;
    private String mText;
    private int intrinsicWidth;
    private int intrinsicHeight;
    private Drawable background;

    public SwipeToDeleteCallback(Context context) {
        mContext = context;
  //      mBackground = new ColorDrawable();
      //  backgroundColor = Color.parseColor("#ff0000");
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        background = ContextCompat.getDrawable(context, R.drawable.swipe_bg);

        // Configurare Paint pentru text
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(48);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        // Textul care va fi afișat
        mText = "Sterge";

        // Estimare dimensiuni text
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        intrinsicWidth = (int) (mTextPaint.measureText(mText) + 20);
        intrinsicHeight = (int) (fontMetrics.bottom - fontMetrics.top);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dX == 0 && !isCurrentlyActive;

        if (isCancelled) {
            clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Aplică fundalul drawable-ului cu colțuri rotunjite
            background.setBounds(
                    itemView.getLeft(),
                    itemView.getTop(),
                    itemView.getRight(),
                    itemView.getBottom()
            );
            background.draw(c);
        }
     //   mBackground.setColor(backgroundColor);
     //   mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
     //   mBackground.draw(c);

        float textX = itemView.getRight() - (intrinsicWidth);
        float textY = itemView.getTop() + (itemHeight / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2);

        c.drawText(mText, textX, textY, mTextPaint);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
        c.drawRect(left, top, right, bottom, mClearPaint);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.7f;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // Implement this in your CartFragment or where you use SwipeToDeleteCallback
    }
}
