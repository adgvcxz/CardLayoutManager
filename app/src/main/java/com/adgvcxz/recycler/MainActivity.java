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

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Integer> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < 100; i++) {
            mList.add(i);
        }
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
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
        final TextView textView = findViewById(R.id.ac_main_info);
        layoutManager.setOnCardSwipeListener(new OnCardSwipeListener() {
            @Override
            public void onSwipe(View view, int position, int dx, int dy) {
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

//        recyclerView.setItemAnimator(new CardItemAnimator(layoutManager));

    }


    class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {

        private LayoutInflater inflater;

        @Override
        public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (inflater == null) {
                inflater = LayoutInflater.from(parent.getContext());
            }
            return new MainViewHolder(inflater.inflate(viewType, null, false));
        }

        @Override
        public void onBindViewHolder(MainViewHolder holder, int position) {
            holder.update(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position % 2 == 0 ? R.layout.item_layout_top : R.layout.item_layout_down;
        }
    }

    class MainViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public MainViewHolder(View itemView) {
            super(itemView);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            int margin = itemView.getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
            lp.setMargins(margin, margin, margin, margin);
            itemView.setLayoutParams(lp);
            imageView = itemView.findViewById(R.id.item_image);
            textView = itemView.findViewById(R.id.item_text);
            imageView.setImageResource(R.mipmap.ic_image);
        }

        public void update(int value) {
            textView.setText(String.format(Locale.getDefault(), "%d", value));
        }
    }
}
