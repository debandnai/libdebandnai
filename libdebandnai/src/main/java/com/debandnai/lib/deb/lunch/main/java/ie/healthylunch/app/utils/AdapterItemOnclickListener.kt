package ie.healthylunch.app.utils

interface AdapterItemOnclickListener {
    fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String)
}