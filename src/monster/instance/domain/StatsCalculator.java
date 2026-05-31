package monster.instance.domain;

import monster.dictionary.domain.BaseStats;
import monster.instance.domain.Nature.Stat;

/**
 * モンスターの実数値を計算するユーティリティクラス。
 * 種族値・IV・EV・レベル・性格補正を用いて実数値を算出する。
 *
 * モンスター準拠の計算式：
 *
 * HP:
 *   ((種族値 * 2 + IV + EV/4) * Lv / 100) + Lv + 10
 *
 * その他:
 *   ( ((種族値 * 2 + IV + EV/4) * Lv / 100) + 5 ) * 性格補正
 *
 * @author Suzuki
 * @version 1.0
 */
public class StatsCalculator {
	
	/**
     * MonsterInstance の実数値を計算して Stats として返す。
     *
     * @param instance モンスター個体
     * @return 計算された実数値
     */
    public static Stats calculate(MonsterInstance instance) {

        BaseStats base = instance.getSpecies().getBaseStats();
        Stats iv = instance.getIv();
        Stats ev = instance.getEv();
        int level = instance.getLevel();
        Nature nature = instance.getNature();

        int hp = calcHp(base.getHp(), iv.getHp(), ev.getHp(), level);
        int attack = calcOther(base.getAttack(), iv.getAttack(), ev.getAttack(), level, nature.getModifier(Stat.ATTACK));
        int defense = calcOther(base.getDefense(), iv.getDefense(), ev.getDefense(), level, nature.getModifier(Stat.DEFENSE));
        int spAttack = calcOther(base.getSpAttack(), iv.getSpAttack(), ev.getSpAttack(), level, nature.getModifier(Stat.SP_ATTACK));
        int spDefense = calcOther(base.getSpDefense(), iv.getSpDefense(), ev.getSpDefense(), level, nature.getModifier(Stat.SP_DEFENSE));
        int speed = calcOther(base.getSpeed(), iv.getSpeed(), ev.getSpeed(), level, nature.getModifier(Stat.SPEED));

        return new Stats(hp, attack, defense, spAttack, spDefense, speed);
    }

    /** HP の計算式 */
    private static int calcHp(int base, int iv, int ev, int level) {
        return ((base * 2 + iv + (ev / 4)) * level) / 100 + level + 10;
    }

    /** HP 以外のステータス計算式 */
    private static int calcOther(int base, int iv, int ev, int level, double natureModifier) {
        int value = ((base * 2 + iv + (ev / 4)) * level) / 100 + 5;
        return (int) Math.floor(value * natureModifier);
    }
}