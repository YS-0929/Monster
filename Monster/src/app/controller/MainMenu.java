package app.controller;

import java.util.Scanner;

import battle.controller.BattleMenuUI;
import monster.dictionary.controller.MonsdexUI;
import monster.instance.controller.MonsterUI;
import trainer.controller.TrainerUI;

/**
 * アプリ全体のメインメニューを提供するクラス。
 * <p>
 * Monsdex（図鑑）、Monster（個体管理）、Trainer（トレーナー管理）
 * の各 UI を統合し、ユーザーが選択できるようにする。
 * </p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    private final MonsdexUI monsdexUI;
    private final MonsterUI monsterUI;
    private final TrainerUI trainerUI;
    private final BattleMenuUI battleMenuUI;

    /**
     * コンストラクタ。
     *
     * @param monsdexUI 図鑑 UI
     * @param monsterUI 個体管理 UI
     * @param trainerUI トレーナー管理 UI
     */
    public MainMenu(
            MonsdexUI monsdexUI,
            MonsterUI monsterUI,
            TrainerUI trainerUI,
            BattleMenuUI battleMenuUI
    ) {
        this.monsdexUI = monsdexUI;
        this.monsterUI = monsterUI;
        this.trainerUI = trainerUI;
        this.battleMenuUI = battleMenuUI;
    }

    /**
     * メインメニューを表示し、ユーザー操作を受け付ける。
     */
    public void start() {
        while (true) {
            System.out.println("=== Main Menu ===");
            System.out.println("1. 図鑑（Monsdex）");
            System.out.println("2. モンスター個体管理");
            System.out.println("3. トレーナー管理");
            System.out.println("4. バトル開始");
            System.out.println("0. 終了");
            System.out.print("選択: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> monsdexUI.start();
                case 2 -> monsterUI.start();
                case 3 -> trainerUI.start();
                case 4 -> battleMenuUI.start();
                case 0 -> {
                    System.out.println("終了します。");
                    return;
                }
                default -> System.out.println("無効な入力です。");
            }
        }
    }

    /**
     * 数字入力を安全に読み取る。
     *
     * @return 入力された整数
     */
    private int readInt() {
        while (true) {
            try {
                String s = scanner.nextLine();
                return Integer.parseInt(s);
            } catch (Exception e) {
                System.out.print("数字を入力してください: ");
            }
        }
    }
}