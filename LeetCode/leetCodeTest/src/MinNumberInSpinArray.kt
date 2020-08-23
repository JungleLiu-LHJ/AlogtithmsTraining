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
 * 数组中第K大的数字
 */
//fun findKthLargest(nums: IntArray, k: Int): Int {
//
//}