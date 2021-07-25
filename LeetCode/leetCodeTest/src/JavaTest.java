import org.w3c.dom.Node;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;


public class JavaTest {
    /**
     * 第4题，耗时太长，需要重写
     * @param args
     */
    public static void main(String[] args) {
        double a = (new JavaTest()).findMedianSortedArrays(new int[]{1}, new int[]{2,3,4});
        System.out.println("result=" + a);
    }

    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1.length == 0) {
            int l2 = nums2.length / 2 - 1;
            if (nums2.length % 2 == 0) {
                return (nums2[l2] + nums2[l2 + 1]) / 2f;
            } else {
                return l2 + 1 >= nums2.length ? nums2[l2] : nums2[l2 + 1];
            }
        } else if (nums2.length == 0) {
            int l1 = nums1.length / 2 - 1;
            if (nums1.length % 2 == 0) {
                return (nums1[l1] + nums1[l1 + 1]) / 2f;
            } else {
                return l1 + 1 >= nums1.length ? nums1[l1] : nums1[l1 + 1];
            }
        }


        int l = (nums1.length + nums2.length);
        int index = l / 2;
        boolean isOU = false;
        if (l % 2 == 1) {
            index = index + 1;
        } else {
            isOU = true;
        }
        int m = 0;
        int n = 0;
        int r = 0;

        while (index > 0) {
            int left = m >= nums1.length ? Integer.MAX_VALUE : nums1[m];
            int right = n >= nums2.length ? Integer.MAX_VALUE : nums2[n];
            if (left > right) {
                n++;
                r = 0;
            } else if (left < right) {
                m++;
                r = 1;
            } else {
                if (r == 0) {
                    m++;
                    r = 1;
                } else {
                    n++;
                    r = 0;
                }

            }
            index--;
        }
        System.out.println(n+","+m);
        float out = 0;
        if (r == 0) {
            n--;
            if (isOU) {
                int b = 0;
                if (n + 1 >= nums2.length) {
                    b = nums1[m];
                } else {
                    if(m>=nums1.length) {
                        b=nums2[n + 1];
                    } else {
                        b = Math.min(nums2[n + 1], nums1[m]);
                    }
                }
                out = (nums2[n] + b) / 2f;
            } else {
                out = nums2[n];
            }
        } else {
            m--;
            if (isOU) {
                int b = 0;
                if (m + 1 >= nums1.length) {
                    b = nums2[n];
                } else {
                    if(n>=nums2.length) {
                        b=nums1[m + 1];
                    } else {
                        b = Math.min(nums1[m + 1], nums2[n]);
                    }
                }
                out = (nums1[m] + b) / 2f;
            } else {
                out = nums1[m];
            }
        }
        return out;


    }

}
