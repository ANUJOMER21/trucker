class KMMPreference(private val context: KMMContext) {

    fun put(key: String, value: String) {
        context.putString(key, value)
    }



    }