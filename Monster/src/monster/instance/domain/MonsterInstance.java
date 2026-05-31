package monster.instance.domain;

import java.util.List;

import monster.dictionary.domain.Monster;
import monster.dictionary.domain.Move;

/**
 * モンスターの個体データを表すクラス。
 * 種族情報（Species）を参照しつつ、個体固有の情報を保持する。
 *
 * @author Suzuki
 * @version 1.0
 */
public class MonsterInstance {
	
	/** 種族情報（図鑑データ） */
    private Monster species;

    /** ニックネーム */
    private String nickname;

    /** レベル */
    private int level;

    /** 現在の経験値 */
    private int currentExp;

    /** 性格 */
    private Nature nature;

    /** 個体値 */
    private Stats iv;

    /** 努力値 */
    private Stats ev;

    /** 実数値（レベル・性格・IV・EV から計算される） */
    private Stats actualStats;

    /** 覚えている技（最大4つ） */
    private List<Move> moves;

    /**
     * コンストラクタ。
     *
     * @param species 種族情報
     * @param nickname ニックネーム
     * @param level 初期レベル
     * @param nature 性格
     * @param iv 個体値
     * @param ev 努力値
     * @param moves 覚えている技
     */
    public MonsterInstance(
            Monster species,
            String nickname,
            int level,
            int currentExp,
            Nature nature,
            Stats iv,
            Stats ev,
            List<Move> moves
    ) {
        this.species = species;
        this.nickname = nickname;
        this.level = level;
        this.currentExp = currentExp;
        this.nature = nature;
        this.iv = iv;
        this.ev = ev;
        this.moves = moves;
        this.currentExp = 0;
        this.actualStats = StatsCalculator.calculate(this);
    }

    // ===== Getter =====

    public Monster getSpecies() { return species; }
    public String getNickname() { return nickname; }
    public int getLevel() { return level; }
    public int getCurrentExp() { return currentExp; }
    public Nature getNature() { return nature; }
    public Stats getIv() { return iv; }
    public Stats getEv() { return ev; }
    public Stats getActualStats() { return actualStats; }
    public List<Move> getMoves() { return moves; }

    // ===== Setter =====

    public void setNickname(String nickname) { this.nickname = nickname; }

    // ===== レベルアップ処理 =====

    /**
     * 経験値を獲得し、必要ならレベルアップする。
     *
     * @param amount 獲得経験値
     */
    public void gainExp(int amount) {
        this.currentExp += amount;

        while (currentExp >= ExpTable.requiredExp(level + 1)) {
            levelUp();
        }
    }

    /**
     * レベルアップ処理。
     * 実数値を再計算する。
     */
    private void levelUp() {
        this.level++;
        this.actualStats = StatsCalculator.calculate(this);
    }

    
    /**
     * 進化を実行する。
     * 種族情報を進化先に置き換え、実数値を再計算する。
     */
    public void evolve(Monster nextSpecies) {

        if (nextSpecies != null) {
            
        	this.species = nextSpecies;
            this.actualStats = StatsCalculator.calculate(this);
        }
    }
}
