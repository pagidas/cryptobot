package me.pysquad.cryptobot.common

import java.lang.RuntimeException

class MissingEnvVariableException(message: String): RuntimeException(message)