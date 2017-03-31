package org.eahom.pinnedgroup;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eahom.pinned.PinnedGroupExpandableListView;

import java.util.ArrayList;
import java.util.List;

import static org.eahom.pinnedgroup.R.id.header_tv;

public class MainActivity extends AppCompatActivity implements ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener, AdapterView.OnItemLongClickListener {

    private PinnedGroupExpandableListView mPinned;
    private TestAdapter mAdapter;

    private List<Group> mDataList = new ArrayList<>();

    private int mGroupCount = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_refresh_adapter) {
            mGroupCount++;
            refreshData(mGroupCount);
            mAdapter.notifyDataSetChanged();
            expandAllGroup();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mPinned = (PinnedGroupExpandableListView) findViewById(R.id.pinned);
        mGroupCount = 7;
        refreshData(mGroupCount);

        View headerView0 = getLayoutInflater().inflate(R.layout.header, null, false);
        TextView header_tv0 = (TextView) headerView0.findViewById(header_tv);
        header_tv0.setText("I'm header text 0");
        mPinned.addHeaderView(headerView0, null, false);
        View headerView1 = getLayoutInflater().inflate(R.layout.header, null, false);
        TextView header_tv1 = (TextView) headerView1.findViewById(header_tv);
        header_tv1.setText("I'm header text 1\nI'm header text 1\nI'm header text 1");
        mPinned.addHeaderView(headerView1, null, false);

        View footerView0 = getLayoutInflater().inflate(R.layout.header, null, false);
        TextView footer_tv0 = (TextView) footerView0.findViewById(header_tv);
        footer_tv0.setText("I'm footer text 0");
        mPinned.addFooterView(footerView0, null, false);

        View footerView1 = getLayoutInflater().inflate(R.layout.header, null, false);
        TextView footer_tv1 = (TextView) footerView1.findViewById(header_tv);
        footer_tv1.setText("I'm footer text 1...\nI'm footer text 1...\nI'm footer text 1...\nI'm footer text 1...\n");
        mPinned.addFooterView(footerView1, null, false);

        View footerView2 = getLayoutInflater().inflate(R.layout.header, null, false);
        TextView footer_tv2 = (TextView) footerView2.findViewById(header_tv);
        footer_tv2.setText("I'm footer text 2...\nI'm footer text 2...\n");
        mPinned.addFooterView(footerView2, null, false);

        mAdapter = new TestAdapter();
        mPinned.setAdapter(mAdapter);
        expandAllGroup();
        mPinned.setOnGroupClickListener(this);
        mPinned.setOnChildClickListener(this);
        mPinned.setOnItemLongClickListener(this);

        Drawable dividerDrawable = new ColorDrawable(Color.MAGENTA);
        mPinned.setPinnedGroupDivider(dividerDrawable);
    }

    private void refreshData(int count) {
        mDataList.clear();
        for (int i = 0; i < count; i++) {
            Group group = new Group();
            String groupName = "group" + i;
            if (i % 3 == 0)
                groupName += "\n ......";
            group.setGroup(groupName);
            List<Child> childList = new ArrayList<>();
            for (int j = 0; j < i * 2 + 1; j++) {
                Child child = new Child("child" + i + "-" + j, j);
                childList.add(child);
            }
            group.setChildList(childList);
            mDataList.add(group);
        }
    }

    private void expandAllGroup() {
        for (int i = 0, count = mPinned.getCount() - mPinned.getHeaderViewsCount() - mPinned.getFooterViewsCount(); i < count; i++) {
//            Logger.e("expandAllGroup, i-->" + i);
            if (i < mDataList.size())
                mPinned.expandGroup(i);
        }
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Toast.makeText(this, "childClick, groupPosition:" + groupPosition + ", childPosition:" + childPosition, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "longClick, position:" + position, Toast.LENGTH_SHORT).show();
        return false;
    }



    class TestAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return mDataList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mDataList.get(groupPosition).getChildList().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mDataList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mDataList.get(groupPosition).getChildList().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder groupHolder = null;
            if (convertView == null) {
                groupHolder = new GroupHolder();
                convertView = getLayoutInflater().inflate(R.layout.group, null, false);
                groupHolder.name_tv = (TextView) convertView.findViewById(R.id.group_name_tv);
                convertView.setTag(groupHolder);
            }
            else {
                groupHolder = (GroupHolder) convertView.getTag();
            }
            groupHolder.name_tv.setText(mDataList.get(groupPosition).getGroup());
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder childHolder = null;
            if (convertView == null) {
                childHolder = new ChildHolder();
                convertView = getLayoutInflater().inflate(R.layout.child, null, false);
                childHolder.name_tv = (TextView) convertView.findViewById(R.id.child_name_tv);
                childHolder.age_tv = (TextView) convertView.findViewById(R.id.child_age_tv);
                convertView.setTag(childHolder);
            }
            else {
                childHolder = (ChildHolder) convertView.getTag();
            }
            Child child = mDataList.get(groupPosition).getChildList().get(childPosition);
            childHolder.name_tv.setText(child.getName());
            childHolder.age_tv.setText("" + child.getAge());
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    class GroupHolder {
        TextView name_tv;
    }

    class ChildHolder {
        TextView name_tv;
        TextView age_tv;
    }

}
