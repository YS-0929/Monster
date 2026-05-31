package monster.dictionary.service;

import java.util.ArrayList;
import java.util.List;

import monster.dictionary.domain.Move;

/**
 * 技図鑑のビジネスロジックを管理するサービスクラス（技図鑑 API）。
 * <p>起動時に Loader を用いてすべての技データをメモリ上にキャッシュし、
 * 検索、登録、削除などの操作を提供する。データの変更時には Saver を通じて永続化を行う。</p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class MoveService {

    /** 技データの永続化を担当するセーバー */
    private final MoveSaver saver;
    
    /** メモリ上にキャッシュされた技データのリスト */
    private final List<Move> moves;

    /**
     * コンストラクタ。
     * <p>初期化時にすべての技データをCSVファイルからメモリ上へ読み込む。
     * ローダーから返されるリストの不変性に依存しないよう、明示的に変更可能な ArrayList として保持する。</p>
     *
     * @param loader 技データのローダー
     * @param saver  技データのセーバー
     */
    public MoveService(MoveLoader loader, MoveSaver saver) {
        this.saver = saver;
        this.moves = new ArrayList<>(loader.loadAll());
    }

    /**
     * メモリ上にキャッシュされているすべての技データを取得する。
     *
     * @return すべての技のリスト（変更不可能）
     */
    public List<Move> findAll() {
        return List.copyOf(moves);
    }

    /**
     * 指定された名前を持つ技を完全一致で検索して返す。
     *
     * @param name 検索対象の技名（完全一致）
     * @return 該当する Move オブジェクト、見つからない場合は null
     */
    public Move findByName(String name) {
        return moves.stream()
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * 指定されたキーワードを名前に含む技を部分一致で検索して返す。
     *
     * @param keyword 検索する名前のキーワード（文字列）
     * @return 条件に合致する技のリスト
     */
    public List<Move> searchByKeyword(String keyword) {
        return moves.stream()
                .filter(m -> m.getName().contains(keyword))
                .toList();
    }

    /**
     * 新しい技を図鑑に登録する。
     * <p>メモリ上のリストに技を追加し、即座にCSVファイルへ上書き保存する。</p>
     *
     * @param move 登録する Move オブジェクト
     */
    public void register(Move move) {
        moves.add(move);
        saver.saveAll(moves);
    }

    /**
     * 指定された名前を持つ技を図鑑から削除する。
     * <p>メモリ上のリストから該当データを削除し、即座にCSVファイルへ上書き保存する。</p>
     *
     * @param name 削除対象の技名
     */
    public void delete(String name) {
        moves.removeIf(m -> m.getName().equals(name));
        saver.saveAll(moves);
    }
}