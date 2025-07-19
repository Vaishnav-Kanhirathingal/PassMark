package easter.egg.passmark.utils.annotation

/** this annotation is applied to [MobilePreview] and [MobileHorizontalPreview] to restrict their
 * calling by requiring opt-in */
@RequiresOptIn(
    message = "This is meant only for production. Do not use in production.",
    level = RequiresOptIn.Level.ERROR
)
@Target(allowedTargets = [AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS])
@Retention(value = AnnotationRetention.BINARY)
annotation class PreviewRestricted