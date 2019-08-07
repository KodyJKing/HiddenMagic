package kjk.hiddenmagic.magictype;

public class MagicTypes {
    public static MagicType LIFE;

    public static void initialize() {
        LIFE = new MagicLife("life");
    }
}
