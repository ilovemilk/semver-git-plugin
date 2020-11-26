package io.wusa.exception

class DirtyWorkingTreeException: Exception {
    constructor(message: String, ex: Throwable?): super(message, ex) {}
    constructor(message: String): super(message) {}
    constructor(ex: Throwable): super(ex) {}
}