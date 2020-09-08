import java.util.*

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
    if(charArray.size%2>0) return false
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
        if(left <= height[it]) {
            while(!stack.isEmpty()) {
                water[stack.pop()] = left
            }
            left = height[it]
        } else {
            stack.push(it)
        }
    }
    for(it in l-1 downTo 0){
        if(right <= height[it]) {
            while(!stack.isEmpty()) {
                water[stack.pop()] = right
            }
            right = height[it]
        } else {
            stack.push(it)
        }
    }
    water.indices.forEach {
        print("${water[it]} ,")
       if(water[it]>height[it]) capacity = capacity + water[it]-height[it]
    }
    return capacity
}

/**
 * 动态
 */
fun trap2(height: IntArray): Int {
    var capacity = 0
    val l = height.size
    val left = IntArray(l)
    val right = IntArray(l)
    var leftMax = 0
    var rightMax = 0
    height.indices.forEach {
        if(height[it] >= leftMax) {
            leftMax = height[it]
            left[it] = height[it]
        } else {
            left[it] = leftMax
        }
        if(height[l - 1 - it] >= rightMax) {
            rightMax = height[l - 1 - it]
            right[l - 1 - it] = height[l - 1 - it]
        } else {
            right[l - 1 - it] = rightMax
        }
    }
    right.indices.forEach {
        if(right[it]<left[it]) left[it] = right[it]
        capacity += left[it] - height[it]
    }
    return capacity
}

/**
 * 用栈解决
 */
fun trap_stack(height: IntArray): Int {
    var left = 0
    val stack = Stack<Int>()
    height.indices.forEach{
        if(height[it]>left) {
            left = height[it]
            while ()
        }
    }
}


fun main() {
    var a = intArrayOf(0,1,0,2,1,0,1,3,2,1,2,1)
    println(trap2(a))
}