package com.jaeyong.oop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
    scanBasePackages = ["com.jaeyong.oop"],
)
@EnableJpaRepositories(basePackages = ["com.jaeyong.oop"])
@EntityScan(basePackages = ["com.jaeyong.oop"])
class BootApplication

fun main(args: Array<String>) {
    runApplication<BootApplication>(*args)
}
