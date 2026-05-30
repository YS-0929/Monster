package dictionary.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dictionary.domain.Evolution;
import dictionary.domain.Monster;

/**
 * モンスター図鑑のビジネスロジックを管理するサービスクラス（図鑑 API）。
 * <p>起動時に Loader を用いてすべてのモンスターデータをメモリ上にキャッシュし、
 * 検索、登録、削除などの操作を提供する。データの変更時には Saver を通じて永続化を行う。</p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class MonsterService {
    
    /** モンスターデータの永続化を担当するセーバー */
    private final MonsterSaver saver;

    /** メモリ上にキャッシュされたモンスターデータのリスト */
    private final List<Monster> monsters;
    
    /** 進化データのローダー（将来的な進化機能のために保持） */
    private final Map<Integer, List<Evolution>> evolutionMap;

    /**
     * コンストラクタ。
     * <p>初期化時にすべてのモンスターデータをCSVファイルからメモリ上へ読み込む。</p>
     *
     * @param loader モンスターデータのローダー
     * @param saver  モンスターデータのセーバー
     * @param evolutionLoader 進化データのローダー（将来的な進化機能のために保持）
     */
    public MonsterService(MonsterLoader loader, MonsterSaver saver, EvolutionLoader evolutionLoader) {
        this.saver = saver;
        this.evolutionMap = evolutionLoader.loadAsMap();
        this.monsters = new ArrayList<>(loader.loadAll());
    }
    
    /**
     * メモリ上にキャッシュされているすべてのモンスターデータを取得する。
     *
     * @return すべてのモンスターのリスト（変更不可能）
     */
    public List<Monster> findAll() {
        return List.copyOf(monsters);
    }
    
    /**
     * 指定されたモンスターIDを起点とする進化データのリストを取得する。
     * <p>内部で保持している進化マップ（evolutionMap）を検索し、該当する進化先が存在すればそのリストを返す。
     * 分岐進化を持つモンスターの場合は複数のデータが含まれる。
     * 進化先が存在しない（最終進化形など）場合は、NullPointerException を防止するために空の不変リストを返す。</p>
     *
     * @param monsterId 進化元のモンスターID
     * @return 該当するモンスターの進化データのリスト。進化先がない場合は空のリスト（null は返らない）
     */
    public List<Evolution> getEvolutions(int monsterId) {
        return evolutionMap.getOrDefault(monsterId, List.of());
    }

    /**
     * 指定されたIDを持つモンスターを検索して返す。
     *
     * @param id 検索対象のモンスターID
     * @return 該当する Monster オブジェクト、見つからない場合は null
     */
    public Monster findById(int id) {
        return monsters.stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * 指定されたキーワードを名前に含むモンスターを部分一致で検索して返す。
     *
     * @param keyword 検索する名前のキーワード（文字列）
     * @return 条件に合致するモンスターのリスト
     */
    public List<Monster> searchByName(String keyword) {
        return monsters.stream()
                .filter(m -> m.getName().contains(keyword))
                .toList();
    }

    /**
     * 新しいモンスターを図鑑に登録する。
     * <p>メモリ上のリストにモンスターを追加し、即座にCSVファイルへ上書き保存する。</p>
     *
     * @param monster 登録するモンスターオブジェクト
     */
    public void register(Monster monster) {
        monsters.add(monster);
        saver.saveAll(monsters);
    }

    /**
     * 指定されたIDを持つモンスターを図鑑から削除する。
     * <p>メモリ上のリストから該当データを削除し、即座にCSVファイルへ上書き保存する。</p>
     *
     * @param id 削除対象のモンスターID
     */
    public void delete(int id) {
        monsters.removeIf(m -> m.getId() == id);
        saver.saveAll(monsters);
    }
}
