/**
 * 堆排序
 */
class HeapSort {
    fun siftDown(h: IntArray, start: Int = 0, num: Int = 0) {
        val l = h.size - num
        println("l = $l")
        var parent = start
        while (l > (parent * 2 + 1)) {
            println("siftDown $parent")
            if (parent * 2 + 2 == l) {
                if (h[parent] < h[parent * 2 + 1]) {
                    val temp = h[parent]
                    h[parent] = h[parent * 2 + 1]
                    h[parent * 2 + 1] = temp
                }
            } else if (h[parent] < h[parent * 2 + 1] || h[parent] < h[parent * 2 + 2]) {
                when (h[parent * 2 + 1] >= h[parent * 2 + 2]) {
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

    fun makeHeap(h: IntArray) {
        val l = h.size / 2
        for (index in l downTo 0) {
            println(index)
            siftDown(h, start = index)
            h.forEach {

                print(" $it ,")
            }
        }

        println("makeHeap finished")
    }

    fun sort(h: IntArray): IntArray {
        makeHeap(h)
        val l = h.size
        h.indices.forEach { it ->
            if (it > (l - 2)) return@forEach
            siftDown(h, 0, num = it + 1)
            val temp = h[l - it - 1]
            h[l - it - 1] = h[0]
            h[0] = temp
        }
        return h
    }
}

fun main() {
    // val h = intArrayOf(1, 3, 4, 6, 7, 9, 8, 5, 2)
    val h = intArrayOf(3,2,3,1,2,4,5,5,6)
    HeapSort().sort(h)
    println("results:")
    h.forEach { print(" $it ,") }
}

