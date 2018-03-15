package quaternary.nd5u;

import net.minecraftforge.common.config.Config;


@Config(modid = NoDebug5You.MODID, name="NoDebug5You")
public class NoDebug5YouConfig {
	@Config.Comment("Should No Debug 5 You dump a list of class names rendering F3 debug information to the console on game startup? (Look for a bunch of ====== in your log during postinit.)")
	public static boolean dump = true;
	
	//TODO nuke this
	@Config.Comment("Should No Debug 5 You spam your console with useless crap?")
	public static boolean debuggerino = true;
	
	//TODO Please rememebr to put this back on WHITELIST before releasing
	@Config.Comment("What mode should ND5U operate in?\n\nNOTHING: do not unsubscribe anything.\nALL: nuke all subscribers to RenderGameOverlayEvent.Text (will cause problems because this event has other purposes too!)\nWHITELIST: unsubscribe only event handlers in classes given in the \"list\" config option.\nBLACKLIST: unsubscribe all event handlers except ones in classes given in the \"list\" config option.")
	public static OperationMode operationMode = OperationMode.ALL;
	
	@Config.Comment("The list of class names to use in WHITELIST or BLACKLIST mode.")
	public static String[] list = new String[]{
		"vazkii.botania.client.core.handler.DebugHandler",
		"hellfirepvp.astralsorcery.client.effect.EffectHandler"
	};
}
