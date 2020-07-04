# NavComponent safeArgs plugin
-keepnames class com.lightteam.filesystem.model.FileModel
-keepnames class com.lightteam.modpeide.ui.explorer.utils.Operation

# ModPE IDE uses reflection to access some DrawerLayout fields
-keepclassmembernames class androidx.drawerlayout.widget.DrawerLayout {
    private androidx.customview.widget.ViewDragHelper mLeftDragger;
}
-keepclassmembernames class androidx.customview.widget.ViewDragHelper {
    private int mEdgeSize;
}