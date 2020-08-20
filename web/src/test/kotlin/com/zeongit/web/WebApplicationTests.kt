package com.zeongit.web

import org.junit.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class WebApplicationTests {

    @Test
    fun contextLoads() {

        val a = listOf("1","2","3")
        val b = listOf("3","5")
        println(a.subtract(b))
    }

}
