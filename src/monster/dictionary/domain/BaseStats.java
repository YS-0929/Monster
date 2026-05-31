package monster.dictionary.domain;

/**
 * モンスターの種族値を表すクラス。
 * 
 * @author Suzuki
 * @version 1.0
 */
public class BaseStats {
	
	/** HP */
    private final int hp;
    
    /** こうげき */
    private final int attack;
    
    /** ぼうぎょ */
    private final int defense;
    
    /** とくこう */
    private final int spAttack;
    
    /** nとくぼう */
    private final int spDefense;
    
    /** すばやさ */
    private final int speed;

    /**
     * コンストラクタ。
     *
     * @param hp        HP
     * @param attack    こうげき
     * @param defense   ぼうぎょ
     * @param spAttack  とくこう
     * @param spDefense とくぼう
     * @param speed     すばやさ
     */
    public BaseStats(int hp, int attack, int defense,
                 int spAttack, int spDefense, int speed) {
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.spAttack = spAttack;
        this.spDefense = spDefense;
        this.speed = speed;
    }

    /**
     * HPを取得する。
     *
     * @return HP
     */
    public int getHp() {
    	return hp;
    }
    
    /**
     * こうげきを取得する。
     *
     * @return こうげき
     */
    public int getAttack() {
    	return attack;
    }
    
    /**
     * ぼうぎょを取得する。
     *
     * @return ぼうぎょ
     */
    public int getDefense() {
    	return defense;
    }
    
    /**
     * とくこうを取得する。
     *
     * @return とくこう
     */
    public int getSpAttack() {
    	return spAttack;
    }
    
    /**
     * とくぼうを取得する。
     *
     * @return とくぼう
     */
    public int getSpDefense() {
    	return spDefense;
    }
    
    /**
     * すばやさを取得する。
     *
     * @return すばやさ
     */
    public int getSpeed() {
    	return speed;
    }
}
