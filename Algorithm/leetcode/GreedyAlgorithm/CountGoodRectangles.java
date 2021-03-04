package GreedyAlgorithm;

import java.lang.reflect.Array;

public class CountGoodRectangles {

    public static void main(String[] args) {
        System.out.println(new Solution().countGoodRectangles(new int[][]{{5,8},{3,9},{5,12},{16,5}}));
    }


    static class Solution {
        public int countGoodRectangles(int[][] rectangles) {
            int maxLen = 0;
            int num = 0;
            int tmpLen = 0;

            for (int[] rectangle : rectangles) {
                tmpLen = rectangle[0] > rectangle[1] ? rectangle[1] : rectangle[0];
                if (maxLen == tmpLen) {
                    num++;
                }
                else if (maxLen < tmpLen){
                    num = 1;
                    maxLen = tmpLen;
                }
            }
            return num;
        }
    }
}
