package com.ruimendes.core.domain.util

class DataErrorException(
    val error: DataError
): Exception()