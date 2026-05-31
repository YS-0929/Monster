package monster.dictionary.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import monster.dictionary.domain.Move;
import monster.dictionary.domain.MoveCategory;
import monster.dictionary.domain.StatChange;
import monster.dictionary.domain.StatType;
import monster.dictionary.domain.Status;
import monster.dictionary.domain.type.Type;
import monster.dictionary.service.MoveService;

/**
 * わざ図鑑のサブメニュー画面を制御するUIクラス。
 * <p>コンソールからの入力に応じて、登録されているすべての技の一覧表示、
 * および技名の一部による部分一致検索を行う。</p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class MoveUI {

    /** 技に関するビジネスロジックを処理するサービス */
    private final MoveService service;

    /**
     * コンストラクタ。
     *
     * @param service 技サービス
     */
    public MoveUI(MoveService service) {
        this.service = service;
    }

    /**
     * わざ図鑑のサブメニューを表示し、ユーザーからの入力を受け付けるループ処理を開始する。
     * <p>「0」が入力されるとループを抜け、呼び出し元のメインメニュー（PokedexUI）に戻る。</p>
     */
    public void start() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("=== わざ図鑑 ===");
            System.out.println("1. 一覧表示");
            System.out.println("2. 名前検索");
            System.out.println("3. ID検索");
            System.out.println("4. 新規登録");
            System.out.println("0. 戻る");
            System.out.print("選択: ");

            String input = sc.nextLine();

            switch (input) {
                case "1" -> showAll();
                case "2" -> searchByName(sc);
                case "3" -> showDetailByName(sc);
                case "4" -> register(sc);
                case "0" -> { return; }
                default -> System.out.println("無効な入力です。");
            }
        }
    }

    /**
     * 登録されているすべての技の名前とタイプをコンソールに一覧表示する。
     */
    private void showAll() {
        for (Move m : service.findAll()) {
            System.out.println(m.getName() + " (" + m.getType() + ")");
        }
    }

    /**
     * ユーザーに入力を促し、入力されたキーワードを名前に含む技を一覧表示する。
     *
     * @param sc コンソール入力を監視する Scanner オブジェクト
     */
    private void searchByName(Scanner sc) {
        System.out.print("名前の一部を入力: ");
        String keyword = sc.nextLine();

        for (Move m : service.searchByKeyword(keyword)) {
            System.out.println(m.getName() + " (" + m.getType() + ")");
        }
    }
    
    /**
     * ユーザーに技名の入力を促し、完全一致する技の詳細情報をコンソールに表示する。
     * <p>入力された技名をもとにサービスを介して検索を行い、該当する Move オブジェクトが存在すれば
     * showDetail メソッドを呼び出して詳細なパラメータを出力する。
     * 対象の技が見つからない場合は、エラーメッセージを表示して処理を終了する。</p>
     *
     * @param sc コンソール入力を監視する Scanner オブジェクト
     */
    private void showDetailByName(Scanner sc) {
        System.out.print("技名を入力: ");
        String name = sc.nextLine();

        Move m = service.findByName(name);
        if (m == null) {
            System.out.println("技が見つかりません。");
            return;
        }

        showDetail(m);
    }
    
    /**
     * コンソールからのインタラクティブな入力により、新しい技（わざ）を生成して図鑑に登録する。
     * <p>技名や各種ユーティリティメソッドを駆使した基本ステータス（タイプ、分類、威力、命中率、急所率、優先度）の入力から始まり、
     * 「コロン区切り（例: ATTACK:1）」の形式を用いた能力変化リスト（バフ・デバフ）の動的生成を行う。
     * さらに、状態異常（Status）とその発生確率の条件付き入力（NONE以外の場合のみ確率を入力）を経て、
     * 最終的に完全な Move オブジェクトを構築してサービスへと永続化する。</p>
     *
     * @param sc コンソール入力を監視する Scanner オブジェクト
     * @throws Exception ステータス変化のパース時に発生する配列の範囲外アクセスや数値変換エラー（内部で捕捉・処理される）
     */
    public void register(Scanner sc) {
        System.out.println("=== 技登録 ===");

        // --- 技名 ---
        System.out.print("技名: ");
        String name = sc.nextLine();

        // --- タイプ ---
        Type type = inputType(sc);

        // --- カテゴリ ---
        MoveCategory category = inputMoveCategory(sc);

        // --- 威力 ---
        int power = inputInt(sc, "威力（変化技は0）: ");

        // --- 命中 ---
        int accuracy = inputInt(sc, "命中（%）: ");

        // --- 急所率 ---
        double criticalRate = inputDouble(sc, "急所率（例: 0.1）: ");

        // --- 優先度 ---
        int priority = inputInt(sc, "優先度（通常は0）: ");

        // --- ステータス変化 ---
        List<StatChange> statChanges = new ArrayList<>();
        System.out.println("=== ステータス変化 ===");
        System.out.println("例: ATTACK:+1, DEFENSE:-1");
        System.out.println("空 Enter で終了");

        while (true) {
            System.out.print("ステータス変化: ");
            String input = sc.nextLine();
            if (input.isBlank()) {
                break;
            }

            try {
                String[] kv = input.split(":");
                StatType stat = StatType.valueOf(kv[0].toUpperCase());
                int stages = Integer.parseInt(kv[1]);
                statChanges.add(new StatChange(stat, stages));
            } catch (Exception e) {
                System.out.println("形式が正しくありません。例: ATTACK:+1");
            }
        }

        // --- 状態異常 ---
        System.out.println("=== 状態異常 ===");
        Status statusEffect = inputStatus(sc);

        double statusChance = 0.0;
        if (statusEffect != Status.NONE) {
            statusChance = inputDouble(sc, "状態異常の発生確率（0.0〜1.0）: ");
        }

        // すべての入力値を結合してドメインオブジェクトを生成
        Move move = new Move(
                name,
                type,
                category,
                power,
                accuracy,
                criticalRate,
                priority,
                statChanges,
                statusEffect,
                statusChance
        );

        service.register(move);
        System.out.println("登録しました！");
    }
    
    /**
     * 指定された技（Move ドメインオブジェクト）の詳細なパラメータをコンソールに美しくフォーマットして表示する。
     * <p>タイプや分類、基本性能（威力・命中など）だけでなく、能力変化リストや状態異常といった追加効果もセクションごとに分けて出力する。
     * 能力変化の出力時には、正の数値に対して視認性を高めるためのプラス記号（+）を動的に付与する制御を行う。</p>
     *
     * @param m 詳細を表示する対象の Move オブジェクト
     */
    private void showDetail(Move m) {
        System.out.println("=== 技の詳細 ===");
        System.out.println(m.getName());
        System.out.println();

        // --- タイプ ---
        System.out.println("【タイプ】");
        System.out.println(" - " + m.getType());
        System.out.println();

        // --- カテゴリ ---
        System.out.println("【分類】");
        System.out.println(" - " + m.getCategory());
        System.out.println();

        // --- 基本性能 ---
        System.out.println("【基本性能】");
        System.out.println("威力: " + m.getPower());
        System.out.println("命中: " + m.getAccuracy());
        System.out.println("急所率: " + m.getCriticalRate());
        System.out.println("優先度: " + m.getPriority());
        System.out.println();

        // --- ステータス変化 ---
        System.out.println("【ステータス変化】");
        if (m.getStatChanges().isEmpty()) {
            System.out.println("なし");
        } else {
            for (StatChange sc : m.getStatChanges()) {
                // 正の数値の場合のみ明示的に "+" 記号を付与する
                String sign = sc.getStages() > 0 ? "+" : "";
                System.out.println(" - " + sc.getStat() + " " + sign + sc.getStages());
            }
        }
        System.out.println();

        // --- 状態異常 ---
        System.out.println("【状態異常】");
        if (m.getStatusEffect() == Status.NONE) {
            System.out.println("なし");
        } else {
            System.out.println(" - " + m.getStatusEffect() + "（発生率 " + m.getStatusChance() + "）");
        }

        System.out.println();
    }
    
    /**
     * コンソールから安全に整数（int）を入力させるためのユーティリティメソッド。
     * <p>ユーザーが数値以外の文字列を入力した場合は、エラーメッセージを表示して入力を再試行させる。
     * 正しい整数値が入力されるまで無限ループで待機する。</p>
     *
     * @param sc      コンソール入力を監視する Scanner オブジェクト
     * @param message 入力を促す際にコンソールへ表示する案内メッセージ
     * @return ユーザーが入力した有効な整数値
     */
    private int inputInt(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("数字を入力してください。");
            }
        }
    }
    
    /**
     * コンソールから安全にモンスターのタイプ（Type 列挙型）を入力させるためのユーティリティメソッド。
     * <p>ユーザーが入力を小文字で行った場合でも自動的に大文字に変換してパースを試みる。
     * 存在しないタイプ名が入力された場合はエラーメッセージを表示して再試行を促し、
     * 何も入力せずに Enter が押された場合は入力処理の終了とみなして null を返す。</p>
     *
     * @param sc コンソール入力を監視する Scanner オブジェクト
     * @return 入力された文字列に対応する Type 列挙型、入力を終了した（空のEnterが押された）場合は null
     * @throws IllegalArgumentException 入力された文字列が Type 列挙型の定義のいずれとも一致しない場合（内部で捕捉される）
     */
    private Type inputType(Scanner sc) {
        while (true) {
            System.out.print("タイプを入力（終了=空 Enter）: ");
            String input = sc.nextLine();

            if (input.isBlank()) {
                return null;
            }

            try {
                return Type.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("存在しないタイプです。");
                System.out.println("有効なタイプ: " + java.util.Arrays.toString(Type.values()));
            }
        }
    }
    
    /**
     * コンソールから安全に技のカテゴリ（MoveCategory 列挙型）を入力させるためのユーティリティメソッド。
     * <p>ユーザーが入力した文字列（物理、特殊、変化に相当する識別子）を大文字に変換してパースを試みる。
     * 定義外の文字列が入力された場合は、エラーメッセージを表示して正しいカテゴリ名が入力されるまで再試行を促す。</p>
     *
     * @param sc コンソール入力を監視する Scanner オブジェクト
     * @return 入力された文字列に対応する MoveCategory 列挙型（PHYSICAL, SPECIAL, STATUS のいずれか）
     * @throws Exception 入力された文字列が MoveCategory 列挙型の定義のいずれとも一致しない場合（内部で捕捉される）
     */
    private MoveCategory inputMoveCategory(Scanner sc) {
        while (true) {
            System.out.print("カテゴリ（PHYSICAL / SPECIAL / STATUS）: ");
            String input = sc.nextLine();

            try {
                return MoveCategory.valueOf(input.toUpperCase());
            } catch (Exception e) {
                System.out.println("カテゴリが不正です。");
            }
        }
    }
    
    /**
     * コンソールから安全に状態異常（Status 列挙型）を入力させるためのユーティリティメソッド。
     * <p>ユーザーが入力した文字列を自動的に大文字に変換し、定義された状態異常の識別子とマッピングを行う。
     * 定義に存在しない文字列が入力された場合は、エラーメッセージを表示して再試行を促す。
     * 追加効果がない場合は「NONE」を入力させる設計となっている。</p>
     *
     * @param sc コンソール入力を監視する Scanner オブジェクト
     * @return 入力された文字列に対応する Status 列挙型
     * @throws Exception 入力された文字列が Status 列挙型の定義のいずれとも一致しない場合（内部で捕捉される）
     */
    private Status inputStatus(Scanner sc) {
        while (true) {
            System.out.print("状態異常（NONE / BURN / PARALYSIS / SLEEP / POISON / BAD_POISON / FREEZE / CONFUSION）: ");
            String input = sc.nextLine();

            try {
                return Status.valueOf(input.toUpperCase());
            } catch (Exception e) {
                System.out.println("状態異常が不正です。");
            }
        }
    }
    
    /**
     * コンソールから安全に浮動小数点数（double）を入力させるためのユーティリティメソッド。
     * <p>ユーザーが数値として解析できない文字列を入力した場合は、エラーメッセージを表示して入力を再試行させる。
     * 正しい浮動小数点数値が入力されるまで無限ループで待機する。</p>
     *
     * @param sc      コンソール入力を監視する Scanner オブジェクト
     * @param message 入力を促す際にコンソールへ表示する案内メッセージ
     * @return ユーザーが入力した有効な浮動小数点数値
     * @throws NumberFormatException 入力された文字列が double 型に変換できない場合（内部で捕捉される）
     */
    private double inputDouble(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine();

            try {
                return Double.parseDouble(input);
            } catch (Exception e) {
                System.out.println("数値を入力してください。");
            }
        }
    }
}