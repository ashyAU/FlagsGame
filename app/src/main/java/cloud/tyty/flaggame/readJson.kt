package cloud.tyty.flaggame

import android.content.Context
import com.google.gson.Gson

/**
 * Reads JSON data from the assets folder of the application.
 *
 * @param context The context of the application.
 * @return A string containing the JSON data read from the assets.
 */
fun readJsonFromAssets(context: Context): String {
    return context.assets.open("countries.json").bufferedReader().use { it.readText()}
}

/**
 * Retrieves country codes from a JSON file located in the assets folder of the application.
 *
 * @param context The context of the application.
 * @return A HashMap containing country codes and corresponding country names.
 */
fun countryCodes(context: Context): HashMap<*, *> {
    // Read JSON data from assets
    val jsonString = readJsonFromAssets(context = context)
    // Parse JSON using Gson library
    val gson = Gson()
    // Convert JSON string to HashMap
    return gson.fromJson(jsonString, HashMap::class.java)
}