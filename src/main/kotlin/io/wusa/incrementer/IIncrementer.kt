package io.wusa.incrementer

import io.wusa.Version


interface IIncrementer {

    fun increment(version: Version): Version
}