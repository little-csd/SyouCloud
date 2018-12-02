package com.example.asus.syoucloud.data;

import com.example.asus.syoucloud.bean.MixItem;

import java.util.List;

public class MixLoader {

    private List<MixItem> mixList;

    MixLoader(List<MixItem> mixList) {
        this.mixList = mixList;
    }

    public String[] getTitleItems() {
        String[] titleItems = new String[mixList.size()];
        for (int i = 0; i < mixList.size(); i++)
            titleItems[i] = mixList.get(i).getTitle();
        return titleItems;
    }

    public List<MixItem> getMixList() {
        return mixList;
    }

    public void addMix(MixItem item) {
        mixList.add(item);
    }

    public void deleteMix(long albumId) {
        for (int i = 0; i < mixList.size(); i++) {
            if (mixList.get(i).getId() == albumId)
                mixList.remove(i);
        }
    }
}
