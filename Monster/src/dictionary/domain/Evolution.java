package dictionary.domain;

/**
 * モンスターの進化関係と、その進化条件を表すドメインオブジェクト（値オブジェクト）。
 * <p>どのモンスターが、どのモンスターへ、どのような条件（レベルアップ、特定の道具の使用、なつき度など）で
 * 進化するのかという、図鑑における進化の系譜の1ステップを管理する。</p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class Evolution {
	
	/** 進化前のモンスターID */
    private final int fromId;
    
    /** 進化後のモンスターID */
    private final int toId;
    
    /** 進化条件の種別（例: LEVEL, ITEM, FRIENDSHIP） */
    private final String conditionType;
    
    /** 進化条件の具体的な値（例: 16, みずのいし, 220） */
    private final String conditionValue;

    /**
     * コンストラクタ。
     *
     * @param fromId         進化前のモンスターID
     * @param toId           進化後のモンスターID
     * @param conditionType  進化条件の種別（LEVEL, ITEM など）
     * @param conditionValue 進化条件の具体的な値（16, みずのいし など）
     */
    public Evolution(int fromId, int toId, String conditionType, String conditionValue) {
        this.fromId = fromId;
        this.toId = toId;
        this.conditionType = conditionType;
        this.conditionValue = conditionValue;
    }

    /**
     * 進化前のモンスターIDを取得する。
     *
     * @return 進化前のモンスターID
     */
    public int getFromId() { 
        return fromId; 
    }

    /**
     * 進化後のモンスターIDを取得する。
     *
     * @return 進化後のモンスターID
     */
    public int getToId() { 
        return toId; 
    }

    /**
     * 進化条件の種別（LEVEL / ITEM / FRIENDSHIP など）を取得する。
     *
     * @return 進化条件の種別を表す文字列
     */
    public String getConditionType() { 
        return conditionType; 
    }

    /**
     * 進化条件の具体的な値（レベル数、道具名、必要ステータスなど）を取得する。
     *
     * @return 進化条件の具体的な値を表す文字列
     */
    public String getConditionValue() { 
        return conditionValue; 
    }
}
