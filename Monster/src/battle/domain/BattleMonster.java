package battle.domain;

import monster.instance.domain.MonsterInstance;
import monster.instance.domain.Stats;

/**
 * バトル中のモンスターを表すクラス。
 * MonsterInstance を元に、戦闘用の一時的な状態（現在HP・状態異常など）を保持する。
 */
public class BattleMonster {

    private final MonsterInstance instance;

    // 戦闘中の現在HP
    private int currentHp;

    // 戦闘不能フラグ
    private boolean fainted = false;

 // ランク補正（攻撃・防御・特攻・特防・素早さ）
    private int atkStage = 0;
    private int defStage = 0;
    private int spAStage = 0;
    private int spDStage = 0;
    private int speStage = 0;
    private int accStage = 0;
    private int evaStage = 0;
    
    public BattleMonster(MonsterInstance instance) {
        this.instance = instance;
        Stats actual = instance.getActualStats();
        this.currentHp = actual.getHp();
    }

    // ====== 基本情報 ======

    public MonsterInstance getInstance() {
        return instance;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public boolean isFainted() {
        return fainted;
    }

    public boolean isAlive() {
        return !fainted;
    }

    public int getMaxHp() {
        return instance.getActualStats().getHp();
    }
    
    // getter
    public int getAtkStage() { return atkStage; }
    public int getDefStage() { return defStage; }
    public int getSpAStage() { return spAStage; }
    public int getSpDStage() { return spDStage; }
    public int getSpeStage() { return speStage; }
    public int getAccStage() { return accStage; }
    public int getEvaStage() { return evaStage; }
    

    // ====== HP 操作 ======

    public void damage(int amount) {
        if (fainted) return;

        currentHp = Math.max(0, currentHp - Math.max(0, amount));
        if (currentHp == 0) {
            fainted = true;
        }
    }

    public void heal(int amount) {
        if (fainted) return; // 戦闘不能からの回復を許すならここは変える

        int maxHp = getMaxHp();
        currentHp = Math.min(maxHp, currentHp + Math.max(0, amount));
    }

    public void fullHeal() {
        this.currentHp = getMaxHp();
        this.fainted = false;
    }

    // ====== バトル開始時の初期化 ======

    public void resetForBattle() {
        this.currentHp = getMaxHp();
        this.fainted = false;
        // 状態異常やランク補正を追加したらここでリセット
    }
    
    // ====== ランク補正の操作 ====== //
    
    public void changeAtkStage(int delta) {
        atkStage = clampStage(atkStage + delta);
    }
    public void changeDefStage(int delta) {
        defStage = clampStage(defStage + delta);
    }
    public void changeSpAStage(int delta) {
        spAStage = clampStage(spAStage + delta);
    }
    public void changeSpDStage(int delta) {
        spDStage = clampStage(spDStage + delta);
    }
    public void changeSpeStage(int delta) {
        speStage = clampStage(speStage + delta);
    }
    public void changeAccStage(int delta) {
    	accStage = clampStage(accStage + delta);
    }
    public void changeEvaStage(int delta) {
    	evaStage = clampStage(evaStage + delta);
    }

    // -6 ～ +6 に制限
    private int clampStage(int value) {
        return Math.max(-6, Math.min(6, value));
    }
}