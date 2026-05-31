package monster.instance.service;

import java.util.ArrayList;
import java.util.List;

import monster.dictionary.domain.Monster;
import monster.dictionary.domain.Move;
import monster.dictionary.service.EvolutionService;
import monster.dictionary.service.MonsterService;
import monster.dictionary.service.MoveService;
import monster.instance.domain.MonsterInstance;
import monster.instance.domain.Nature;
import monster.instance.domain.Stats;
import monster.instance.repository.MonsterInstanceRepository;

/**
 * MonsterInstance（個体）の管理を行うサービスクラス。
 * <p>個体の生成、検索、削除、保存などの操作を提供し、
 * 永続化は MonsterInstanceRepository に委譲する。</p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class MonsterInstanceService {

    private final MonsterInstanceRepository repository;
    private final MonsterService monsterService;
    private final MoveService moveService;

    /** メモリ上の個体リスト */
    private final List<MonsterInstance> instances = new ArrayList<>();

    public MonsterInstanceService(
            MonsterInstanceRepository repository,
            MonsterService monsterService,
            MoveService moveService
    ) {
        this.repository = repository;
        this.monsterService = monsterService;
        this.moveService = moveService;

        // 起動時に CSV からロード
        this.instances.addAll(repository.loadAll());
    }

    /**
     * すべての個体を返す（変更不可）
     */
    public List<MonsterInstance> findAll() {
        return List.copyOf(instances);
    }

    /**
     * 新しい個体を生成して登録する。
     *
     * @param speciesId 種族ID
     * @param nickname ニックネーム
     * @param level レベル
     * @param nature 性格
     * @param iv 個体値
     * @param ev 努力値
     * @param moveNames 覚える技名のリスト
     * @return 生成された MonsterInstance
     */
    public MonsterInstance create(
            int speciesId,
            String nickname,
            int level,
            Nature nature,
            Stats iv,
            Stats ev,
            List<String> moveNames
    ) {
        Monster species = monsterService.findById(speciesId);
        if (species == null) {
            throw new IllegalArgumentException("種族IDが存在しません: " + speciesId);
        }

        List<Move> moves = new ArrayList<>();
        for (String name : moveNames) {
            Move move = moveService.findByName(name);
            if (move != null) moves.add(move);
        }

        MonsterInstance instance = new MonsterInstance(
                species,
                nickname,
                level,
                0,          // 初期経験値
                nature,
                iv,
                ev,
                moves
        );

        instances.add(instance);
        save(); // 永続化

        return instance;
    }

    /**
     * 個体を削除する。
     */
    public void delete(MonsterInstance instance) {
        instances.remove(instance);
        save();
    }

    /**
     * CSV に保存する。
     */
    public void save() {
        repository.saveAll(instances);
    }

    /**
     * 個体をIDで取得する（CSVの行番号ベース）
     */
    public MonsterInstance findByIndex(int index) {
        if (index < 0 || index >= instances.size()) return null;
        return instances.get(index);
    }

    /**
     * レベルアップ処理を行う。
     */
    public void gainExp(MonsterInstance instance, int exp) {
        instance.gainExp(exp);
        save();
    }

    /**
     * 進化処理を行う。
     *
     * @param instance 進化させたい個体
     * @param evolutionService 進化判定サービス
     */
    public void tryEvolve(MonsterInstance instance, EvolutionService evolutionService) {

        int speciesId = instance.getSpecies().getId();
        int level = instance.getLevel();

        // 進化可能か？
        if (!evolutionService.canEvolveByLevel(speciesId, level)) {
            return; // 進化不可
        }

        // 進化先を取得
        Monster next = evolutionService.getNextEvolutionByLevel(speciesId, level);
        if (next == null) {
            return; // 進化先なし
        }

        // 進化実行
        instance.evolve(next);

        // 永続化
        save();
    }
}