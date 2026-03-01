package com.example.meh.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    suspend fun getUserById(uid: String): User?
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<Product>>

    @Insert
    suspend fun addProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Int): Product?
}

@Dao
interface TokenDao {
    @Insert
    suspend fun bookToken(token: Token)

    @Query("SELECT * FROM tokens WHERE userId = :userId")
    fun getTokensForUser(userId: String): Flow<List<Token>>

    @Query("SELECT * FROM tokens")
    fun getAllTokens(): Flow<List<Token>>
}

@Dao
interface SaleDao {
    @Insert
    suspend fun recordSale(sale: Sale)

    @Query("SELECT * FROM sales")
    fun getAllSales(): Flow<List<Sale>>

    @Query("SELECT SUM(totalPrice) FROM sales")
    fun getTotalRevenue(): Flow<Double?>
}
