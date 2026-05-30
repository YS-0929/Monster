package dictionary.controller;

import java.util.Scanner;

import dictionary.service.MonsterService;
import dictionary.service.MoveService;

/**
 * 図鑑アプリケーション全体のメインメニュー画面を制御するUIクラス。
 * <p>コンソールからの入力に基づいて、モンスター図鑑のサブメニュー（MonsterUI）と
 * わざ図鑑のサブメニュー（MoveUI）の切り替えや、アプリケーションの終了を制御する。</p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class MonsdexUI {
	
	/** モンスター図鑑の画面制御UI */
    private final MonsterUI monsterUI;
    
    /** わざ図鑑の画面制御UI */
    private final MoveUI moveUI;

    /**
     * コンストラクタ。
     * <p>各サービスクラスを受け取り、対応するサブメニューUI（MonsterUI, MoveUI）を初期化する。</p>
     *
     * @param monsterService モンスターの処理を担当するサービス
     * @param moveService    技の処理を担当するサービス
     */
    public MonsdexUI(MonsterService monsterService, MoveService moveService) {
        this.monsterUI = new MonsterUI(monsterService, moveService); 
        this.moveUI = new MoveUI(moveService);
    }

    /**
     * メインメニュー画面を表示し、ユーザーからの入力を受け付けるループ処理を開始する。
     * <p>「0」が入力されるまで、サブメニューの呼び出しとメニューの再表示を繰り返す。</p>
     */
    public void start() {
        Scanner sc = new Scanner(System.in);

        while (true) {
        	System.out.println();
            System.out.println("=== 図鑑メニュー ===");
            System.out.println("1. モンスター図鑑");
            System.out.println("2. わざ図鑑");
            System.out.println("0. 終了");
            System.out.print("選択: ");

            String input = sc.nextLine();

            switch (input) {
                case "1" -> monsterUI.start();
                case "2" -> moveUI.start();
                case "0" -> {
                    System.out.println("終了します。");
                    return;
                }
                default -> System.out.println("無効な入力です。");
            }
        }
    }
}
