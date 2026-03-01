package com.example.meh.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Data model for a User in the system.
 * Stores personal details and the user role (ADMIN or CUSTOMER).
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String = "", // Unique identifier from Firebase Auth
    val name: String = "",
    val rationCardNumber: String = "",
    val mobileNumber: String = "",
    val address: String = "",
    val email: String = "",
    val role: String = "CUSTOMER"
) : Serializable

/**
 * Data model for a Product available in the ration shop.
 */
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val unit: String = "", // e.g., "kg", "liter"
    val imageUrl: String = "",
    val maxQuota: Int = 3 // Maximum quantity a customer can buy
) : Serializable

/**
 * Data model for a Token booking.
 * Used by customers to book a slot for visiting the shop.
 */
@Entity(tableName = "tokens")
data class Token(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "",
    val date: String = "",
    val slot: String = "",
    val status: String = "" // e.g., "BOOKED", "COMPLETED"
) : Serializable

/**
 * Data model for a Sale record.
 * Created when a customer successfully completes a purchase.
 */
@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int = 0,
    val productName: String = "",
    val userId: String = "",
    val quantity: Double = 0.0,
    val totalPrice: Double = 0.0,
    val date: String = "",
    val unit: String = "",
    val invoiceId: String = "" // Generated invoice ID for the transaction
) : Serializable

/**
 * Data model for an item in the shopping cart.
 * Managed locally in the app until purchase is finalized.
 */
data class CartItem(
    val id: String = "",
    val productId: Int = 0,
    val userId: String = "",
    val productName: String = "",
    val quantity: Double = 0.0,
    val unitPrice: Double = 0.0,
    val unit: String = ""
) : Serializable

/**
 * Data model for a Stock Update record.
 * Used by admins to track when and how much stock was added.
 */
data class StockUpdate(
    val id: String = "",
    val productName: String = "",
    val addedAmount: Int = 0,
    val totalStock: Int = 0,
    val date: String = "",
    val unit: String = ""
) : Serializable
