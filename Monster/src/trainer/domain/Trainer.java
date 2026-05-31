package trainer.domain;

import java.util.List;

import monster.instance.domain.MonsterInstance;

/**
 * トレーナーを表すドメインクラス。
 * <p>
 * トレーナー名、所持金、手持ちモンスター（MonsterInstance）を保持する。
 * MonsterInstance は別ドメイン（instance）で管理されるため、
 * Trainer はそれらを参照するだけで生成・削除は行わない。
 * </p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class Trainer {

    /** トレーナー名 */
    private String name;

    /** 所持金 */
    private int money;
    
    /** トレーナー固有のボックス（倉庫） */
    private List<MonsterInstance> box;
    
    /** 手持ちモンスター（最大3体を想定） */
    private List<MonsterInstance> party;

    /**
     * コンストラクタ。
     *
     * @param name トレーナー名
     * @param money 所持金
     * @param box トレーナー固有のボックス（倉庫）
     * @param party 手持ちモンスターのリスト
     */
    public Trainer(String name, int money, List<MonsterInstance> box, List<MonsterInstance> party) {
        this.name = name;
        this.money = money;
        this.box = box;
        this.party = party;
    }
    
    // ===== Getter =====

    /** @return トレーナー名 */
    public String getName() { return name; }

    /** @return 所持金 */
    public int getMoney() { return money; }
    
    /** @return トレーナー固有のボックス（倉庫） */
    public List<MonsterInstance> getBox() { return box; }

    /** @return 手持ちモンスターのリスト */
    public List<MonsterInstance> getParty() { return party; }

    // ===== 操作メソッド =====

    /**
     * 所持金を増やす。
     *
     * @param amount 増加額
     */
    public void addMoney(int amount) {
        this.money += amount;
    }

    /**
     * 所持金を消費する。
     *
     * @param amount 消費額
     */
    public void spendMoney(int amount) {
        this.money -= amount;
    }
    
    /** 手持ちを更新する（box の中から選ぶ） */
    public void setParty(List<MonsterInstance> newParty) {
        if (newParty.size() > 3) {
            throw new IllegalArgumentException("手持ちは最大3体までです");
        }
        this.party = newParty;
    }

    /** box に個体を追加 */
    public void addToBox(MonsterInstance inst) {
        box.add(inst);
    }
}