package com.kcd.tax

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class TaxApplication

fun main(args: Array<String>) {
    runApplication<TaxApplication>(*args)
}
