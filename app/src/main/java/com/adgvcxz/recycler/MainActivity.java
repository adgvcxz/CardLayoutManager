package com.adgvcxz.recycler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adgvcxz.cardlayoutmanager.CardLayoutManager;
import com.adgvcxz.cardlayoutmanager.CardSnapHelper;
import com.adgvcxz.cardlayoutmanager.OnCardSwipeListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        final CardLayoutManager layoutManager = new CardLayoutManager();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new MainAdapter());
        new CardSnapHelper().attachToRecyclerView(recyclerView);
        layoutManager.setBottomInterval(getResources().getDimensionPixelSize(R.dimen.card_bottom_interval));
        layoutManager.setShowCardCount(4);
        findViewById(R.id.ac_main_pre_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(layoutManager.getTopPosition() - 2);
            }
        });
        findViewById(R.id.ac_main_next_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(layoutManager.getTopPosition() + 2);
            }
        });
        final TextView textView = (TextView) findViewById(R.id.ac_main_info);
        layoutManager.setOnCardSwipeListener(new OnCardSwipeListener() {
            @Override
            public void onSwipe(View view, int position, int dx, int dy) {
//                Log.e("zhaow", "onSwipe" + dx + "    " + dy);
                textView.setText(String.format(getString(R.string.info), dx, dy));
            }

            @Override
            public void onAnimOutStart(View view, int position, int direction) {
//                Log.e("zhaow", "动画Out开始" + view + "   " + position);
            }

            @Override
            public void onAnimOutStop(View view, int position, int direction) {
//                Log.e("zhaow", "动画OUT结束" + view + "   " + position);
            }

            @Override
            public void onAnimInStart(View view, int position) {
//                Log.e("zhaow", "动画IN开始" + view + "   " + position);
            }

            @Override
            public void onAnimInStop(View view, int position) {
//                Log.e("zhaow", "动画IN结束" + view + "   " + position);
            }
        });
    }


    class MainAdapter extends RecyclerView.Adapter {

        private LayoutInflater inflater;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (inflater == null) {
                inflater = LayoutInflater.from(parent.getContext());
            }
            return new MainViewHolder(inflater.inflate(viewType, null, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        }

        @Override
        public int getItemCount() {
            return 100;
        }

        @Override
        public int getItemViewType(int position) {
            return position % 2 == 0 ? R.layout.item_layout_top : R.layout.item_layout_down;
        }
    }

    class MainViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MainViewHolder(View itemView) {
            super(itemView);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            int margin = itemView.getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
            lp.setMargins(margin, margin, margin, margin);
            itemView.setLayoutParams(lp);
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
            imageView.setImageResource(R.mipmap.ic_image);
        }
    }
}
