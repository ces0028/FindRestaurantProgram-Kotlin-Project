package kr.or.mrhi.findrestaurant

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kr.or.mrhi.findrestaurant.databinding.CustomDialogShowInfoBinding
import kr.or.mrhi.findrestaurant.databinding.ItemRestaurantBinding

class CustomAdapter(val context: Context, val restaurantList:  MutableList<Restaurant>?)
    : RecyclerView.Adapter<CustomAdapter.CustomViewHolder>(){
    lateinit var dialog : AlertDialog
    override fun getItemCount(): Int {
        return restaurantList?.size?:0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val customViewHolder = CustomViewHolder(binding)

        customViewHolder.itemView.setOnClickListener {
            val dialogBinding = CustomDialogShowInfoBinding.inflate(LayoutInflater.from(parent.context))
            val builder = AlertDialog.Builder(parent.context)
            val itemPosition = customViewHolder.adapterPosition
            val restaurant = restaurantList?.get(itemPosition)
            dialogBinding.tvName.text = restaurant?.name?.replace("\n", "")
            dialogBinding.tvAddress.text = restaurant?.address?.replace("\n", "")
            dialogBinding.tvSubwayInfo.text = restaurant?.subwayInfo?.replace("\n", "")
            dialogBinding.tvWebpage.text = restaurant?.webPage?.replace("\n", "")
            dialogBinding.tvOpeningHours.text = restaurant?.openingHours?.replace("\n", "")
            dialogBinding.tvPhone.text = restaurant?.phone?.replace("\n", "")
            dialogBinding.tvMenu.text = restaurant?.menu?.replace("\n", "")
            builder.setView(dialogBinding.root)
            dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            dialog.show()

            dialogBinding.ivClose.setOnClickListener {
                dialog.dismiss()
            }
            dialogBinding.ivCall.setOnClickListener {
                (context as MainActivity).callRestaurant(dialogBinding.tvPhone.text.toString())
            }
            dialogBinding.ivHomepage.setOnClickListener {
                val url = dialogBinding.tvWebpage.text.toString()
                if(url == " - ") {
                    when(restaurant?.language) {
                        "ko" -> Toast.makeText(context,
                                "해당 음식점은 홈페이지가 등록돼있지 않습니다.\n문의사항이 있으신 경우에는 가게에 전화해주십시오.",
                                Toast.LENGTH_LONG
                        ).show()
                        "en" -> Toast.makeText(context,
                            "This restaurant does not have its website registered.\nIf you have any questions, please call the store.",
                            Toast.LENGTH_LONG
                        ).show()
                        "ja" -> Toast.makeText(context,
                            "このレストランはウェブサイトが登録されていません。\nご不明な点がございましたら店舗までご連絡ください。",
                            Toast.LENGTH_LONG
                        ).show()
                        "zh-CN" -> Toast.makeText(context,
                            "这家餐厅没有注册其网站。\n如有任何疑问，请致电商店。",
                            Toast.LENGTH_LONG
                        ).show()
                        "zh-TW" -> Toast.makeText(context,
                            "這家餐廳沒有註冊其網站。\n如有任何疑問，請致電商店。",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    (context as MainActivity).moveToHomepage(url)
                }
            }
        }
        return customViewHolder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val binding = holder.binding
        val restaurant = restaurantList?.get(position)
        binding.tvItemName.text = restaurant?.name
        binding.tvItemMenu.text = restaurant?.menu
        binding.tvItemAddress.text = restaurant?.address
    }

    class CustomViewHolder(val binding: ItemRestaurantBinding) : RecyclerView.ViewHolder(binding.root)
}