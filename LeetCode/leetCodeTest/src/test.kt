class test {
    fun twoSum(nums: IntArray, target: Int): IntArray {
        var map = HashMap<Int, Int>()
        nums.indices.forEach {
            var num = target - nums[it]
            if (map.containsKey(num)) {
                return intArrayOf(map[num]!!, it)
            } else {
                map[nums[it]] = it

            }
        }
        return intArrayOf(0, 0)
    }

    class ListNode(var num: Int) {
        var next: ListNode? = null
    }





}


fun main() {
    var input = intArrayOf(2, 7, 11, 15)
    var target = 9
    test().twoSum(input, target = target).also {
        print("${it[0]},${it[1]}")
    }


}