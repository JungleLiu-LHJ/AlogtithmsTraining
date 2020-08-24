import java.util.Arrays.sort

/**
 * 旋转数组中的最小数
 */

fun minArray(numbers: IntArray): Int {
    val l = numbers.size - 1
    var left = 0
    var right = l
    while (left != right) {
        val mid = (left + right) / 2
        if (numbers[right] > numbers[left]) {
            return numbers[left]
        }
        if (right - left == 1) {
            return numbers[right]
        }
        if (numbers[left] == numbers[right]) {
            var min = numbers[left]
            numbers.forEach {
                if (it < min) min = it
            }
            return min
        }
        if (numbers[mid] >= numbers[left]) {
            left = mid
            println("1")
        } else if (numbers[mid] <= numbers[right]) {
            right = mid
        }
    }
    return numbers[right]
}

//fun main() {
//    minArray(intArrayOf(3, 4, 5, 1, 2)).also { print("$it") }
//}

class ListNode(var `val`: Int) {
    var next: ListNode? = null
}

/**
 * 合并两个排序的链表
 */
fun mergeTwoLists(l1: ListNode?, l2: ListNode?): ListNode? {
    var isGoOn = true
    l1 ?: return l2
    l2 ?: return l1
    var l1Node = l1
    var l2Node = l2
    var outList = ListNode(l1.`val`)
    if (l1.`val` > l2.`val`) {
        outList = l2Node
        l2Node = l2Node.next
    } else {
        outList = l1Node
        l1Node = l1Node.next
    }
    var tmpListNode = outList
    while (isGoOn) {
        if (l1Node == null && l2Node == null) {
            return outList
        } else if (l1Node == null) {
            tmpListNode.next = l2Node
            isGoOn = false
        } else if (l2Node == null) {
            tmpListNode.next = l1Node
            isGoOn = false
        } else {
            if (l1Node.`val` > l2Node.`val`) {
                tmpListNode.next = l2Node
                tmpListNode = tmpListNode.next!!
                l2Node = l2Node.next
            } else {
                tmpListNode.next = l1Node
                tmpListNode = tmpListNode.next!!
                l1Node = l1Node.next
            }
        }
    }
    return outList
}

/**
 * 215. 数组中的第K个最大元素
 */
fun siftDown(h: IntArray, start: Int = 0, num: Int = 0) {
    val l = h.size - num
    var parent = start
    while (l > (parent * 2 + 1)) {
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
        siftDown(h, start = index)
    }
    println("makeHeap finished")
}

fun findKthLargest(nums: IntArray, k: Int): Int {
    makeHeap(nums)
    val l = nums.size
    for(index in 1 .. k){
        siftDown(nums,0,index)
        val temp = nums[l - index]
        nums[l - index ] = nums[0]
        nums[0] = temp
    }
    return nums[l-k ]
}
fun main() {
    val a = intArrayOf(3,2,3,1,2,4,5,5,6)
    println(findKthLargest(a, 4))
}