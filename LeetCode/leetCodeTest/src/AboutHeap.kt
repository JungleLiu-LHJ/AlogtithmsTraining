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
        siftDown(nums, 0, index)
        val temp = times[l - index]
        times[l - index] = times[0]
        times[0] = temp
    }

    val out = hashSetOf<Int>()
    i = k
    for (index in 1..k) {
        map.forEach { item ->
            println("${l - index} : ${times[l - index]} and ${item.value}")
            if (times[l - index] == item.value) {

                println("l = $i")

                out.add(item.key)
                i--
                return@forEach
            }
        }
    }
    return out.toIntArray()
}


fun main() {
    val a = intArrayOf(5, 3, 1, 1, 1, 3, 73, 1)
    val k = 2
    topKFrequent(a, k).forEach {
        println(it)
    }
}
