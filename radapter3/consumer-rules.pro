-keepclassmembers class * extends me.wcy.radapter3.RViewHolder {
    public <init>(*);
}
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
  public static * inflate(android.view.LayoutInflater);
  public static * inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
}