package monster.instance.domain;

/**
 * モンスターの性格を表す列挙型。
 * 各性格は特定のステータスに補正を与える。
 *
 * 上昇：1.1倍
 * 下降：0.9倍
 *
 * @author Suzuki
 * @version 1.0
 */
public enum Nature {

    // 攻撃↑
    ADAMANT(Stat.ATTACK, Stat.SP_ATTACK),   // いじっぱり
    BRAVE(Stat.ATTACK, Stat.SPEED),         // ゆうかん
    IMPISH(Stat.DEFENSE, Stat.SP_ATTACK),   // わんぱく
    LAX(Stat.DEFENSE, Stat.SP_DEFENSE),     // のんき

    // 防御↑
    BOLD(Stat.DEFENSE, Stat.ATTACK),        // ずぶとい

    // 特攻↑
    MODEST(Stat.SP_ATTACK, Stat.ATTACK),    // ひかえめ
    QUIET(Stat.SP_ATTACK, Stat.SPEED),      // れいせい

    // 特防↑
    CALM(Stat.SP_DEFENSE, Stat.ATTACK),     // おだやか
    CAREFUL(Stat.SP_DEFENSE, Stat.SP_ATTACK), // しんちょう
    SASSY(Stat.SP_DEFENSE, Stat.SPEED),     // なまいき

    // 素早さ↑
    TIMID(Stat.SPEED, Stat.ATTACK),         // おくびょう
    JOLLY(Stat.SPEED, Stat.SP_ATTACK);      // ようき

    /** 上昇するステータス */
    private final Stat increased;

    /** 下降するステータス */
    private final Stat decreased;

    Nature(Stat increased, Stat decreased) {
        this.increased = increased;
        this.decreased = decreased;
    }

    public Stat getIncreased() {
        return increased;
    }

    public Stat getDecreased() {
        return decreased;
    }

    /**
     * 指定されたステータスに対する補正倍率を返す。
     *
     * @param stat 対象ステータス
     * @return 補正倍率（1.1, 0.9, 1.0）
     */
    public double getModifier(Stat stat) {
        if (stat == increased) return 1.1;
        if (stat == decreased) return 0.9;
        return 1.0;
    }

    /**
     * ステータス種別を表す内部列挙型。
     */
    public enum Stat {
        HP, ATTACK, DEFENSE, SP_ATTACK, SP_DEFENSE, SPEED
    }
}