package com.clashwars.cwutils.config;

import com.clashwars.cwutils.config.internal.EasyConfig;
import com.clashwars.cwutils.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TipConfig extends EasyConfig {

    public List<String> tips = new ArrayList<String>();

    public TipConfig(String fileName) {
        this.setFile(fileName);
    }

    public String getTip(int ID) {
        return tips.get(ID);
    }

    public String getRandomTip() {
        return tips.get(Utils.random(0, tips.size() - 1));
    }

    public List<String> getTips() {
        return tips;
    }
}
