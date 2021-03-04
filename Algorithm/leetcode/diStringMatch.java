package com;

import java.util.Arrays;

public class diStringMatch {


    public static void main(String[] args) {
        Solution s = new Solution();
        System.out.println(Arrays.toString(s.diStringMatch("IDID")));
        System.out.println(Arrays.toString(s.diStringMatch("DDI")));
        System.out.println(Arrays.toString(s.diStringMatch("III")));
    }
    static class Solution {
        public int[] diStringMatch(String S) {
            int min_point = 0;
            int max_point = S.length();
            int[] res = new int[S.length() + 1];

            for (int i = 0; i < S.length(); i++){
                res[i] = S.charAt(i) == 'I' ? min_point++ : max_point--;
            }
            res[res.length - 1] = min_point;
            return res;
        }
    }
}

