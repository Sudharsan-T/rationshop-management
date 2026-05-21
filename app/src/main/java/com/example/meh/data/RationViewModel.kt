package com.example.meh.data

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel for the Ration Shop app.
 * Manages all data logic, Firebase connections, and UI state.
 */
class RationViewModel(application: Application) : AndroidViewModel(application) {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    val allProducts = MutableLiveData<List<Product>>(emptyList())
    val allSales = MutableLiveData<List<Sale>>(emptyList())
    val allTokens = MutableLiveData<List<Token>>(emptyList())
    val stockUpdates = MutableLiveData<List<StockUpdate>>(emptyList())
    
    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> = _cartItems
    
    val totalRevenue: LiveData<Double> = allSales.map { sales: List<Sale> ->
        sales.sumOf { it.totalPrice }
    }

    private var productsListener: ValueEventListener? = null
    private var salesListener: ValueEventListener? = null
    private var tokensListener: ValueEventListener? = null
    private var stockUpdatesListener: ValueEventListener? = null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                if (_currentUser.value == null) {
                    fetchUserData(user.uid)
                }
                startRealtimeListeners()
            } else {
                _currentUser.postValue(null)
                removeListeners()
            }
        }
    }

    private fun startRealtimeListeners() {
        removeListeners() 

        productsListener = db.child("products").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Product>()
                for (child in snapshot.children) {
                    try {
                        child.getValue(Product::class.java)?.let { list.add(it) }
                    } catch (e: Exception) {}
                }
                allProducts.postValue(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        salesListener = db.child("sales").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Sale>()
                for (child in snapshot.children) {
                    child.getValue(Sale::class.java)?.let { list.add(it) }
                }
                allSales.postValue(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        tokensListener = db.child("tokens").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Token>()
                for (child in snapshot.children) {
                    child.getValue(Token::class.java)?.let { list.add(it) }
                }
                allTokens.postValue(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        stockUpdatesListener = db.child("stock_updates").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<StockUpdate>()
                for (child in snapshot.children) {
                    child.getValue(StockUpdate::class.java)?.let { list.add(it) }
                }
                stockUpdates.postValue(list.reversed()) 
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun removeListeners() {
        productsListener?.let { db.child("products").removeEventListener(it) }
        salesListener?.let { db.child("sales").removeEventListener(it) }
        tokensListener?.let { db.child("tokens").removeEventListener(it) }
        stockUpdatesListener?.let { db.child("stock_updates").removeEventListener(it) }
        productsListener = null
        salesListener = null
        tokensListener = null
        stockUpdatesListener = null
    }

    fun updateCartQuantity(productId: Int, delta: Double): Pair<Boolean, String> {
        val product = allProducts.value?.find { it.id == productId } ?: return false to "Product not found"
        val currentList = _cartItems.value?.toMutableList() ?: mutableListOf()
        val existingItem = currentList.find { it.productId == productId }
        
        val currentQtyInCart = existingItem?.quantity ?: 0.0
        val newQty = currentQtyInCart + delta
        
        if (newQty < 0) return false to "Invalid quantity"
        
        if (newQty == 0.0) {
            currentList.removeAll { it.productId == productId }
            _cartItems.value = currentList
            return true to ""
        }

        if (product.stock < newQty) {
            return false to "Not enough stock in shop"
        }

        if (existingItem != null) {
            val updatedItem = existingItem.copy(quantity = newQty)
            currentList[currentList.indexOf(existingItem)] = updatedItem
        } else {
            val cartItem = CartItem(
                id = java.util.UUID.randomUUID().toString(),
                productId = product.id,
                userId = auth.currentUser?.uid ?: "",
                productName = product.name,
                quantity = newQty,
                unitPrice = product.price,
                unit = product.unit
            )
            currentList.add(cartItem)
        }
        
        _cartItems.value = currentList
        return true to ""
    }

    fun removeFromCart(itemId: String) {
        val currentList = _cartItems.value?.toMutableList() ?: mutableListOf()
        currentList.removeAll { it.id == itemId }
        _cartItems.value = currentList
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun register(email: String, password: String, user: User, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid
                if (uid != null) {
                    val newUser = user.copy(uid = uid)
                    db.child("users").child(uid).setValue(newUser).await()
                    _currentUser.value = newUser
                    callback(true, null)
                } else {
                    callback(false, "Failed to get user ID")
                }
            } catch (e: Exception) {
                callback(false, e.message)
            }
        }
    }

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid
                if (uid != null) {
                    val snapshot = db.child("users").child(uid).get().await()
                    val userProfile = snapshot.getValue(User::class.java)
                    if (userProfile != null) {
                        _currentUser.value = userProfile
                        callback(true, null)
                    } else {
                        callback(false, "User profile not found in database")
                    }
                } else {
                    callback(false, "Authentication failed")
                }
            } catch (e: Exception) {
                callback(false, e.message)
            }
        }
    }

    private fun fetchUserData(uid: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.child("users").child(uid).get().await()
                val user = snapshot.getValue(User::class.java)
                _currentUser.postValue(user)
            } catch (e: Exception) {}
        }
    }

    fun updateStock(productId: Int, addedAmount: Int) {
        viewModelScope.launch {
            try {
                val snapshot = db.child("products").orderByChild("id").equalTo(productId.toDouble()).get().await()
                for (child in snapshot.children) {
                    val product = child.getValue(Product::class.java)
                    if (product != null) {
                        val newStock = product.stock + addedAmount
                        child.ref.child("stock").setValue(newStock).await()
                        
                        val updateKey = db.child("stock_updates").push().key
                        if (updateKey != null) {
                            val update = StockUpdate(
                                id = updateKey,
                                productName = product.name,
                                addedAmount = addedAmount,
                                totalStock = newStock,
                                date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
                                unit = product.unit
                            )
                            db.child("stock_updates").child(updateKey).setValue(update)
                        }
                        break
                    }
                }
            } catch (e: Exception) {}
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            try {
                val snapshot = db.child("products").orderByChild("id").equalTo(productId.toDouble()).get().await()
                for (child in snapshot.children) {
                    child.ref.removeValue().await()
                    break
                }
            } catch (e: Exception) {}
        }
    }

    fun updateProductDetails(product: Product) {
        viewModelScope.launch {
            try {
                val snapshot = db.child("products").orderByChild("id").equalTo(product.id.toDouble()).get().await()
                for (child in snapshot.children) {
                    child.ref.setValue(product).await()
                    break
                }
            } catch (e: Exception) {}
        }
    }

    fun bookToken(token: Token) {
        viewModelScope.launch {
            val key = db.child("tokens").push().key ?: return@launch
            val newToken = token.copy(id = key.hashCode())
            db.child("tokens").child(key).setValue(newToken).await()
        }
    }

    fun cancelToken(tokenId: Int) {
        viewModelScope.launch {
            val snapshot = db.child("tokens").get().await()
            for (child in snapshot.children) {
                val t = child.getValue(Token::class.java)
                if (t?.id == tokenId) {
                    child.ref.removeValue().await()
                    break
                }
            }
        }
    }

    fun canUserBuy(productId: Int, userId: String, requestedQuantity: Double): Pair<Boolean, String> {
        val products = allProducts.value ?: return false to "Products not loaded"
        val product = products.find { it.id == productId } ?: return false to "Product not found"
        
        if (product.stock < requestedQuantity) {
            return false to "Not enough stock in shop"
        }
        return true to ""
    }

    fun recordSale(sale: Sale) {
        viewModelScope.launch {
            val key = db.child("sales").push().key ?: return@launch
            val newSale = sale.copy(id = key.hashCode())
            db.child("sales").child(key).setValue(newSale).await()
            
            val productSnapshot = db.child("products").orderByChild("id").equalTo(sale.productId.toDouble()).get().await()
            for (child in productSnapshot.children) {
                val product = child.getValue(Product::class.java)
                if (product != null) {
                    val newStock = product.stock - sale.quantity.toInt()
                    child.ref.child("stock").setValue(newStock).await()
                    break
                }
            }
        }
    }

    fun logout() {
        auth.signOut()
    }
}
