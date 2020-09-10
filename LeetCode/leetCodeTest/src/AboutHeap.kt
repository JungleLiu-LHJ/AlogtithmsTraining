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

        if(list[list.size - 1] != out) {
            list.add(out)
            i++
            println(out)
        }

    }

    return list[n-1]
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


fun main(){
    println(nthUglyNumber(10))
}
