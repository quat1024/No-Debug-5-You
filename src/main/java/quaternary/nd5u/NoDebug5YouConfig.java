package quaternary.nd5u;

import net.minecraftforge.common.config.Config;


@Config(modid = NoDebug5You.MODID, name="NoDebug5You")
public class NoDebug5YouConfig {
	@Config.Comment("Should No Debug 5 You dump a list of class names rendering F3 debug information to the console? You'll see one log on game startup, and another when you log in (to catch a few more)")
	public static boolean dump = false;
	
	@Config.Comment("What mode should ND5U operate in?\n\nNOTHING: do not unsubscribe anything.\nALL: indiscriminately nuke all subscribers to RenderGameOverlayEvent.Text.\nWHITELIST: unsubscribe only event handlers in classes given in the \"list\" config option.\nBLACKLIST: unsubscribe all event handlers except ones in classes given in the \"list\" config option.")
	public static OperationMode operationMode = OperationMode.WHITELIST;
	
	@Config.Comment("The list of class names to use in WHITELIST or BLACKLIST mode.")
	public static String[] list = new String[]{
		//botania
		"vazkii.botania.client.core.handler.DebugHandler",
		//astral
		"hellfirepvp.astralsorcery.client.effect.EffectHandler",
		//dynamicsurroundings (don't ask me why the class is like this)
		"DiagnosticHandler{name=Diagnostics}",
		//journeymap
		"journeymap.client.forge.event.MiniMapOverlayHandler"	
	};
}
