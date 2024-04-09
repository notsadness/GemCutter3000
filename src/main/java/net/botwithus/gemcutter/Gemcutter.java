package net.botwithus.gemcutter;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.configs.ConfigManager;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.util.RandomGenerator;
import net.botwithus.rs3.game.skills.Skills;

import java.util.Random;

public class Gemcutter extends LoopingScript {

    public BotState botState = BotState.IDLE;
    private boolean someBool = true;
    private Random random = new Random();
    public boolean DebugScript = false;
    public int bankcheck = 0;

    enum BotState {
        IDLE,
        RUNNING,
        BANKING,
        PROCESSING
    }

    public Gemcutter(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new GemcutterUI(getConsole(), this);
    }

    @Override
    public void onLoop() {
        // this.loopDelay = 500;
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN || botState == BotState.IDLE) {
            Execution.delay(random.nextLong(1750, 3000));
            return;
        }
        switch (botState) {
            case IDLE -> {
                println("IDLE");
                Execution.delay(random.nextLong(1000, 3000));
            }
            case RUNNING -> {
                println("RUNNING");
                Execution.delay(handleSkilling());
            }
            case BANKING -> {
                println("BANKING");
                Execution.delay(handleBanking());
            }
            case PROCESSING -> {
                Execution.delay(handleProcessing());
            }
        }
    }

    private long handleBanking() {
        Execution.delayUntil(30000, () -> Bank.loadLastPreset());
        ExecDelay();
        if (!Backpack.isEmpty()) {
            botState = BotState.RUNNING;
        } else {
            println("BANKING | No gems found, changing state to idle.");
            botState = BotState.IDLE;
        }
        return random.nextLong(500, 1353);
    }

    private long handleProcessing() {
        boolean processing = WaitForProcessing(1251); // Interface ID of the 'item production' interface
        if (!processing) {
            if (DebugScript) {
                println("HandleProcessing |  Waiting for interface to close");
            }
        } else {
            botState = BotState.BANKING;
        }
        return random.nextLong(450, 550);
    }

    private long handleSkilling() {
        if (Backpack.isEmpty() || !Backpack.containsItemByCategory(5289)) {
            println("handleSkilling | No Gems found! Banking.");
            botState = BotState.BANKING;
            return random.nextLong(250, 500);
        }
        ExecDelay();
        Component craftlog1 = ComponentQuery.newQuery(1473).itemCategory(5289).results().first();
        int gemid = craftlog1.getItemId();
        if (!CanCut(gemid)) {
            println("handleSkilling | You don't have the required level to cut this gem.");
            botState = BotState.IDLE;
            return random.nextLong(1230, 2132);
        } else {
            boolean craftsuccess = craftlog1.interact("Craft");
            ExecDelay();
            if (craftsuccess) {
                boolean execute = MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
                ExecDelay();
                if (execute) {
                    ExecDelay();
                    println("CraftGems | Cutting gems");
                    botState = BotState.PROCESSING;
                    return random.nextLong(400, 890);
                }
            }
        }
        println("CraftGems | Something went wrong");
        return random.nextLong(1230, 2132);
    }

    public boolean CanCut(int ItemId) {
        int itemlevel = ConfigManager.getItemType(ItemId).getIntParam(771);
        int craftinglevel = Skills.CRAFTING.getActualLevel();
        if (DebugScript) {
            println("CRAFTING LEVEL REQUIRED: " + itemlevel);
            println("CURRENT CRAFTING LEVEL REQUIRED: " + craftinglevel);
        }
        if (itemlevel <= craftinglevel) {
            if (DebugScript) {
                println("CanCut: true");
            }
            return true;
        } else {
            if (DebugScript) {
                println("CanCut: false");
            }
            return false;
        }
    }

    // helpers
    public boolean WaitForProcessing(int InterfaceId) {
        boolean completed = false;
        if (!completed) {
            boolean checkinterface = CheckInterface(InterfaceId);
            Execution.delay(500);
            if (!checkinterface) {
                completed = true;
                println("ActionCompleted | Interface has closed: " + InterfaceId);
                return true;
            }
        }
        return false;
    }

    public void ExecDelay() {
        int delay = RandomGenerator.nextInt(400, 800);
        Execution.delay(delay);
    }

    public boolean CheckInterface(int InterfaceId) {
        boolean IsOpen = Interfaces.isOpen(InterfaceId);
        return IsOpen;
    }

    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }

    public boolean isSomeBool() {
        return someBool;
    }

    public void setSomeBool(boolean someBool) {
        this.someBool = someBool;
    }
}