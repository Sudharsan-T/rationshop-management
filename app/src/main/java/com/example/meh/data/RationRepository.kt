package com.example.meh.data

import kotlinx.coroutines.flow.Flow

class RationRepository(private val db: AppDatabase) {
    private val userDao = db.userDao()
    private val productDao = db.productDao()
    private val tokenDao = db.tokenDao()
    private val saleDao = db.saleDao()

    // User operations
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun getUserById(uid: String) = userDao.getUserById(uid)

    // Product operations
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    fun searchProducts(query: String) = productDao.searchProducts(query)
    suspend fun addProduct(product: Product) = productDao.addProduct(product)
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)

    // Token operations
    suspend fun bookToken(token: Token) = tokenDao.bookToken(token)
    fun getTokensForUser(userId: String) = tokenDao.getTokensForUser(userId)
    val allTokens: Flow<List<Token>> = tokenDao.getAllTokens()

    // Sale operations
    suspend fun recordSale(sale: Sale) = saleDao.recordSale(sale)
    val allSales: Flow<List<Sale>> = saleDao.getAllSales()
    val totalRevenue: Flow<Double?> = saleDao.getTotalRevenue()
}
