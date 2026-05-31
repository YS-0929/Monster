package monster.dictionary;

import monster.dictionary.controller.MonsdexUI;
import monster.dictionary.repository.BaseStatsRepository;
import monster.dictionary.repository.EvolutionRepository;
import monster.dictionary.repository.LearnableMoveRepository;
import monster.dictionary.repository.MonsterRepository;
import monster.dictionary.repository.MonsterTypeRepository;
import monster.dictionary.repository.MoveRepository;
import monster.dictionary.service.EvolutionLoader;
import monster.dictionary.service.MonsterLoader;
import monster.dictionary.service.MonsterSaver;
import monster.dictionary.service.MonsterService;
import monster.dictionary.service.MoveLoader;
import monster.dictionary.service.MoveSaver;
import monster.dictionary.service.MoveService;

/**
 * アプリケーションのエントリーポイントを定義するクラス。
 * <p>このクラスのmainメソッドからアプリケーションが起動されます。</p>
 *
 * @author Suzuki
 * @version 1.0
 */
public class Main {
		/**
		 * アプリケーションのエントリーポイント。
		 * <p>ここで必要なサービスクラスを初期化し、メインUIを起動するコードを記述します。</p>
		 * 
		 * @param args
		 */
		
		public static void main(String[] args) {
			
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

	        // === UI 起動 ===
	        MonsdexUI ui = new MonsdexUI(monsterService, moveService);
	        ui.start();
	}
}