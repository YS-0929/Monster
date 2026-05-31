package trainer.service;

import java.util.ArrayList;
import java.util.List;

import monster.instance.domain.MonsterInstance;
import trainer.domain.Trainer;
import trainer.repository.TrainerRepository;

/**
 * Trainer（トレーナー）を管理するサービスクラス。
 * <p>
 * トレーナーの生成、検索、保存、手持ち変更などの操作を提供し、
 * 永続化は TrainerRepository に委譲する。
 * </p>
 *
 * トレーナーは固有のボックス（MonsterInstance の倉庫）を持ち、
 * その中から最大3体を手持ちとして選択する。
 *
 * @author Suzuki
 * @version 1.0
 */
public class TrainerService {

    /** 永続化を担当するリポジトリ */
    private final TrainerRepository repository;

    /** メモリ上のトレーナー一覧 */
    private final List<Trainer> trainers = new ArrayList<>();

    public TrainerService(TrainerRepository repository) {
        this.repository = repository;
        this.trainers.addAll(repository.loadAll());
    }

    /**
     * すべてのトレーナーを返す。
     *
     * @return トレーナー一覧（変更不可）
     */
    public List<Trainer> findAll() {
        return List.copyOf(trainers);
    }

    /**
     * 新しいトレーナーを作成して登録する。
     * <p>
     * 初期状態では box は空、party も空。
     * </p>
     *
     * @param name トレーナー名
     * @param money 初期所持金
     * @return 生成されたトレーナー
     */
    public Trainer create(String name, int money) {
        Trainer t = new Trainer(
                name,
                money,
                new ArrayList<>(),   // box
                new ArrayList<>()    // party
        );
        trainers.add(t);
        save();
        return t;
    }

    /**
     * トレーナーの box に個体を追加する。
     *
     * @param trainer 対象トレーナー
     * @param instance 追加する個体
     */
    public void addToBox(Trainer trainer, MonsterInstance inst) {
        for (Trainer t : trainers) {
            if (t.getBox().contains(inst)) {
                throw new IllegalStateException("この個体はすでに別のトレーナーが所有しています");
            }
        }
        trainer.getBox().add(inst);
        save();
    }

    /**
     * 手持ち（party）を更新する。
     * <p>
     * newParty は trainer.box に含まれている必要がある。
     * 手持ちは 0〜3 匹まで許可する。
     * </p>
     */
    public void setParty(Trainer trainer, List<MonsterInstance> newParty) {

        if (newParty.size() > 3) {
            throw new IllegalArgumentException("手持ちは最大3体までです");
        }

        // box に存在しない個体が party に入っていないかチェック
        for (MonsterInstance inst : newParty) {
            if (!trainer.getBox().contains(inst)) {
                throw new IllegalArgumentException("手持ちには box の個体しか選べません");
            }
        }

        trainer.setParty(newParty);
        save();
    }

    /**
     * トレーナー一覧を CSV に保存する。
     */
    public void save() {
        repository.saveAll(trainers);
    }
}