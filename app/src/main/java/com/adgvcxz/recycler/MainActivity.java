package com.adgvcxz.recycler;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adgvcxz.cardlayoutmanager.CardItemAnimator;
import com.adgvcxz.cardlayoutmanager.CardLayoutManager;
import com.adgvcxz.cardlayoutmanager.CardSnapHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mDatas = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e"));
    private ArrayList<Integer> mColors = new ArrayList<>(Arrays.asList(Color.CYAN, Color.GREEN));

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
        recyclerView.setItemAnimator(new CardItemAnimator());
        findViewById(R.id.ac_main_pre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(layoutManager.getTopPosition() - 2);
//                mDatas.remove(0);
//                recyclerView.getAdapter().notifyItemRemoved(0);
            }
        });
        findViewById(R.id.ac_main_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(layoutManager.getTopPosition() + 2);
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
            return new MainViewHolder(inflater.inflate(R.layout.item_layout, null, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((MainViewHolder) holder).update(position, mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
    }

    class MainViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public MainViewHolder(View itemView) {
            super(itemView);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.setMargins(48, 48, 48, 48);
            itemView.setLayoutParams(lp);
            textView = (TextView) itemView.findViewById(R.id.item_text);
        }

        void update(int position, String str) {
            itemView.setBackgroundColor(mColors.get(position % 2));
            textView.setText(str);
        }
    }
}
