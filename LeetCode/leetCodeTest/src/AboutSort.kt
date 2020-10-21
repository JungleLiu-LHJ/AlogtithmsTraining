import java.util.*
import kotlin.text.toCharArray as toCharArray1

/**
 * 剑指 Offer 45. 把数组排成最小的数
 * 输入一个非负整数数组，把数组里所有数字拼接起来排成一个数，打印能拼接出的所有数字中最小的一个。
 *
 **/

fun minNumber(nums: IntArray): String {
    var queue = PriorityQueue<Int> { val1: Int, val2: Int ->

        val cha1 = val1.toString().toCharArray1()
        val cha2 = val2.toString().toCharArray1()
        var l = 0
        var out = -1
        if (cha1.size < cha2.size) {
            l = cha1.size
            out = -1
        } else {
            l = cha2.size
            out = 1
        }
        for (index in 0 until l) {
            if (cha1[index].toInt() > cha2[index].toInt()) {
                out = 1
                return@PriorityQueue 1
            } else if (cha1[index].toInt() < cha1[index].toInt()) {
                out = -1
                return@PriorityQueue -1
            }
        }
        return@PriorityQueue out
    }

    nums.forEach {
        queue.add(it)
    }
    var a  = ""
    while(queue.isNotEmpty()) {
        a += queue.poll()
    }
    return a

}

fun main() {
    var a = intArrayOf(3,30,34,5,9)
    println("${minNumber(a)}")

}