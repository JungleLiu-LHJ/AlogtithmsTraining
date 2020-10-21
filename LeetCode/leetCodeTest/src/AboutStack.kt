import java.util.*
import kotlin.math.min


/**
 * 20. 有效的括号
 * 给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串，判断字符串是否有效。
有效字符串需满足：
左括号必须用相同类型的右括号闭合。
左括号必须以正确的顺序闭合。
注意空字符串可被认为是有效字符串
 **/

fun isValid(s: String): Boolean {
    val charArray = s.toCharArray()
    if (charArray.size % 2 > 0) return false
    val stack = Stack<Char>()
    charArray.forEach {
        when (it) {
            '(', '[', '{' -> {
                stack.add(it)
            }
            ')' -> if (stack.isEmpty() || stack.pop() != '(') return false
            ']' -> if (stack.isEmpty() || stack.pop() != '[') return false
            '}' -> if (stack.isEmpty() || stack.pop() != '{') return false
        }
    }

    return true
}


/**
 * 42. 接雨水
 * 给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水
 */
/**
 * 暴力解法：左右扫描两次，得到两个高的位置
 */
fun trap(height: IntArray): Int {
    var left = 0
    var right = 0
    var capacity = 0
    val l = height.size
    val stack = Stack<Int>()
    val water = IntArray(l)
    height.indices.forEach {
        if (left <= height[it]) {
            while (!stack.isEmpty()) {
                water[stack.pop()] = left
            }
            left = height[it]
        } else {
            stack.push(it)
        }
    }
    for (it in l - 1 downTo 0) {
        if (right <= height[it]) {
            while (!stack.isEmpty()) {
                water[stack.pop()] = right
            }
            right = height[it]
        } else {
            stack.push(it)
        }
    }
    water.indices.forEach {
        print("${water[it]} ,")
        if (water[it] > height[it]) capacity = capacity + water[it] - height[it]
    }
    return capacity
}

/**
 * 动态 时间O(n) 空间O(n)
 */
fun trap2(height: IntArray): Int {
    var capacity = 0
    val l = height.size
    val left = IntArray(l)
    val right = IntArray(l)
    var leftMax = 0
    var rightMax = 0
    height.indices.forEach {
        if (height[it] >= leftMax) {
            leftMax = height[it]
            left[it] = height[it]
        } else {
            left[it] = leftMax
        }
        if (height[l - 1 - it] >= rightMax) {
            rightMax = height[l - 1 - it]
            right[l - 1 - it] = height[l - 1 - it]
        } else {
            right[l - 1 - it] = rightMax
        }
    }
    right.indices.forEach {
        if (right[it] < left[it]) left[it] = right[it]
        capacity += left[it] - height[it]
    }
    return capacity
}

/**
 * 用栈解决
 */
fun trap_stack(height: IntArray): Int {
    val stack = Stack<Int>()
    var capacity = 0
    height.indices.forEach { index ->
        while (stack.isNotEmpty() && height[index] > height[stack.peek()]) {
            val first = stack.pop()
            if (stack.isEmpty()) break
            val distance = index - stack.peek() - 1
            val deep = min(height[index], height[stack.peek()]) - height[first]
            capacity += deep * distance
        }
        stack.push(index)
    }
    return capacity
}

/**
 * 接水-双指针 时间O(n) 空间O(1)
 */
fun trap_points(height: IntArray): Int {
    var left = 0
    var right = height.size - 1
    var deep = 0
    var capacity = 0
    while (left < right) {
        if (height[left] < height[right]) {
            deep = if (height[left] > deep) height[left] else deep
            capacity += deep - height[left]
            left += 1
        } else {
            deep = if (height[right] > deep) height[right] else deep
            capacity += deep - height[right]
            right -= 1
        }
    }
    return capacity
}


/**
 * 71. 简化路径
 * 以 Unix 风格给出一个文件的绝对路径，你需要简化它。或者换句话说，将其转换为规范路径。
在 Unix 风格的文件系统中，一个点（.）表示当前目录本身；此外，两个点 （..） 表示将目录切换到上一级（指向父目录）；两者都可以是复杂相对路径的组成部分。更多信息请参阅：Linux / Unix中的绝对路径 vs 相对路径
请注意，返回的规范路径必须始终以斜杠 / 开头，并且两个目录名之间必须只有一个斜杠 /。最后一个目录名（如果存在）不能以 / 结尾。此外，规范路径必须是表示绝对路径的最短字符串。
 */

fun simplifyPath(path: String): String {
    val pathArray = path.toCharArray()
    val a = StringBuilder()
    val stack = Stack<Char>()
    pathArray.indices.forEach {
        if (pathArray[it] == '/' || it== pathArray.size - 1) {
            if(pathArray[it] != '/' ) a.append(pathArray[it])
            if (a.isEmpty()) {
                if (stack.isEmpty() || stack.peek() != '/') {
                        stack.push(pathArray[it])
                }
            } else {
                    when (a.toString()) {
                        "." -> {
                        }
                        ".." -> {
                            stack.pop()
                            if (stack.isEmpty() ) {
                                stack.push('/')
                            }
                            while (stack.isNotEmpty() && stack.peek() != '/') {
                                stack.pop()
                            }

                        }
                        else -> {
                            a.append(pathArray[it])
                            a.toString().toCharArray().forEach { item ->
                                stack.push(item)
                            }
                        }
                    }
                    a.clear()


            }

        } else {
            a.append(pathArray[it])
        }
    }
    a.clear()
    if(stack.isNotEmpty())stack.pop()
    if (stack.isEmpty()) {
        a.append('/')
    }

    while (stack.isNotEmpty()) {
        a.append(stack.pop())
    }
    return a.reverse().toString()
}

/**
 * 剑指 Offer 09. 用两个栈实现队列
 *
 * 用两个栈实现一个队列。队列的声明如下，请实现它的两个函数 appendTail 和 deleteHead ，分别完成在队列尾部插入整数和在队列头部删除整数的功能。(若队列中没有元素，deleteHead 操作返回 -1
 */

class CQueue() {
    var stack1 = Stack<Int>()
    var stack2 = Stack<Int>()

    fun appendTail(value: Int) {
        stack1.push(value)
    }

    fun deleteHead(): Int {
        if(stack2.isEmpty()) {
            while (stack1.isNotEmpty()) {
                stack2.push(stack1.pop())
            }
        }
        return if (stack2.isNotEmpty()) stack2.pop() else -1
    }
}


/**
 * 剑指 Offer 30. 包含min函数的栈
 * 定义栈的数据结构，请在该类型中实现一个能够得到栈的最小元素的 min 函数在该栈中，调用 min、push 及 pop 的时间复杂度都是 O(1)
 *
 */
class MinStack() {

    /** initialize your data structure here. */
    private val minValue = Stack<Int>()

    private val stack = Stack<Int>()

    fun push(x: Int) {
        val min = if(minValue.isNotEmpty()) minValue.peek() else Int.MAX_VALUE
        if(min >= x){
            minValue.push(x)
        }
        stack.push(x)
    }

    fun pop() {
        if(stack.pop() == minValue.peek()) {
            minValue.pop()
        }
    }

    fun top(): Int {
        return stack.peek()
    }

    fun min(): Int {
        return minValue.peek()
    }

}

/**
 * 剑指 Offer 59 - II. 队列的最大值
 * 请定义一个队列并实现函数 max_value 得到队列里的最大值，要求函数max_value、push_back 和 pop_front 的均摊时间复杂度都是O(1)。
 */
class MaxQueue() {

    var queue = LinkedList<Int>()
    var deque = LinkedList<Int>()

    fun max_value(): Int {
        if(deque.isEmpty()) return -1
        return deque.first
    }

    fun push_back(value: Int) {
        queue.addLast(value)
        while (deque.isNotEmpty() && value > deque.last) {
            deque.pollLast()
        }
        deque.addLast(value)
    }

    fun pop_front(): Int {
        if(queue.isEmpty()) return -1
        if (deque.isNotEmpty() && queue.first == deque.first) {
            deque.pollFirst()
        }
        return queue.poll()
    }

}


fun main() {
    println(simplifyPath("/a/../../b/../c//.//"))
    println(simplifyPath("/a//b////c/d//././/.."))

}