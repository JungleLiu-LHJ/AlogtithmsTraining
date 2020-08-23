import java.util.*

class HeapSort {
    fun siftDown(h: IntArray, start: Int = 0, num: Int = 0) {
        val l = h.size - num
        var parent = start + 1
        while (l > (parent * 2)) {
            if (h[parent - 1] < h[parent * 2 - 1] || h[parent - 1] < h[parent * 2]) {
                when (h[parent * 2 - 1] >= h[parent * 2]) {
                    true -> {
                        val temp = h[parent - 1]
                        h[parent - 1] = h[parent * 2 - 1]
                        h[parent * 2 - 1] = temp
                    }
                    false -> {
                        val temp = h[parent - 1]
                        h[parent - 1] = h[parent * 2]
                        h[parent * 2] = temp
                    }
                }
            }
            parent += 1
        }
    }

    fun makeHeap(h: IntArray) {
        val l = h.size / 2
        //println("l = $l")
        for (index in l downTo  0) {
            //println("$index")
            siftDown(h, start = index)
//            h.forEach {
//                print("$it ,")
//            }
//            println()
        }
//        h.forEach {
//            print("$it ,")
//        }
        println("makeHeap finished")
    }

    fun sort(h: IntArray): IntArray {
        makeHeap(h)
        val l = h.size
        h.indices.forEach { it ->
            if(it == (l-1)) return@forEach
//            h.forEach {index->
//                print("$index ,")
//            }
//            println()
            //println("index = ${l - it -1}")
            val temp = h[l - it -1]
            h[l - it -1 ] = h[0]
            h[0] = temp
            siftDown(h, 0,num = it +1)
//            h.forEach {index ->
//                print("$index")
//            }
//            println()
        }
        return h
    }
}

fun main() {
    val h = intArrayOf(1,3,4,6,7,9,8,5,2)
    HeapSort().sort(h)
    println("results:")
    h.forEach { print(" $it ,") }
}

