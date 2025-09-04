package com.example.uni.match.policy;

import com.example.uni.user.domain.*;

public class MatchingPolicy {
    public static int hitCount(User me, User c){
        int hit = 0;
        if (me.getIdealMbti()!=null && me.getIdealMbti()==c.getMbti()) hit++;
        if (me.getIdealHeightBand()!=null && me.getIdealHeightBand()==c.getHeightBand()) hit++;
        // hair: 상대 성별 기준
        if (me.getGender()==Gender.MALE) {
            if (me.getIdealFemaleHair()!=null && me.getIdealFemaleHair()==c.getFemaleHair()) hit++;
        } else {
            if (me.getIdealMaleHair()!=null && me.getIdealMaleHair()==c.getMaleHair()) hit++;
        }
        if (me.getIdealAgePref()!=null && c.getAge()!=null && me.getAge()!=null) {
            int diff = c.getAge() - me.getAge();
            switch (me.getIdealAgePref()){
                case OLDER -> { if (diff>0) hit++; }
                case YOUNGER -> { if (diff<0) hit++; }
                case SAME -> { if (diff==0) hit++; }
                case ANY -> hit++;
            }
        }
        return hit;
    }
}
