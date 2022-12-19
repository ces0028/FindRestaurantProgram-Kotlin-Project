package kr.or.mrhi.findrestaurant

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kr.or.mrhi.findrestaurant.data.Restaurants
import kr.or.mrhi.findrestaurant.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    companion object {
        const val DB_NAME = "restaurantDB"
        var VERSION = 1
    }

    lateinit var binding: ActivityMainBinding
    lateinit var id: String
    lateinit var language: String
    lateinit var name: String
    lateinit var address: String
    lateinit var subwayInfo: String
    lateinit var openingHours: String
    lateinit var webPage: String
    lateinit var phone: String
    lateinit var menu: String
    lateinit var customAdapter: CustomAdapter
    private var restaurantList: MutableList<Restaurant>? = mutableListOf<Restaurant>()
    private var getRestaurant = mutableListOf<Restaurant>()
    private var selectLanguage = "ko"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        val dbConnector = DBConnector(this@MainActivity, DB_NAME, VERSION)
        restaurantList = dbConnector.selectRestaurantAll()

        if (restaurantList == null || restaurantList?.size == 0) {
            val retrofit = Retrofit.Builder()
                .baseUrl(SeoulOpenAPI.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(SeoulOpenService::class.java)
            service.getRestaurant(SeoulOpenAPI.API_KEY, SeoulOpenAPI.LIMIT).enqueue(object :
                Callback<Restaurants> {
                override fun onResponse(call: Call<Restaurants>, response: Response<Restaurants>) {
                    val data = response.body()
                    data?.let {
                        for (restaurant in it.TbVwRestaurants.row) {
                            id = restaurant.POST_SN
                            language = restaurant.LANG_CODE_ID
                            name = restaurant.POST_SJ
                            address = restaurant.NEW_ADDRESS
                            if (address.substring(0 until 1) == "1") {
                                address = address.substring(8)
                            } else {
                                address = address.substring(6)
                            }
                            address = address.replace("특별시", "")
                            subwayInfo = restaurant.SUBWAY_INFO
                            openingHours = if(restaurant.CMMN_USE_TIME.isBlank()) {
                                " - "
                            } else {
                                restaurant.CMMN_USE_TIME
                            }
                            webPage = if(restaurant.CMMN_HMPG_URL.isBlank()) {
                                " - "
                            } else {
                                restaurant.CMMN_HMPG_URL
                            }
                            phone = restaurant.CMMN_TELNO
                            menu = if(restaurant.FD_REPRSNT_MENU.isBlank()) {
                                " - "
                            } else {
                                restaurant.FD_REPRSNT_MENU
                            }
                            val getRestaurantData = Restaurant(id,language,name,address,subwayInfo,openingHours,webPage,phone,menu)
                            getRestaurant.add(getRestaurantData)
                        }
                        if (getRestaurant.size != 0) {
                            for (i in 0 until getRestaurant.size - 1) {
                                val restaurant = getRestaurant.get(i)
                                dbConnector.insertRestaurant(restaurant)
                            }
                            restaurantList = getRestaurant
                        } else {
                            Log.d(
                                "kr.or.mrhi",
                                "MainActivity.startProcess() DATA IS EMPTY"
                            )
                        }
                        Log.d("LogCheck", "centerList ${restaurantList?.size}")
                        restaurantList = dbConnector.selectRestaurant(selectLanguage)
                        customAdapter = CustomAdapter(this@MainActivity, restaurantList)
                        binding.recyclerView.adapter = customAdapter
                        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                    } ?: let {
                        Log.d(
                            "kr.or.mrhi",
                            "MainActivity.onFailure() InformationCenter Data is empty"
                        )
                        Toast.makeText(
                            this@MainActivity,
                            "InformationCenter Data is empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Restaurants>, t: Throwable) {
                    Log.d(
                        "kr.or.mrhi",
                        "MainActivity.onFailure() InformationCenter Load Error ${t.printStackTrace()}"
                    )
                    Toast.makeText(
                        this@MainActivity,
                        "InformationCenter Load Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            restaurantList = dbConnector.selectRestaurant(selectLanguage)
            customAdapter = CustomAdapter(this@MainActivity, restaurantList)
            binding.recyclerView.adapter = customAdapter
            binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val dbConnector = DBConnector(applicationContext, DB_NAME, VERSION)
        if(item.itemId == R.id.menuLanguageKo) selectLanguage = "ko"
        if(item.itemId == R.id.menuLanguageEn) selectLanguage = "en"
        if(item.itemId == R.id.menuLanguageJa) selectLanguage = "ja"
        if(item.itemId == R.id.menuLanguageZhCN) selectLanguage = "zh-CN"
        if(item.itemId == R.id.menuLanguageZhTW) selectLanguage = "zh-TW"
        restaurantList?.clear()
        dbConnector.selectRestaurant(selectLanguage)?.let{restaurantList?.addAll(it)}
        customAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_overflow, menu)
        val dbConnector = DBConnector(applicationContext, DB_NAME, VERSION)
        val searchMenu = menu?.findItem(R.id.menuSearch)
        val searchView = searchMenu?.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(query: String?): Boolean {
                if (query.isNullOrBlank()) {
                    restaurantList?.clear()
                    dbConnector.selectRestaurant(selectLanguage)?.let { restaurantList?.addAll(it) }
                    customAdapter.notifyDataSetChanged()
                } else {
                    restaurantList?.clear()
                    dbConnector.searchRestaurant(selectLanguage, query)?.let { restaurantList?.addAll(it) }
                    customAdapter.notifyDataSetChanged()
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    fun callRestaurant(phone: String) {
        val myUri = Uri.parse("tel:${phone}")
        val intent = Intent(Intent.ACTION_DIAL, myUri)
        startActivity(intent)
    }

    fun moveToHomepage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}