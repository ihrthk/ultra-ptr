package me.zhangls.ultraptr;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import me.zhangls.adapter.helper.BaseAdapterHelper;
import me.zhangls.adapter.helper.QuickAdapter;
import me.zhangls.loadmore.containner.LoadMoreListViewContainer;
import me.zhangls.loadmore.containner.LoadMoreListener;

public class MainActivity extends AppCompatActivity {

    PtrClassicFrameLayout mPtrFrame;
    QuickAdapter adapter;

    public static final int MORE_DATA_MAX_COUNT = 1;
    public int moreDataCount = 0;
    private LoadMoreListViewContainer loadMoreListViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_web_view_frame);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                LoadMoreListViewContainer container = (LoadMoreListViewContainer) content;
                ListView listView = (ListView) container.getChildAt(0);
                return listView.getFirstVisiblePosition() == 0 && listView.getChildAt(0).getTop() == listView.getPaddingTop();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                new GetDataTask(true).execute();
            }
        });
        loadMoreListViewContainer = (LoadMoreListViewContainer) findViewById(R.id.load_more);
        loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void onLoadMore() {
                new GetDataTask(false).execute();
            }
        });

        ListView listView = (ListView) findViewById(R.id.listview);
        adapter = new QuickAdapter<String>(this, R.layout.list_item) {
            @Override
            protected void convert(BaseAdapterHelper baseAdapterHelper, String s) {
                baseAdapterHelper.setText(R.id.textview, s);
            }
        };
        List<String> list = makeDatas(20, 0);
        adapter.addAll(list);


        listView.setAdapter(adapter);
    }

    public List<String> makeDatas(int size, int start) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add("item" + (i + start));
        }
        return list;
    }

    private class GetDataTask extends AsyncTask<Void, Void, List<String>> {

        private boolean isDropDown;

        public GetDataTask(boolean isDropDown) {
            this.isDropDown = isDropDown;
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {

            }

            return makeDatas(20, (isDropDown ? 0 : ++moreDataCount) * 20);
        }

        @Override
        protected void onPostExecute(List<String> result) {

            if (isDropDown) {
                moreDataCount = 0;
                loadMoreListViewContainer.setHasMore(true);
                adapter.clear();
                adapter.addAll(result);
                mPtrFrame.refreshComplete();
            } else {
                adapter.addAll(result);
                if (moreDataCount > MORE_DATA_MAX_COUNT) {
                    loadMoreListViewContainer.setHasMore(false);
                }
                loadMoreListViewContainer.loadMoreFinish();
            }
            super.onPostExecute(result);
        }
    }


}
