package app;

import app.controller.MainMenu;
import battle.controller.BattleMenuUI;
import battle.service.BattleService;
import monster.dictionary.controller.MonsdexUI;
import monster.dictionary.repository.BaseStatsRepository;
import monster.dictionary.repository.EvolutionRepository;
import monster.dictionary.repository.LearnableMoveRepository;
import monster.dictionary.repository.MonsterRepository;
import monster.dictionary.repository.MonsterTypeRepository;
import monster.dictionary.repository.MoveRepository;
import monster.dictionary.service.EvolutionLoader;
import monster.dictionary.service.EvolutionService;
import monster.dictionary.service.MonsterLoader;
import monster.dictionary.service.MonsterSaver;
import monster.dictionary.service.MonsterService;
import monster.dictionary.service.MoveLoader;
import monster.dictionary.service.MoveSaver;
import monster.dictionary.service.MoveService;
import monster.instance.controller.MonsterUI;
import monster.instance.repository.MonsterInstanceRepository;
import monster.instance.service.MonsterInstanceService;
import trainer.controller.TrainerUI;
import trainer.repository.TrainerRepository;
import trainer.service.TrainerService;

/**
 * アプリケーションのエントリーポイント。
 * <p>
 * すべての Repository / Loader / Service / UI を組み立て、
 * MainMenu を起動する。
 * </p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {

        // ============================
        // 1. 辞書データ（図鑑）ロード
        // ============================

    	// === Move 図鑑の構築 ===
        MoveRepository moveRepo = new MoveRepository();
        MoveLoader moveLoader = new MoveLoader(moveRepo);
        MoveSaver moveSaver = new MoveSaver(moveRepo);
        MoveService moveService = new MoveService(moveLoader, moveSaver);
        
        // === Monster 図鑑の構築 ===
        MonsterRepository monsterRepo = new MonsterRepository();
        BaseStatsRepository baseStatsRepo = new BaseStatsRepository();
        MonsterTypeRepository monsterTypeRepo = new MonsterTypeRepository();
        LearnableMoveRepository learnableMoveRepo = new LearnableMoveRepository();
        EvolutionRepository evolutionRepo = new EvolutionRepository();
        MonsterLoader monsterLoader = new MonsterLoader(monsterRepo, baseStatsRepo, monsterTypeRepo, learnableMoveRepo, moveLoader);
        EvolutionLoader evolutionLoader = new EvolutionLoader(evolutionRepo);
        MonsterSaver monsterSaver = new MonsterSaver(monsterRepo, baseStatsRepo, monsterTypeRepo, learnableMoveRepo);
        MonsterService monsterService = new MonsterService(monsterLoader, monsterSaver, evolutionLoader);
        EvolutionService evolutionService = new EvolutionService(evolutionLoader, monsterService);

        // ============================
        // 2. 個体データ（MonsterInstance）
        // ============================

        MonsterInstanceRepository instanceRepo =
                new MonsterInstanceRepository("data/instance.csv", monsterService, moveService);

        MonsterInstanceService instanceService =
                new MonsterInstanceService(instanceRepo, monsterService, moveService);

        MonsterUI monsterUI =
                new MonsterUI(instanceService, monsterService, moveService, evolutionService);

        // ============================
        // 3. トレーナー
        // ============================

        TrainerRepository trainerRepo =
                new TrainerRepository("data/trainer.csv", instanceService);

        TrainerService trainerService =
                new TrainerService(trainerRepo);

        TrainerUI trainerUI =
                new TrainerUI(trainerService, instanceService);

        // ============================
        // 4. 図鑑 UI
        // ============================

        MonsdexUI monsdexUI = new MonsdexUI(monsterService, moveService);
        
        // ============================
        // 5. バトル
        // ============================
        BattleService battleService = new BattleService();
        BattleMenuUI battleMenuUI = new BattleMenuUI(trainerService, battleService);

        // ============================
        // 5. メインメニュー起動
        // ============================

        MainMenu menu = new MainMenu(monsdexUI, monsterUI, trainerUI, battleMenuUI);
        menu.start();
    }
}