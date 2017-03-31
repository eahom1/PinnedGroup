package org.eahom.pinnedgroup;

import java.util.List;

/**
 * Created by eahom on 17/1/5.
 */

public class Group {

    private String group;
    private List<Child> childList;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<Child> getChildList() {
        return childList;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
    }
}
