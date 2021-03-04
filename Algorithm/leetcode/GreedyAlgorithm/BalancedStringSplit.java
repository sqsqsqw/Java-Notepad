package com;

public class BalancedStringSplit {
    public static void main(String[] args) {
        System.out.println(new Solution().balancedStringSplit("RLRRLLRLRL"));
    }
}

class Solution {
    public int balancedStringSplit(String s) {
        char[] chars = s.toCharArray();
        int num = 0;
        int res = 0;
        for (char aChar : chars) {
            num = aChar == 'L' ? num + 1 : num - 1 ;
            res = num == 0 ? res + 1 : res;
        }
        return res;
    }
}