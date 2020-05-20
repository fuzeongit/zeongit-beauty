package com.junjie.core.fragment

import com.fasterxml.jackson.annotation.JsonIgnore


abstract class MixIn {
    @get:JsonIgnore
    abstract val keyAsNumber: Number
}