package easter.egg.passmark.utils.extensions

import easter.egg.passmark.data.models.content.password.Password

/** note that only one of the given arguments [localId] or [cloudId] should be non null */
fun List<Password>.findPassword(
    localId: Int?,
    cloudId: Int?
): Password? {
    val useCloudId = cloudId != null
    val useLocalId = localId != null
    return this.find { p ->
        if (useCloudId) {
            p.cloudId == cloudId
        } else if (useLocalId) {
            p.localId == localId
        } else {
            false
        }
    }
}
