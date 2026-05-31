package monster.dictionary.domain;

import java.util.Collections;
import java.util.List;

import monster.dictionary.domain.type.Type;

/**
 * モンスターが使用できる技のデータを表すクラス。
 * バトルロジックは保持せず、純粋な技情報のみを管理する。
 *
 * @author Suzuki
 * @version 1.0
 */
public class Move {
	
	/** 技名 */
	private final String name;
	
	/** タイプ */
    private final Type type;
    
    /** 技カテゴリ(物理/特殊/変化) */
    private final MoveCategory category;
    
    /** 威力 */
    private final int power;
    
    /** 命中率 (%) */
    private final int accuracy;
    
    /** 急所率 (0.0625 など) */
    private final double criticalRate;
    
    /** 優先度 (先制技など) */
    private final int priority;
    
    /** 能力変化リスト */
    private final List<StatChange> statChanges;
    
    /** 状態異常効果（例：やけど、まひなど） */
    private final Status statusEffect;
    
    /** 状態異常の発生確率 (%) */
    private final double statusChance;

    /**
     * コンストラクタ。
     *
     * @param name          技名
     * @param type          タイプ
     * @param category      技カテゴリ（物理/特殊/変化）
     * @param power         威力
     * @param accuracy      命中率（%）
     * @param criticalRate  急所率（0.0625 など）
     * @param priority      優先度（先制技など）
     * @param statChanges   能力変化リスト
     */
    public Move(String name, Type type, MoveCategory category,
                int power, int accuracy, double criticalRate,
                int priority, List<StatChange> statChanges,
                Status statusEffect, double statusChance) {

        this.name = name;
        this.type = type;
        this.category = category;
        this.power = power;
        this.accuracy = accuracy;
        this.criticalRate = criticalRate;
        this.priority = priority;
        this.statChanges = List.copyOf(statChanges);
        this.statusEffect = statusEffect;
        this.statusChance = statusChance;
    }

    /**
     * 技名を取得する。
     *
     * @return 技名
     */
    public String getName() {
    	return name;
    }
    
    /**
     * 技のタイプ（ほのお、みずなど）を取得する。
     *
     * @return {@link Type} オブジェクト
     */
    public Type getType() {
    	return type;
    }
    
    /**
     * 技のカテゴリ（物理／特殊／変化）を取得する。
     *
     * @return {@link MoveCategory} オブジェクト
     */
    public MoveCategory getCategory() {
    	return category;
    }
    
    /**
     * 技の威力を取得する。
     *
     * @return 威力（変化技の場合は0や固有の値を想定）
     */
    public int getPower() {
    	return power;
    }
    
    /**
     * 技の命中率を取得する。
     *
     * @return 命中率（%単位）
     */
    public int getAccuracy() {
    	return accuracy;
    }
    
    /**
     * 技の基本急所率を取得する。
     *
     * @return 急所率（0.0625 など）
     */
    public double getCriticalRate() {
    	return criticalRate;
    }
    
    /**
     * 技の優先度を取得する。
     * <p>「でんこうせっか」などの先制技はプラス、「トリックルーム」などの後攻技はマイナスの値となる。</p>
     *
     * @return 優先度（通常は0）
     */
    public int getPriority() {
    	return priority;
    }
    
    /**
     * 技によって引き起こされる能力変化（バフ・デバフ）のリストを取得する。
     *
     * @return {@link StatChange} のリスト
     */
    public List<StatChange> getStatChanges() {
    	return Collections.unmodifiableList(statChanges);
    }
    
    /**
	 * 技によって引き起こされる状態異常効果を取得する。
	 *
	 * @return 状態異常効果（例：やけど、まひなど）
	 */
    public Status getStatusEffect() {
		return statusEffect;
	}
	
	/**
	 * 技によって状態異常が発生する確率を取得する。
	 *
	 * @return 状態異常の発生確率（%単位）
	 */
	public double getStatusChance() {
		return statusChance;
	}
}
