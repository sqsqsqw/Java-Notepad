package com;

public class FindMaxAverage {
    public static void main(String[] args) {
        Solution s = new Solution();
        System.out.println(s.findMaxAverage(new int[]{1, 12, -5, -6, 50, 3}, 4)); //12.75
        System.out.println(s.findMaxAverage(new int[]{0, 1, 1, 3, 3}, 4));      //2.0
    }

    static class Solution {
        public double findMaxAverage(int[] nums, int k) {
            int res = Integer.MIN_VALUE;
            int tmp = 0;
            for (int i = 0; i < k; i++)
                tmp += nums[i];
            res = tmp;
            for (int i = 1; i < nums.length - k + 1; i++) {
                tmp -= nums[i - 1];
                tmp += nums[i + k - 1];
                res = tmp > res ? tmp : res;
            }

            return res == 0 ? 0f : res/ (double)k;
        }
    }
}


