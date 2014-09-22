package com.clashwars.cwutils.config;

import com.clashwars.cwutils.config.internal.EasyConfig;
import com.clashwars.cwutils.util.Utils;
import java.util.Random;

public class TipConfig extends EasyConfig {

    public String[] tips = new String[] {};
    private Random random = new Random();

    public TipConfig(String fileName) {
        this.setFile(fileName);
    }

    public String getTip(int ID) {
        return tips[ID];
    }

    public String getRandomTip() {
        return tips[Utils.random(0, tips.length - 1)];
    }

    public String[] getTips() {
        return tips;
    }
}
