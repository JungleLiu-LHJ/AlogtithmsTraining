import java.util.*
import kotlin.collections.HashMap

/**
 * 最小的k个数
 * 输入整数数组 arr ，找出其中最小的 k 个数。例如，输入4、5、1、6、2、7、3、8这8个数字，则最小的4个数字是1、2、3、4。
 */
class Solution {
    fun getLeastNumbers(arr: IntArray, k: Int): IntArray {
        val l = arr.size - 1
        var out = IntArray(k)
        for(index in l downTo 0) {
            siftUp(arr,start = index)
        }
//        arr.forEach {
//            print("$it ,")
//        }

        for(index in 0 until  k) {
            siftUp(arr,num = index)
            out[index] = arr[0]
            val temp = arr[l - index]
            arr[l - index] = arr[0]
            arr[0] = temp


        }
        return out

    }

    fun siftUp(h: IntArray, start: Int = 0, num: Int = 0) {
        val l = h.size - num
        var parent = start
        while (l > (parent * 2 + 1)) {
            if (parent * 2 + 2 == l) {
                if (h[parent] > h[parent * 2 + 1]) {
                    val temp = h[parent]
                    h[parent] = h[parent * 2 + 1]
                    h[parent * 2 + 1] = temp
                }
            } else if (h[parent] > h[parent * 2 + 1] || h[parent] > h[parent * 2 + 2]) {
                when (h[parent * 2 + 1] <= h[parent * 2 + 2]) {
                    true -> {
                        val temp = h[parent]
                        h[parent] = h[parent * 2 + 1]
                        h[parent * 2 + 1] = temp
                    }
                    false -> {
                        val temp = h[parent]
                        h[parent] = h[parent * 2 + 2]
                        h[parent * 2 + 2] = temp
                    }
                }
            }
            parent += 1
        }
    }
}

fun main() {
    val a = intArrayOf(3,2,1)
    val k = 2
    Solution().getLeastNumbers(a, k).forEach {
        println(it)
    }

}

/**
 * 剑指 Offer 41. 数据流中的中位数
 * 如何得到一个数据流中的中位数？如果从数据流中读出奇数个数值，那么中位数就是所有数值排序之后位于中间的数值。如果从数据流中读出偶数个数值，那么中位数就是所有数值排序之后中间两个数的平均值。
 *
 */

class MedianFinder() {

    /** initialize your data structure here. */
    private val maxQueue = PriorityQueue<Int> { a, b -> if (b - a > 0) 1 else -1 }
    private val minQueue = PriorityQueue<Int> { a, b -> if (a - b > 0) 1 else -1 }

    fun addNum(num: Int) {
        when {
            maxQueue.isEmpty() -> {
                maxQueue.offer(num)
            }
            num > maxQueue.peek() -> {
                minQueue.offer(num)
            }
            else -> {
                maxQueue.offer(num)
            }
        }
        while (maxQueue.size - 1 != minQueue.size && maxQueue.size != minQueue.size) {
            if (minQueue.size < maxQueue.size - 1) {
                minQueue.offer(maxQueue.poll())
            } else if (minQueue.size > maxQueue.size) {
                maxQueue.offer(minQueue.poll())
            }
        }
    }

    fun findMedian(): Double {
        return if (minQueue.size == maxQueue.size) (minQueue.peek() + maxQueue.peek()).toDouble() / 2 else maxQueue.peek().toDouble()

    }

}


/**
 * 264.丑数
 * 编写一个程序，找出第 n 个丑数。

丑数就是质因数只包含 2, 3, 5 的正整数。
 */
/**
 * 动态规划
 */
fun nthUglyNumber(n: Int): Int {
    var list = mutableListOf<Int>(1)
    var i = 0
    var node2 = 0
    var node3 = 0
    var node5 = 0
    while (i != n - 1) {
        val a = 2 * list[node2]
        val b = 3 * list[node3]
        val c = 5 * list[node5]
        var out = 0
        when {
            (a <= b && a <= c) -> {
                node2 += 1
                out = a
            }
            (b <= a && b <= c) -> {
                node3 += 1
                out = b
            }
            (c <= a && c <= b) -> {
                node5 += 1
                out = c
            }
            else -> {
            }
        }

        if (list[list.size - 1] != out) {
            list.add(out)
            i++
            println(out)
        }

    }

    return list[n - 1]
}

/**
 * 判断是否为丑数
 */
fun isUglyNumber(n: Int): Boolean {
    if (n < 0) return false
    var num = n
    while (num % 2 == 0) num /= 2
    while (num % 3 == 0) num /= 3
    while (num % 5 == 0) num /= 5
    return num == 1
}





/**
 * 347. 前 K 个高频元素
 * 给定一个非空的整数数组，返回其中出现频率前 k 高的元素。
 */
fun topKFrequent(nums: IntArray, k: Int): IntArray {
    val map = HashMap<Int, Int>()
    nums.forEach { num ->
        if (map.containsKey(num)) {
            map[num] = map[num]!!.plus(1)
        } else {
            map[num] = 1
        }
    }
    val l = map.size
    val times = IntArray(l)
    var i = 0
    map.forEach {
        times[i] = it.value
        i++
    }

    makeHeap(times)
    for (index in 1..k) {
        if (index > (l - 2)) break
        siftDown(times, 0, index)
        val temp = times[l - index]
        times[l - index] = times[0]
        times[0] = temp
    }

    val out = hashSetOf<Int>()
    i = k

    for (index in 1..k)  {
        map.forEach { item ->
            println("${l - index} : ${times[l - index]} and ${item.value}")
            if (times[l - index] == item.value) {
                println("l = $i")
                if(i>0){
                    out.add(item.key)
                    map[item.key]= 0
                    i--
                }

            }
        }
    }
    return out.toIntArray()
}
/************未完成*********************************/


