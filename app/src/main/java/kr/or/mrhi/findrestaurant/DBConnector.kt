package kr.or.mrhi.findrestaurant

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBConnector(context: Context, dbName: String, version: Int) : SQLiteOpenHelper(context, dbName, null, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query = """
            CREATE TABLE restaurantTBL(
            id TEXT PRIMARY KEY,
            language TEXT,
            name TEXT,
            address TEXT,
            subwayInfo TEXT,
            openingHours TEXT,
            webPage TEXT,
            phone TEXT,
            menu TEXT
            )
        """.trimIndent()
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val query = """
            DROP TABLE restaurantTBL
        """.trimIndent()
        db?.execSQL(query)
        this.onCreate(db)
    }

    fun insertRestaurant(restaurant: Restaurant) : Boolean{
        var flag = false
        val query = """
            INSERT INTO restaurantTBL (id, language, name, address, subwayInfo, openingHours, webPage, phone, menu) 
            VALUES ('${restaurant.id}','${restaurant.language}','${restaurant.name}','${restaurant.address}',
            '${restaurant.subwayInfo}','${restaurant.openingHours}','${restaurant.webPage}','${restaurant.phone}','${restaurant.menu}')
        """.trimIndent()
        val db = this.writableDatabase
        try {
            db.execSQL(query)
            flag = true
            Log.d("kr.or.mrhi", "insertRestaurant() SUCCESS")
        } catch (e: Exception) {
            Log.d("kr.or.mrhi", "insertRestaurant() ${e.printStackTrace()}")
            flag= false
        } finally {
            db.close()
        }
        return flag
    }

    fun selectRestaurantAll(): MutableList<Restaurant>? {
        var restaurantList: MutableList<Restaurant>? = mutableListOf<Restaurant>()
        var cursor: Cursor? = null
        val db = this.readableDatabase
        val query = """
            SELECT * FROM restaurantTBL
        """.trimIndent()
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.count > 0) {
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val language = cursor.getString(1)
                    val name = cursor.getString(2)
                    val address = cursor.getString(3)
                    val subwayInfo = cursor.getString(4)
                    val openingHours = cursor.getString(5)
                    val webPage = cursor.getString(6)
                    val phone = cursor.getString(7)
                    val menu = cursor.getString(8)
                    val restaurant = Restaurant(id, language, name, address, subwayInfo, openingHours, webPage, phone, menu)
                    restaurantList?.add(restaurant)
                    Log.d("kr.or.mrhi", "selectRestaurantAll() SUCCESS")
                }
            } else {
                restaurantList = null
            }
        } catch (e: Exception) {
            Log.d("kr.or.mrhi", "selectRestaurantAll() ${e.printStackTrace()}")
        } finally {
            cursor?.close()
            db.close()
        }
        return restaurantList
    }

    fun selectRestaurant(selectLanguage: String): MutableList<Restaurant>? {
        var restaurantList: MutableList<Restaurant>? = mutableListOf<Restaurant>()
        var cursor: Cursor? = null
        val db = this.readableDatabase
        val query = """
            SELECT * FROM restaurantTBL WHERE language = '${selectLanguage}' ORDER BY name
        """.trimIndent()
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.count > 0) {
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val language = cursor.getString(1)
                    val name = cursor.getString(2)
                    val address = cursor.getString(3)
                    val subwayInfo = cursor.getString(4)
                    val openingHours = cursor.getString(5)
                    val webPage = cursor.getString(6)
                    val phone = cursor.getString(7)
                    val menu = cursor.getString(8)
                    val restaurant = Restaurant(id, language, name, address, subwayInfo, openingHours, webPage, phone, menu)
                    restaurantList?.add(restaurant)
                    Log.d("kr.or.mrhi", "selectRestaurant() SUCCESS")
                }
            } else {
                restaurantList = null
            }
        } catch (e: Exception) {
            Log.d("kr.or.mrhi", "selectRestaurant() ${e.printStackTrace()}")
        } finally {
            cursor?.close()
            db.close()
        }
        return restaurantList
    }

    fun searchRestaurant(selectLanguage: String, searchQuery: String) : MutableList<Restaurant>? {
        var restaurantList: MutableList<Restaurant>? = mutableListOf<Restaurant>()
        var cursor: Cursor? = null
        val db = this.readableDatabase
        val query = """
            SELECT * FROM restaurantTBL WHERE (address LIKE '%${searchQuery}%' OR name Like '%${searchQuery}%' 
            OR menu LIKE '%${searchQuery}%') AND (language = '${selectLanguage}') 
        """.trimIndent()
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.count > 0) {
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val language = cursor.getString(1)
                    val name = cursor.getString(2)
                    val address = cursor.getString(3)
                    val subwayInfo = cursor.getString(4)
                    val openingHours = cursor.getString(5)
                    val webPage = cursor.getString(6)
                    val phone = cursor.getString(7)
                    val menu = cursor.getString(8)
                    val restaurant = Restaurant(id, language, name, address, subwayInfo, openingHours, webPage, phone, menu)
                    restaurantList?.add(restaurant)
                    Log.d("kr.or.mrhi", "searchRestaurant() SUCCESS")
                }
            } else {
                restaurantList = null
            }
        } catch (e: Exception) {
            Log.d("kr.or.mrhi", "searchRestaurant() ${e.printStackTrace()}")
        } finally {
            cursor?.close()
            db.close()
        }
        return restaurantList
    }
}