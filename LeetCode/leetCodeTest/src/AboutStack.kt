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
fun trap(height: IntArray): Int {
    var left = 0
    var right = 0
    val stack = Stack<Int>()
    height.indices.forEach {
        if(left)
    }

}


