package net.botwithus.gemcutter;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;

public class GemcutterUI extends ScriptGraphicsContext {

    private final Gemcutter script;

    public GemcutterUI(ScriptConsole scriptConsole, Gemcutter script) {
        super(scriptConsole);
        this.script = script;
    }

    @Override
    public void drawSettings() {
        if (ImGui.Begin("GemCutter3000", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("My bar", ImGuiWindowFlag.None.getValue())) {
                if (ImGui.BeginTabItem("Settings", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Ensure your last preset contains  any type of uncut gems.");
                    ImGui.Text("Bot state: " + script.getBotState());
                    if (ImGui.Button("Start Script")) {
                        script.println("Start script button selected.");
                        script.setBotState(script.botState.RUNNING);
                    }
                    ImGui.SameLine();
                    if (ImGui.Button("Stop Script")) {
                        script.println("Stop script button selected.");
                        script.setBotState(script.botState.IDLE);
                    }
                    ImGui.SameLine();
                    script.DebugScript = ImGui.Checkbox("Debugging", script.DebugScript);
                    ImGui.EndTabItem();
                }
                // if (ImGui.BeginTabItem("Other", ImGuiWindowFlag.None.getValue())) {
                //     script.setSomeBool(ImGui.Checkbox("Are you cool?", script.isSomeBool()));
                //     ImGui.EndTabItem();
                // }
                // ImGui.EndTabBar();
            }
            ImGui.End();
        }

    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }
}
