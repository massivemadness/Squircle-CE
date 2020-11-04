# NavComponent safeArgs plugin
-keepnames class com.brackeys.ui.filesystem.base.model.FileModel

# Brackeys IDE uses reflection to access some DrawerLayout fields
-keepclassmembernames class androidx.drawerlayout.widget.DrawerLayout {
    private androidx.customview.widget.ViewDragHelper mLeftDragger;
}
-keepclassmembernames class androidx.customview.widget.ViewDragHelper {
    private int mEdgeSize;
}