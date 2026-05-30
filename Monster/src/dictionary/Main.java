package dictionary;

import dictionary.controller.MonsdexUI;
import dictionary.repository.BaseStatsRepository;
import dictionary.repository.EvolutionRepository;
import dictionary.repository.LearnableMoveRepository;
import dictionary.repository.MonsterRepository;
import dictionary.repository.MonsterTypeRepository;
import dictionary.repository.MoveRepository;
import dictionary.service.EvolutionLoader;
import dictionary.service.MonsterLoader;
import dictionary.service.MonsterSaver;
import dictionary.service.MonsterService;
import dictionary.service.MoveLoader;
import dictionary.service.MoveSaver;
import dictionary.service.MoveService;

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