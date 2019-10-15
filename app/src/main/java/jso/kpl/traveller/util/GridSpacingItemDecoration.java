package jso.kpl.traveller.util;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    //단수,패딩값,
    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
           /* if(view instanceof ViewModel == false){ //홈 그리드 아이템이 아닌경우 패스
                return;
            }*/
        int position = parent.getChildAdapterPosition(view); // item position

        int spanIndex = ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).getSpanIndex();

        if (includeEdge) {


            if (spanIndex == 0) {
                //좌측 아이템이며 우측 패딩을 설정한 패딩의 1/2로 설정
                outRect.left = spacing / spanCount;
                outRect.right = spacing / spanCount;
            } else {//if you just have 2 span . Or you can use (staggeredGridLayoutManager.getSpanCount()-1) as last span
                //우측 아이템이며 좌측 패딩을 설정한 패딩의 1/2로 설정
                outRect.left = spacing / spanCount;
                outRect.right = spacing / spanCount;
            }

            //상단 패딩
            if (position < spanCount) { // top edge
                outRect.top = spacing;
            }

            //하단 패딩
            outRect.bottom = spacing; // item bottom
        }
    }
}

