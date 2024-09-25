package com.hr.webfluxdemo.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("product")
data class Product(
    val name: String,

    val price: Int,

    @Id
    val id: Long? = null,
)