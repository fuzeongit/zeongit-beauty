package com.junjie.core.exception

class PermissionException : BaseException {
    constructor(message: String, status: Int, data: Any?) : super(message, status, data) {}

    constructor(message: String, status: Int) : super(message, status) {}

    constructor(message: String) : super(message) {
        super.status = 403
    }
}
