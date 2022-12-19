package kr.or.mrhi.findrestaurant.data

data class TbVwRestaurants(
    val RESULT: RESULT,
    val list_total_count: Int,
    val row: List<Row>
)