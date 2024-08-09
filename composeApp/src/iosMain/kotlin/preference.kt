import platform.Foundation.NSUserDefaults





actual fun KMMContext.putString(key: String, value: String) {
    NSUserDefaults.standardUserDefaults.setObject(value, key)
}

