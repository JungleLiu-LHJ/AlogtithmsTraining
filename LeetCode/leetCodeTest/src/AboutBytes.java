public class AboutBytes {
    /**
     * 剑指 Offer 15. 二进制中1的个数
     * 请实现一个函数，输入一个整数，输出该数二进制表示中 1 的个数。例如，把 9 表示成二进制是 1001，有 2 位是 1。因此，如果输入 9，则该函数输出 2
     */
    public int hammingWeight(int n) {
        int o = 0;
        while(n!=0) {
            n = n & n-1;
            o++;
        }
        return o;
    }

    /**
     * 剑指 Offer 39. 数组中出现次数超过一半的数字
     * 数组中有一个数字出现的次数超过数组长度的一半，请找出这个数字
     */
    public int majorityElement(int[] nums) {
        int num =nums[0];
        int vote = 0;
        for(int i : nums) {
            if(vote==0) num = i;
            vote = vote + (num==i?1:-1);
        }
        return num;
    }

}
